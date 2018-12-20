package org.coody.framework.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 参数名操作工具
 * 
 * @author Coody
 *
 *         2018年12月20日
 * 
 * @blog 54sb.org
 */
public class ParameterNameUtil {

	private static final Map<Class<?>, Map<Executable, List<String>>> classExecutableCache = new HashMap<Class<?>, Map<Executable, List<String>>>();

	private static final ConcurrentLinkedQueue<Class<?>> EXECUTABLE_QUEUE = new ConcurrentLinkedQueue<Class<?>>();

	static {
		//启动队列守护线程，用于加速
		Thread executableQueueThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						TimeUnit.MILLISECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Class<?> clazz = EXECUTABLE_QUEUE.poll();
					while (clazz != null) {
						try {
							getExecutableParameters(clazz);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							clazz = EXECUTABLE_QUEUE.poll();
						}
					}
				}
			}
		});
		executableQueueThread.start();
	}

	public static void doExecutable(Class<?> clazz) {
		EXECUTABLE_QUEUE.add(clazz);
	}

	/**
	 * 加载指定方法的参数名
	 * 
	 * @param executable
	 * @return
	 */
	public static List<String> getParameters(Executable executable) {
		Class<?> clazz = PropertUtil.getClass(executable);
		Map<Executable, List<String>> executableParameters = getExecutableParameters(clazz);
		return executableParameters.get(executable);
	}

	/**
	 * 加载所有方法和参数
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<Executable, List<String>> getExecutableParameters(Class<?> clazz) {
		Map<Executable, List<String>> executableParameters = classExecutableCache.get(clazz);
		if (executableParameters != null) {
			return executableParameters;
		}
		executableParameters = new HashMap<Executable, List<String>>();
		try {
			ClassReader reader = ParameterNameUtil.createClassReader(clazz);

			ParameterNameUtil.ExecutableParameterNameVisitor visitor = new ParameterNameUtil.ExecutableParameterNameVisitor(
					clazz);
			reader.accept(visitor, 0);

			Map<String, Exception> exceptions = visitor.getExceptions();
			if (exceptions.size() == 1) {
				throw new RuntimeException((Exception) exceptions.values().iterator().next());
			}
			if (!exceptions.isEmpty()) {
				throw new RuntimeException(exceptions.toString());
			}

			executableParameters = visitor.getExecutableParameters();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		classExecutableCache.put(clazz, executableParameters);
		return executableParameters;
	}

	private static ClassReader createClassReader(Class<?> declaringClass) throws IOException {
		InputStream in = null;
		try {
			ClassLoader classLoader = declaringClass.getClassLoader();
			in = classLoader.getResourceAsStream(declaringClass.getName().replace('.', '/') + ".class");
			ClassReader reader = new ClassReader(in);
			return reader;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static class ExecutableParameterNameVisitor extends ClassVisitor {

		private final Map<Executable, List<String>> executableParameters = new HashMap<Executable, List<String>>();
		private final Map<String, Exception> exceptions = new HashMap<String, Exception>();
		private final Map<String, Executable> executableMap = new HashMap<String, Executable>();

		public ExecutableParameterNameVisitor(Class<?> clazz) {
			super(Opcodes.ASM7);
			List<Constructor<?>> constructors = new ArrayList<Constructor<?>>(
					Arrays.<Constructor<?>>asList(clazz.getConstructors()));
			constructors.addAll(Arrays.asList(clazz.getDeclaredConstructors()));
			for (Constructor<?> constructor : constructors) {
				Type[] types = new Type[constructor.getParameterTypes().length];
				for (int j = 0; j < types.length; j++) {
					types[j] = Type.getType(constructor.getParameterTypes()[j]);
				}
				executableMap.put(constructor.getName() + Type.getMethodDescriptor(Type.VOID_TYPE, types), constructor);
			}
			List<Method> methods = new ArrayList<Method>(Arrays.asList(clazz.getMethods()));
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			for (Method method : methods) {
				executableMap.put(method.getName() + Type.getMethodDescriptor(method), method);
			}
		}

		public Map<Executable, List<String>> getExecutableParameters() {
			return executableParameters;
		}

		public Map<String, Exception> getExceptions() {
			return exceptions;
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			try {
				final List<String> parameterNames;
				Executable executable = executableMap.get(name + desc);
				if (executable == null) {
					return null;
				}
				parameterNames = new ArrayList<String>(executable.getParameterTypes().length);
				parameterNames.addAll(Collections.<String>nCopies(executable.getParameterTypes().length, null));
				executableParameters.put(executable, parameterNames);
				return new MethodVisitor(Opcodes.ASM7) {
					public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
							int index) {
						if (Modifier.isStatic(executable.getModifiers())) {
							if (index > parameterNames.size() - 1) {
								return;
							}
							parameterNames.set(index, name);
							return;
						}
						if (index > 0) {
							if (index > parameterNames.size()) {
								return;
							}
							parameterNames.set(index - 1, name);
							return;
						}
					}
				};
			} catch (Exception e) {
				this.exceptions.put(signature, e);
			}
			return null;
		}
	}
}