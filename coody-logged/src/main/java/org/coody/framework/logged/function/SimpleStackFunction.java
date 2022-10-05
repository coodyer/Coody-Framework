package org.coody.framework.logged.function;

import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.exception.LoggedException;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class SimpleStackFunction extends LoggedFunction {

	@Override
	public String invoke(String parameter, LoggedEntity logged) {
		Integer index = 0;
		if (!CommonUtil.isNullOrEmpty(parameter)) {
			index = Integer.valueOf(parameter.trim());
		}
		if (index > 0) {
			throw new LoggedException("堆栈索引不能大于 0");
		}
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		String line = stacks[stacks.length - 1 + index].toString();

		return line.substring(line.indexOf("(") + 1, line.indexOf(")"));
	}

}
