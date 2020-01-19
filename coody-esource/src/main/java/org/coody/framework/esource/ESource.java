package org.coody.framework.esource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.esource.exception.ESourceCloseException;
import org.coody.framework.esource.exception.ESourceCreateConnectionException;
import org.coody.framework.esource.exception.ESourceException;
import org.coody.framework.esource.pool.GuardThreadPool;
import org.coody.framework.esource.wrapper.ConnectionWrapper;
import org.coody.framework.esource.wrapper.DataSourceWrapper;

/**
 * @1、创建连接优先使用回收线程，每次回收记录回收次数
 * @2、空闲连接低于阀值时创建指定连接数
 * @3、当工作线程小于空闲线程 且回收线程大于工作线程时释放回收池连接
 * @4、回收连接如果提前关闭，触发vaild连接校验
 */
public class ESource extends DataSourceWrapper {

	/**
	 * 空闲
	 */
	private final LinkedBlockingDeque<ConnectionWrapper> IDLED_DEQUE = new LinkedBlockingDeque<ConnectionWrapper>();

	/**
	 * 工作
	 */
	private final LinkedBlockingQueue<ConnectionWrapper> WORK_QUEUE = new LinkedBlockingQueue<ConnectionWrapper>();

	/**
	 * 回收
	 */
	private final LinkedBlockingDeque<ConnectionWrapper> RECOVERY_DEQUE = new LinkedBlockingDeque<ConnectionWrapper>();

	/**
	 * 创建连接任务
	 */
	private final LinkedBlockingQueue<Object> CREATE_TASK_QUEUE = new LinkedBlockingQueue<Object>(1);

	/**
	 * 校验连接任务
	 */
	private final LinkedBlockingQueue<Object> VAILD_TASK_QUEUE = new LinkedBlockingQueue<Object>(1);

	private AtomicInteger poolSize = new AtomicInteger();

	private boolean inited = false;

	public ESource() {
		super();
	}

