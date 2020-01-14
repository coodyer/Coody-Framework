package org.coody.framework.core.util.log;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.coody.framework.core.threadpool.SysThreadPool;

public class LogUtil {

	public static Logger log = new LoggerWrapper(LogUtil.class.getName());

	static {
		SysThreadPool.THREAD_POOL.execute(new Runnable() {

			@Override
			public void run() {
				Logger log4j = Logger.getLogger(LogUtil.class);

				ConcurrentLinkedQueue<LoggerInvoke> queue = ((LoggerWrapper) log).getQueue();
				log = log4j;

				Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();
				while (!queue.isEmpty()) {
					LoggerInvoke invoke = queue.poll();
					if (invoke == null) {
						continue;
					}
					try {

						Method method = methodMap.get(invoke.getMethodKey());
						if (method == null) {
							method = Category.class.getDeclaredMethod(invoke.getMethod(),
									invoke.getTypes().toArray(new Class<?>[] {}));
							methodMap.put(invoke.getMethodKey(), method);
						}
						if (method == null) {
							continue;
						}
						method.invoke(log4j, invoke.getParameters().toArray());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private static class LoggerWrapper extends Logger {

		protected LoggerWrapper(String name) {
			super(name);
		}

		public void debug(Object message) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("debug");
			invoke.getTypes().add(Object.class);
			invoke.getParameters().add(message);
			queue.offer(invoke);
		}

		public void debug(Object message, Throwable t) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("debug");
			invoke.getTypes().add(Object.class);
			invoke.getTypes().add(Throwable.class);
			invoke.getParameters().add(message);
			invoke.getParameters().add(t);
			queue.offer(invoke);
		}

		public void error(Object message) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("error");
			invoke.getTypes().add(Object.class);
			invoke.getParameters().add(message);
			queue.offer(invoke);
		}

		public void error(Object message, Throwable t) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("error");
			invoke.getTypes().add(Object.class);
			invoke.getTypes().add(Throwable.class);
			invoke.getParameters().add(message);
			invoke.getParameters().add(t);
			queue.offer(invoke);
		}

		public void info(Object message) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("info");
			invoke.getTypes().add(Object.class);
			invoke.getParameters().add(message);
			queue.offer(invoke);
		}

		public void info(Object message, Throwable t) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("info");
			invoke.getTypes().add(Object.class);
			invoke.getTypes().add(Throwable.class);
			invoke.getParameters().add(message);
			invoke.getParameters().add(t);
			queue.offer(invoke);
		}

		public void warn(Object message) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("warn");
			invoke.getTypes().add(Object.class);
			invoke.getParameters().add(message);
			queue.offer(invoke);
		}

		public void warn(Object message, Throwable t) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("warn");
			invoke.getTypes().add(Object.class);
			invoke.getTypes().add(Throwable.class);
			invoke.getParameters().add(message);
			invoke.getParameters().add(t);
			queue.offer(invoke);
		}

		public void fatal(Object message) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("fatal");
			invoke.getTypes().add(Object.class);
			invoke.getParameters().add(message);
			queue.offer(invoke);
		}

		public void fatal(Object message, Throwable t) {
			LoggerInvoke invoke = new LoggerInvoke();
			invoke.setMethod("fatal");
			invoke.getTypes().add(Object.class);
			invoke.getTypes().add(Throwable.class);
			invoke.getParameters().add(message);
			invoke.getParameters().add(t);
			queue.offer(invoke);
		}

		private ConcurrentLinkedQueue<LoggerInvoke> queue = new ConcurrentLinkedQueue<LoggerInvoke>();

		public ConcurrentLinkedQueue<LoggerInvoke> getQueue() {
			return queue;
		}
	}

	@SuppressWarnings("serial")
	private static class LoggerInvoke implements Serializable {

		private String method;

		private List<Class<?>> types = new ArrayList<Class<?>>();

		private List<Object> parameters = new ArrayList<Object>();

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public List<Class<?>> getTypes() {
			return types;
		}

		public List<Object> getParameters() {
			return parameters;
		}

		public String getMethodKey() {
			StringBuilder key = new StringBuilder(this.getMethod());
			for (Class<?> type : types) {
				key.append(type.getName());
			}
			return key.toString();
		}
	}

}
