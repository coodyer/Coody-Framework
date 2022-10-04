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
	 * 
	 * @throws Exception
	 */
	public byte[] send(String path, byte[] data) throws Exception {

		RccInstance rcc = registry.getRccInstance(path);

		RccSignalerEntity rccSignalerEntity = new RccSignalerEntity();
		rccSignalerEntity.setRcc(rcc);
		rccSignalerEntity.setData(data);

		rccSignalerEntity = RccKeepInstance.signaler.doConsume(rccSignalerEntity);
		if (rccSignalerEntity.getException() != null) {
			throw rccSignalerEntity.getException();
		}
		return rccSignalerEntity.getData();
	}

}
