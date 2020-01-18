package org.coody.framework.esource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
				while (true) {
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
				return;
			}
			if (!connection.getAutoCommit()) {
				connection.getSource().close();
				poolSize.decrementAndGet();
				return;
			}
			connection.setActiveTime(System.currentTimeMillis());
			idledDeque.push(connection);
			return;
		} catch (Exception e) {
			throw new ESourceCloseException("关闭连接出错", e);
		} finally {
			if (idledDeque.size() < getMinPoolSize()) {
				needCreate.getAndSet(true);
			}
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
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(this.getUrl(), username, password);
	}

}
