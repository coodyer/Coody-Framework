package org.coody.framework.rcc.caller;

import org.apache.zookeeper.ZooKeeper;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.rcc.entity.RccInstance;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.registry.ZkRegistry;
import org.coody.framework.rcc.registry.iface.RccRegistry;

@AutoBuild
public class RccSendCaller implements InitBeanFace {

	RccRegistry registry;

	/**
	 * 调用远程方法
	 */
	public byte[] send(String methodKey, byte[] params) {
		RccInstance rcc = registry.getRccInstance(methodKey);
		return null;
	}

	@Override
	public void init() throws Exception {
		if (registry == null) {
			ZooKeeper zooKeeper = BeanContainer.getBean(ZooKeeper.class);
			if (zooKeeper == null) {
				throw new RccException("no zooKeeper config");
			}
			registry = new ZkRegistry().setZookeeper(zooKeeper);
		}
	}
}
