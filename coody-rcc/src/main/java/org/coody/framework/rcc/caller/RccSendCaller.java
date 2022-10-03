package org.coody.framework.rcc.caller;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.rcc.entity.RccInstance;
import org.coody.framework.rcc.entity.RccSignalerEntity;
import org.coody.framework.rcc.instance.RccKeepInstance;
import org.coody.framework.rcc.registry.iface.RccRegistry;

@AutoBuild
public class RccSendCaller {

	@AutoBuild
	RccRegistry registry;

	/**
	 * 调用远程方法
	 */
	public byte[] send(String methodKey, byte[] data) {

		RccInstance rcc = registry.getRccInstance(methodKey);

		RccSignalerEntity rccSignalerEntity = new RccSignalerEntity();
		rccSignalerEntity.setRcc(rcc);
		rccSignalerEntity.setData(data);

		return RccKeepInstance.signaler.doConsume(rccSignalerEntity);
	}

}
