package org.coody.framework.esource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.coody.framework.esource.exception.ESourceCloseException;
import org.coody.framework.esource.exception.ESourceCreateConnectionException;
import org.coody.framework.esource.exception.ESourceException;
import org.coody.framework.esource.exception.ESourceWaitTimeOutException;
import org.coody.framework.esource.pool.GuardThreadPool;
import org.coody.framework.esource.wrapper.ConnectionWrapper;
import org.coody.framework.esource.wrapper.DataSourceWrapper;

public class ESource extends DataSourceWrapper {

	/**
	 * 取出即工作
	 */
	private final LinkedBlockingQueue<ConnectionWrapper> workQueue = new LinkedBlockingQueue<ConnectionWrapper>();
	/**
	 * 创建则空闲
	 */
	private final LinkedBlockingDeque<ConnectionWrapper> idledDeque = new LinkedBlockingDeque<ConnectionWrapper>();
	/**
	 * close 无事务则回收
	 */
	private final LinkedBlockingDeque<ConnectionWrapper> recoveryDeque = new LinkedBlockingDeque<ConnectionWrapper>();

	public static AtomicInteger poolSize = new AtomicInteger(0);

	private boolean inited = false;

	private AtomicBoolean needCreate = new AtomicBoolean(true);

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
		GuardThreadPool.THREAD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if (poolSize.longValue() >= getMaxPoolSize()) {
							TimeUnit.MILLISECONDS.sleep(1);
							continue;
						}
						if (needCreate.get()) {
							needCreate.getAndSet(false);
							if (idledDeque.size() < getMinPoolSize()) {
								for (int i = 0; i < getInitialPoolSize(); i++) {
									if (poolSize.longValue() > getMaxPoolSize()) {
										break;
									}
									createConnection();
								}
							}
						} else {
							TimeUnit.MILLISECONDS.sleep(1);
						}
					} catch (Exception e) {
					}
				}
			}
		});
		GuardThreadPool.THREAD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						TimeUnit.MILLISECONDS.sleep(getMaxIdleTime() / 5);
						for (ConnectionWrapper connection : idledDeque) {
							if (connection.getStatus() != 0) {
								continue;
							}
							if (System.currentTimeMillis() - connection.getCreateTime() <= getMaxIdleTime()) {
								continue;
							}
							if (connection.statusAdd(1) != 1) {
								continue;
							}
							idledDeque.remove(connection);
							recoveryDeque.push(connection);
						}
					} catch (Exception e) {
					}
				}
			}
		});
		inited = true;
	}

	private ConnectionWrapper createConnection() {
		try {
			Connection source = DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword());
			ConnectionWrapper connection = new ConnectionWrapper(source, this);
			idledDeque.offer(connection);
			poolSize.getAndIncrement();
			return connection;
		} catch (Exception e) {
			throw new ESourceCreateConnectionException("创建连接失败", e);
		}
	}

	// 关闭连接
	public boolean close(ConnectionWrapper connection) {
		try {
			if (!connection.getAutoCommit()) {
				workQueue.remove(connection);
				connection.getSource().close();
				poolSize.decrementAndGet();
				if (idledDeque.size() < getMinPoolSize()) {
					needCreate.getAndSet(true);
				}
				return true;
			}
			recoveryDeque.push(connection);
			return workQueue.remove(connection);
		} catch (Exception e) {
			throw new ESourceCloseException("关闭连接出错", e);
		}
	}

	@Override
	public Connection getConnection() {
		if (!inited) {
			init();
		}
		ConnectionWrapper connection = null;
		try {
			if (!recoveryDeque.isEmpty()) {
				connection = recoveryDeque.poll();
				while (connection != null && connection.isClosed()) {
					poolSize.decrementAndGet();
					connection = recoveryDeque.poll();
				}
				if (connection != null) {
					return connection;
				}
			}
			while (true) {
				connection = idledDeque.poll(this.getMaxWaitTime(), TimeUnit.MILLISECONDS);
				if (connection == null) {
					throw new ESourceWaitTimeOutException("等待连接超时");
				}
				if (connection.statusAdd(1) == 1) {
					return connection;
				}
			}
		} catch (Exception e) {
			throw new ESourceException("获取连接出错", e);
		} finally {
			if (connection != null) {
				workQueue.offer(connection);
			}
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(this.getUrl(), username, password);
	}

}
