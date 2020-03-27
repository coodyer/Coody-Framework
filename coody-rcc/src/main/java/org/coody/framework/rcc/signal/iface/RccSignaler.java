package org.coody.framework.rcc.signal.iface;

import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.entity.RccSignalerEntity;

public interface RccSignaler {

	/**
	 * 进行服务
	 */
	void doService(RccConfig config);

	/**
	 * 进行消费
	 */
	byte[] doConsume(RccConfig config,RccSignalerEntity signaler);
}
