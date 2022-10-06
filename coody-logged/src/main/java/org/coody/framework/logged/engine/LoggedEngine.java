package org.coody.framework.logged.engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

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

	public LoggedEngine() {

		tryLoadConfig();
		if (LoggedConfig.functions != null && LoggedConfig.functions.trim().length() > 0) {
			String[] functions = LoggedConfig.functions.split(",");
			for (String line : functions) {
				try {
					Class<?> clazz = Class.forName(line.trim());
					LoggedFunction function = (LoggedFunction) clazz.newInstance();
					LoggedFunction.register(function.getName(), function);
				} catch (Exception e) {
				}
			}
		}
		LoggedConfig.level = LoggedConfig.level.toUpperCase();

		String logged = LoggedConfig.pattern;

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

	private void tryLoadConfig() {
		try {
			InputStream inputStream = LoggedEngine.class.getClassLoader().getResourceAsStream("coody.properties");
			if (inputStream == null) {
				return;
			}
			Properties properties = new Properties();
			properties.load(inputStream);

			Field[] fields = LoggedConfig.class.getDeclaredFields();

			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = properties.getProperty(key);
				if (key == null || (value == null || value.trim().length() == 0)) {
					value = "";
				}
				for (Field field : fields) {
					if (Modifier.isFinal(field.getModifiers())) {
						continue;
					}
					String configField = LoggedConfig.prefix + "." + field.getName();

					if (!configField.equals(key)) {
						continue;
					}
					field.setAccessible(true);
					if (Integer.class.isAssignableFrom(field.getType())) {
						field.set(new LoggedConfig(), Integer.valueOf(value.trim()));
						continue;
					}
					if (Boolean.class.isAssignableFrom(field.getType())) {
						field.set(new LoggedConfig(), Boolean.valueOf(value.trim()));
						continue;
					}

					field.set(new LoggedConfig(), value.trim());
					continue;
				}
			}
			return;
		} catch (Exception e) {
			// TODO: handle exception
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
		LoggedEntity entity = new LoggedEntity();
		entity.setLevel(level);
		if (msg != null) {
			msg = String.format(msg, parameter);
			entity.setMsg(msg);
		}

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

	public void error(Throwable ex) {
		write(null, LevelConstant.ERROR, null, new Object[] {});
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
