package org.coody.framework.rcc.config;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.rcc.registry.iface.RccRegistry;
import org.coody.framework.rcc.serialer.iface.RccSerialer;

@SuppressWarnings("serial")
public class RccConfig extends BaseModel {

	/**
	 * 服务者线程数
	 */
	public static int serverThread = 100;
	/**
	 * 消费者线程数
	 */
	public static int consumeThread = 100;

	/**
	 * 本地host
	 */
	private String host;
	/**
	 * 本服务端口
	 */
	private Integer port;
	/**
	 * 本机权重
	 */
	private Integer pr;
	/**
	 * 调用其他服务的超时时间
	 */
	private Integer expire;
	/**
	 * 调用其他服务的重试次数
	 */
	private Integer retry;
	/**
	 * 调用其他服务使用的序列化工具
	 */
	private Class<? extends RccSerialer> serialer;
	/**
	 * 注册中心
	 */
	private Class<? extends RccRegistry> registry;

	public Integer getPr() {
		return pr;
	}

	public void setPr(Integer pr) {
		this.pr = pr;
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

	public Integer getExpire() {
		return expire;
	}

	public void setExpire(Integer expire) {
		this.expire = expire;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public Class<? extends RccSerialer> getSerialer() {
		return serialer;
	}

	public void setSerialer(Class<? extends RccSerialer> serialer) {
		this.serialer = serialer;
	}

	public Class<? extends RccRegistry> getRegistry() {
		return registry;
	}

	public void setRegistry(Class<? extends RccRegistry> registry) {
		this.registry = registry;
	}

}