	private synchronized void init() {
		if (inited) {
			return;
		}
		doVerificat();
		try {
			Class.forName(getDriver());
		} catch (Exception e) {
			throw new ESourceCreateConnectionException("加载驱动失败", e);
		}
		inited = true;
		/**
		 * 创建连接
		 */
		GuardThreadPool.ESOURCE_CREATE_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						CREATE_TASK_QUEUE.take();
						if (IDLED_DEQUE.size() >= getMinPoolSize()) {
							continue;
						}
						for (int i = 0; i < getInitialPoolSize(); i++) {
							if (poolSize.intValue() >= getMaxPoolSize()) {
								break;
							}
							if (!activationRecovery()) {
								makeConnection();
							}
						}
					} catch (Exception e) {
						LogUtil.log.error("创建连接数出错", e);
						sleep(TimeUnit.MILLISECONDS, 1);
					}

				}
			}
		});

		/**
		 * 校验连接
		 */
		GuardThreadPool.ESOURCE_CREATE_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						VAILD_TASK_QUEUE.take();
						/**
						 * 校验空闲连接
						 */
						for (ConnectionWrapper connection : IDLED_DEQUE) {
							if (connection.isClosed() || !connection.isValid(getMaxWaitTime())) {
								if (!IDLED_DEQUE.remove(connection)) {
									continue;
								}
								connection.clearAndClose();
								poolSize.getAndDecrement();
							}
						}
						/**
						 * 校验回收连接
						 */
						for (ConnectionWrapper connection : RECOVERY_DEQUE) {
							if (connection.isClosed() || !connection.isValid(getMaxWaitTime())) {
								if (!RECOVERY_DEQUE.remove(connection)) {
									continue;
								}
								connection.clearAndClose();
								poolSize.getAndDecrement();
							}
						}
					} catch (Exception e) {
						LogUtil.log.error("校验连接出错", e);
						sleep(TimeUnit.MILLISECONDS, 1);
					}
				}
			}
		});
		/**
		 * 回收连接
		 */
		GuardThreadPool.ESOURCE_GUARD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						LogUtil.log.debug("当前连接数>>" + (WORK_QUEUE.size() + IDLED_DEQUE.size() + RECOVERY_DEQUE.size()));
						sleep(TimeUnit.SECONDS, 1);
						/**
						 * 回收连接
						 */
						for (ConnectionWrapper connection : IDLED_DEQUE) {
							if (System.currentTimeMillis() - connection.getActiveTime() <= getMaxIdleTime()) {
								continue;
							}
							if (!IDLED_DEQUE.remove(connection)) {
								continue;
							}
							LogUtil.log.debug("回收连接>>" + connection);
							RECOVERY_DEQUE.offer(connection);
						}
						if (WORK_QUEUE.size() >= IDLED_DEQUE.size() || IDLED_DEQUE.size() < getMinPoolSize()) {
							continue;
						}
						/**
						 * 释放资源
						 */
						for (ConnectionWrapper connection : RECOVERY_DEQUE) {
							if (connection == null) {
								continue;
							}
							if (System.currentTimeMillis() - connection.getActiveTime() <= getMaxIdleTime()) {
								continue;
							}
							if (!RECOVERY_DEQUE.remove(connection)) {
								continue;
							}
							LogUtil.log.debug("释放连接>>" + connection);
							connection.clearAndClose();
							poolSize.getAndDecrement();

						}
					} catch (Exception e) {
						LogUtil.log.error("释放连接出错", e);
						sleep(TimeUnit.MILLISECONDS, 1);
					}

				}
			}
		});
	}

	@SuppressWarnings("resource")
	public boolean activationRecovery() throws SQLException {
		ConnectionWrapper connection = RECOVERY_DEQUE.poll();
		while (connection != null && !connection.isValid(getMaxWaitTime())) {
			connection.clearAndClose();
			poolSize.getAndDecrement();
			connection = RECOVERY_DEQUE.poll();
		}
		if (connection == null) {
			return false;
		}
		if (!RECOVERY_DEQUE.remove(connection)) {
			return false;
		}
		IDLED_DEQUE.offer(connection);
		return true;
	}

	private void sleep(TimeUnit unit, int offset) {
		try {
			unit.sleep(offset);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ConnectionWrapper makeConnection() {
		try {
			Connection source = DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword());
			ConnectionWrapper connection = new ConnectionWrapper(source, this);
			poolSize.getAndIncrement();
			IDLED_DEQUE.offerFirst(connection);
			return connection;
		} catch (Exception e) {
			throw new ESourceCreateConnectionException("创建连接失败", e);
		}
	}

	/**
	 * 关闭连接
	 */
	public void close(ConnectionWrapper connection) {
		try {
			if (!WORK_QUEUE.remove(connection)) {
				throw new ESourceCloseException("关闭连接出错");
			}
			if (connection.isClosed()) {
				poolSize.getAndDecrement();
				/**
				 * 触发存活检测
				 */
				VAILD_TASK_QUEUE.offer(new Object());
				return;
			}
			connection.setAutoCommit(false);
			connection.setActiveTime(System.currentTimeMillis());
			IDLED_DEQUE.offer(connection);
			return;
		} catch (Exception e) {
			throw new ESourceCloseException("关闭连接出错", e);
		} finally {
			if (IDLED_DEQUE.size() < getMinPoolSize()) {
				/**
				 * 触发连接创建
				 */
				CREATE_TASK_QUEUE.offer(new Object());
			}
		}
	}

	@Override
	public Connection getConnection() {
		if (!inited) {
			init();
		}
		ConnectionWrapper connection = null;
		if (!IDLED_DEQUE.isEmpty()) {
			connection = RECOVERY_DEQUE.poll();
		} else {
			/**
			 * 触发连接创建
			 */
			CREATE_TASK_QUEUE.offer(new Object());
		}
		if (connection != null) {
			WORK_QUEUE.offer(connection);
			return connection;
		}
		if (!RECOVERY_DEQUE.isEmpty()) {
			connection = RECOVERY_DEQUE.poll();
		}
		if (connection != null) {
			WORK_QUEUE.offer(connection);
			return connection;
		}
		try {
			connection = IDLED_DEQUE.poll(this.getMaxWaitTime(), TimeUnit.MILLISECONDS);
			if (connection == null) {
				throw new ESourceException("获取连接超时");
			}
			WORK_QUEUE.offer(connection);
			return connection;
		} catch (InterruptedException e) {
			throw new ESourceException("获取连接出错", e);
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(this.getUrl(), username, password);
	}

}
