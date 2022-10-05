package org.coody.framework.logged.main;

import org.coody.framework.logged.config.LoggedConfig;
import org.coody.framework.logged.engine.LoggedEngine;

public class Tested {

	public static void main(String[] args) {
		LoggedConfig.outOfDebug = "d://debug.log";

		LoggedConfig.outOfInfo = "d://info.log";

		LoggedConfig.outOfError = "d://info.log";

		LoggedConfig.sysout = true;

		String patten = "[${LEVEL} ${TIME(yyyy-MM-dd HH:mm:ss:SSS)} ${THREAD} ](${SIMPLESTACK}) ：${MSG}";

		LoggedEngine engine = new LoggedEngine(patten);

		for (int i = 0; i < 10; i++) {

			engine.debug("测试日志，参数一：%s", "参数2内容");
		}
	}

}
