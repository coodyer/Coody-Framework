package org.coody.framework.esource.wrapper;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.coody.framework.esource.config.EConfig;

public abstract class DataSourceWrapper extends EConfig implements DataSource {

	protected PrintWriter writer;

	public DataSourceWrapper() {

	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return writer;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.writer = out;
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

}
