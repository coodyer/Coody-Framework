package org.coody.framework.rcc.instance;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.serialer.iface.RccSerialer;
import org.coody.framework.rcc.signal.iface.RccSignaler;

@AutoBuild
public class RccKeepInstance {

	/**
	 * 调用其他服务使用的序列化工具
	 */
	public static RccSerialer serialer;

	/**
	 * 通信工具
	 */
	public static RccSignaler signaler;

	public RccKeepInstance() {
		try {
			serialer = RccConfig.serialer.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			signaler = RccConfig.signaler.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
