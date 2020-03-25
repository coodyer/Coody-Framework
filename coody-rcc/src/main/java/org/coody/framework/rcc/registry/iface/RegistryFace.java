package org.coody.framework.rcc.registry.iface;

import java.util.List;

import org.coody.framework.rcc.entity.RccInstance;

public interface RegistryFace {

	/**
	 * 根据方法唯一标识获得所有实例
	 * 
	 * @param methodKey 方法唯一标识
	 * @return
	 */
	public List<RccInstance> getRccInstances(String methodKey);

	/**
	 * 获取某台主机所注册的所有服务
	 * @param host 主机
	 * @param port 端口
	 * @return
	 */
	public List<String> getMethods(String host, Integer port);

	/**
	 * 根据方法分配实例
	 * @param methodKey 方法唯一标识
	 * @return
	 */
	public RccInstance getRccInstance(String methodKey);

	/**
	 *  注册服务
	 * @param methodKey 方法唯一标识
	 * @param host 主机
	 * @param port 端口
	 * @param pr 分配权重
	 * @return
	 */
	public boolean register(String methodKey, String host, Integer port, Integer pr);
}
