package org.coody.framework.esource.config;

import org.coody.framework.esource.exception.ESourceVerificatException;

public class EConfig {

	// db url
	private String url;

	// db user
	private String user;

	// db password
	private String password;

	// driver驱动
	private String driver;

	// 初始化连接数，默认为5
	private int initialPoolSize = 5;

	// 最大连接数，默认为64
	private int maxPoolSize = 64;

	// 最小连接数，默认为2
	private int minPoolSize = 2;

	// 获取连接最大等待时长，默认为30s
	private int maxWaitTime = 30 * 1000;

	// 最大空闲回收时长，默认为60s
	private int maxIdleTime = 60 * 1000;

	public EConfig() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public int getInitialPoolSize() {
		return initialPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public void doVerificat() {
		if (url == null || "".equals(url.trim())) {
			throw new ESourceVerificatException("url is empty");
		}
		if (user == null || "".equals(user.trim())) {
			throw new ESourceVerificatException("user is empty");
		}
		if (driver == null || "".equals(driver.trim())) {
			throw new ESourceVerificatException("driver is empty");
		}
	}

}
