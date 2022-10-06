package org.coody.framework.logged.function;

import org.coody.framework.logged.config.LoggedConfig;
import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class SimpleStackFunction extends LoggedFunction {

	@Override
	public String invoke(String parameter, LoggedEntity logged) {
		Integer index = 0;
		if (!(parameter == null || parameter.trim().length() == 0)) {
			index = Integer.valueOf(parameter.trim());
		}
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		String line = stacks[index + LoggedConfig.stack].toString();

		return line.substring(line.indexOf("(") + 1, line.indexOf(")"));
	}

	@Override
	public String getName() {
		return "SIMPLESTACK";
	}

}
