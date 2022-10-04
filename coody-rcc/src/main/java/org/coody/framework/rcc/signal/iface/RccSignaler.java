package org.coody.framework.rcc.signal.iface;

import org.coody.framework.rcc.entity.RccSignalerEntity;

public interface RccSignaler {

	/**
	 * 进行服务
	 */
	void doService(int port);

	/**
	 * 进行消费
	 */
	RccSignalerEntity doConsume(RccSignalerEntity signaler);
}
