package org.coody.framework.rcc.entity;

import java.util.Objects;

import org.coody.framework.core.model.BaseModel;

@SuppressWarnings("serial")
public class RccInstance extends BaseModel {

	/**
	 * ip地址
	 */
	private String host;
	/**
	 * 端口
	 */
	private Integer port;
	/**
	 * 权重
	 */
	private Integer pr;
	
	/**
	 * path
	 */
	private String path;

	public Integer getPr() {
		return pr;
	}

	public void setPr(Integer pr) {
		this.pr = pr;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port, pr);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RccInstance other = (RccInstance) obj;
		return Objects.equals(host, other.host) && Objects.equals(port, other.port) && Objects.equals(pr, other.pr);
	}

}
