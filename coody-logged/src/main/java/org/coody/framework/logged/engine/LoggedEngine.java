package org.coody.framework.logged.engine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.coody.framework.logged.config.LoggedConfig;
import org.coody.framework.logged.constant.LevelConstant;
import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.entity.LoggedWriteEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;
import org.coody.framework.logged.function.invoke.FunctionInvoker;
import org.coody.framework.logged.io.LogWriter;
import org.coody.framework.logged.util.ExpressionUtil;

public class LoggedEngine {

	private List<FunctionInvoker> invokers = new ArrayList<FunctionInvoker>();

	private static final String PARAMETER_PATTEN = "\\$\\{.+?\\}";

	public LoggedEngine(String logged) {
		List<String> parameters = ExpressionUtil.getParameters(logged, PARAMETER_PATTEN);

		for (String line : parameters) {
			Integer index = logged.indexOf(line);
			if (index > 0) {
				String part = logged.substring(0, index);
				FunctionInvoker invoker = new FunctionInvoker();
				invoker.setParameter(part);
				invoker.setFunction(LoggedFunction.get("APPEND"));
				invokers.add(invoker);
			}
			String name = ExpressionUtil.center(line, "\\{", "\\}");
			String parameter = null;
			if (name.contains("(")) {
				parameter = ExpressionUtil.center(line, "\\(", "\\)");
				name = name.substring(0, name.indexOf("("));
			}
			FunctionInvoker invoker = new FunctionInvoker();
			invoker.setParameter(parameter);
			invoker.setFunction(LoggedFunction.get(name.trim()));
			invokers.add(invoker);

			logged = logged.substring(index + line.length());
		}
	}

	private void write(String msg, String level, Throwable ex, Object... parameter) {

		if (LoggedConfig.level.equals(LevelConstant.ERROR)) {
			if (!level.equals(LevelConstant.ERROR)) {
				return;
			}
		}
		if (LoggedConfig.level.equals(LevelConstant.INFO)) {
			if (level.equals(LevelConstant.DEBUG)) {
				return;
			}
		}
		msg = String.format(msg, parameter);

		LoggedEntity entity = new LoggedEntity();
		entity.setLevel(level);
		entity.setMsg(msg);

		StringBuilder log = new StringBuilder();
		for (FunctionInvoker invoker : invokers) {
			log.append(invoker.getFunction().invoke(invoker.getParameter(), entity));
		}
		if (ex != null) {
			log.append("\r\n");
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(output);
				ex.printStackTrace(printStream);
				log.append(output.toString());
				output.close();
				printStream.close();
			} catch (Exception e) {
				log.append(e.toString());
			}
		}
		
		log.append("\r\n");
		String file = LoggedConfig.outOfDebug;
		if (level.equals(LevelConstant.INFO)) {
			file = LoggedConfig.outOfInfo;
		}
		if (level.equals(LevelConstant.ERROR)) {
			file = LoggedConfig.outOfError;
		}

		LoggedWriteEntity line = new LoggedWriteEntity();
		line.setFile(file);
		line.setLevel(level);
		line.setMsg(log.toString());

		LogWriter.offer(line);
	}

	public void debug(String msg, Object... parameter) {
		write(msg, LevelConstant.DEBUG, null, parameter);
	}

	public void error(String msg, Object... parameter) {
		write(msg, LevelConstant.ERROR, null, parameter);
	}

	public void info(String msg, Object... parameter) {
		write(msg, LevelConstant.INFO, null, parameter);
	}

	public void error(String msg, Throwable ex, Object... parameter) {
		write(msg, LevelConstant.ERROR, ex, parameter);
	}

}
