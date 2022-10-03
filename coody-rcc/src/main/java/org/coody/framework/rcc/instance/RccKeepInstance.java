package org.coody.framework.rcc.instance;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.serialer.iface.RccSerialer;
import org.coody.framework.rcc.signal.iface.RccSignaler;

@AutoBuild
public class RccKeepInstance implements InitBeanFace {

	/**
	 * 调用其他服务使用的序列化工具
	 */
	public static RccSerialer serialer;

	/**
	 * 通信工具
	 */
	public static RccSignaler signaler;

	@Override
	public void init() throws Exception {
		serialer = RccConfig.serialer.newInstance();
		
		signaler = RccConfig.signaler.newInstance();
		
	}

}
