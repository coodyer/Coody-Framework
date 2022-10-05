package org.coody.framework.logged.function.iface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.exception.LoggedException;
import org.coody.framework.logged.function.AppendFunction;
import org.coody.framework.logged.function.LevelFunction;
import org.coody.framework.logged.function.MsgFunction;
import org.coody.framework.logged.function.SimpleStackFunction;
import org.coody.framework.logged.function.StackFunction;
import org.coody.framework.logged.function.ThreadFunction;
import org.coody.framework.logged.function.TimeFunction;

public abstract class LoggedFunction {

	private static final Map<String, LoggedFunction> FUNCTION_MAP = new ConcurrentHashMap<String, LoggedFunction>();

	/**
	 * Bean表达式
	 */

	public abstract String invoke(String parameter, LoggedEntity logged);

	static {
		LoggedFunction.register("APPEND", new AppendFunction());
		LoggedFunction.register("LEVEL", new LevelFunction());
		LoggedFunction.register("SIMPLESTACK", new SimpleStackFunction());
		LoggedFunction.register("STACK", new StackFunction());
		LoggedFunction.register("THREAD", new ThreadFunction());
		LoggedFunction.register("TIME", new TimeFunction());
		LoggedFunction.register("MSG", new MsgFunction());
	}

	public static void register(String name, LoggedFunction function) {
		name = name.toUpperCase();
		if (FUNCTION_MAP.containsKey(name)) {
			throw new LoggedException("存在相同的函数名->" + name);
		}
		FUNCTION_MAP.put(name, function);
	}

	public static LoggedFunction get(String name) {
		name = name.toUpperCase();
		return FUNCTION_MAP.get(name);
	}
}
