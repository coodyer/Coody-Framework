package org.coody.framework.esource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.esource.exception.ESourceCloseException;
import org.coody.framework.esource.exception.ESourceCreateConnectionException;
import org.coody.framework.esource.exception.ESourceException;
import org.coody.framework.esource.exception.ESourceWaitTimeOutException;
import org.coody.framework.esource.pool.GuardThreadPool;
import org.coody.framework.esource.wrapper.ConnectionWrapper;
import org.coody.framework.esource.wrapper.DataSourceWrapper;

public class ESource extends DataSourceWrapper {

	/**
	 * 创建则空闲
	 */
	private final LinkedBlockingDeque<ConnectionWrapper> idledDeque = new LinkedBlockingDeque<ConnectionWrapper>();

	public static AtomicInteger poolSize = new AtomicInteger(0);

	private boolean inited = false;

	private AtomicBoolean needCreate = new AtomicBoolean(true);

	/**
	 * 累计创建连接
	 */
	public static AtomicLong created = new AtomicLong();
	/**
	 * 累计使用次数
	 */
	public static AtomicLong used = new AtomicLong();

	/**
	 * 累计回收连接
	 */
	public static AtomicLong recoveryed = new AtomicLong();

	/**
	 * 累计关闭连接
	 */
	public static AtomicLong closed = new AtomicLong();

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
		GuardThreadPool.ESOURCE_CREATE_POOL.execute(new Runnable() {
			@Override
			public void run() {
				long m = 0;
				while (true) {
					m++;
					try {
						if (poolSize.longValue() >= getMaxPoolSize()) {
							sleep(TimeUnit.MILLISECONDS, 1);
							continue;
						}
						if (!needCreate.get()) {
							sleep(TimeUnit.MILLISECONDS, 1);
							continue;

						}
						needCreate.getAndSet(false);
						if (idledDeque.size() >= getMinPoolSize()) {
							continue;
						}
						for (int i = 0; i < getInitialPoolSize(); i++) {
							if (poolSize.longValue() > getMaxPoolSize()) {
								break;
							}
							createConnection();
						}
					} catch (Exception e) {
						LogUtil.log.error("创建连接数出错", e);
						sleep(TimeUnit.MILLISECONDS, 1);
					} finally {
						if (m > 1000) {
							LogUtil.log.error("当前空闲:" + idledDeque.size() + ">>" + "累计创建:" + created + ";累计使用:" + used
									+ ";累计关闭:" + closed + ";累计回收:" + recoveryed);
							m = 0;
						}
					}

				}
			}
		});

	}

	private void sleep(TimeUnit unit, int offset) {
		try {
			unit.sleep(offset);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ConnectionWrapper createConnection() {
		try {
			Connection source = DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword());
			ConnectionWrapper connection = new ConnectionWrapper(source, this);
			idledDeque.offer(connection);
			poolSize.getAndIncrement();

			created.getAndIncrement();
			return connection;
		} catch (Exception e) {
			throw new ESourceCreateConnectionException("创建连接失败", e);
		}
	}

	// 关闭连接
	public void close(ConnectionWrapper connection) {
		try {
			if (connection.isClosed()) {
				poolSize.decrementAndGet();
				if (idledDeque.size() < getMinPoolSize()) {
					needCreate.getAndSet(true);
				}
				closed.getAndIncrement();
				return;
			}
			if (!connection.getAutoCommit()) {
				connection.getSource().close();
				poolSize.decrementAndGet();
				if (idledDeque.size() < getMinPoolSize()) {
					needCreate.getAndSet(true);
				}
				closed.getAndIncrement();
				return;
			}
			connection.setActiveTime(System.currentTimeMillis());
			recoveryed.getAndIncrement();
			idledDeque.push(connection);
			return;
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
			if (idledDeque.isEmpty()) {
				needCreate.getAndSet(true);
			}
			while (true) {
				connection = idledDeque.poll(this.getMaxWaitTime(), TimeUnit.MILLISECONDS);
				if (connection == null) {
					throw new ESourceWaitTimeOutException("等待连接超时");
				}
				return connection;
			}
		} catch (Exception e) {
			throw new ESourceException("获取连接出错", e);
		} finally {
			used.getAndIncrement();
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(this.getUrl(), username, password);
	}

}
