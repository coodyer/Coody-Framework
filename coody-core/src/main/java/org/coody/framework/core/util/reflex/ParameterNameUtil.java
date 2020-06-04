package org.coody.framework.core.util.reflex;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.coody.framework.core.util.asm.AsmClassReader;
import org.coody.framework.core.util.log.LogUtil;
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

	private static final LinkedBlockingQueue<Class<?>> EXECUTABLE_QUEUE = new LinkedBlockingQueue<Class<?>>();

	static {
		// 启动队列守护线程，用于加速
		Thread executableQueueThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Class<?> clazz = null;
					try {
						clazz = EXECUTABLE_QUEUE.take();
						getExecutableParameters(clazz);
					} catch (Exception e) {
						LogUtil.log.error("预加载方法参数失败>>" + clazz, e);
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
			return AsmClassReader.getAsmClassReader(declaringClass);
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
			super(Opcodes.ASM5);
			Set<Executable> executables = getExecutables(clazz);
			for (Executable executable : executables) {
				if (executable instanceof Method) {
					executableMap.put(executable.getName() + "." + Type.getMethodDescriptor((Method) executable),
							executable);
					continue;
				}
				Type[] types = new Type[executable.getParameterTypes().length];
				for (int j = 0; j < types.length; j++) {
					types[j] = Type.getType(executable.getParameterTypes()[j]);
				}
				String name = "<init>";
				executableMap.put(name + "." + Type.getConstructorDescriptor((Constructor<?>) executable), executable);
			}
		}

		private Set<Executable> getExecutables(Class<?> clazz) {
			if (clazz == Object.class) {
				return null;
			}
			Set<Executable> executables = new HashSet<Executable>();
			for (Method method : clazz.getDeclaredMethods()) {
				executables.add(method);
			}
			for (Constructor<?> constructor : clazz.getConstructors()) {
				executables.add(constructor);
			}
			if (clazz.getSuperclass() != null) {
				Set<Executable> childerExecutables = getExecutables(clazz.getSuperclass());
				if (!(childerExecutables == null || childerExecutables.isEmpty())) {
					executables.addAll(childerExecutables);
				}
			}
			return executables;
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
				Executable executable = executableMap.get(name + "." + desc);
				if (executable == null) {
					return null;
				}
				parameterNames = new ArrayList<String>(executable.getParameterTypes().length);
				parameterNames.addAll(Collections.<String>nCopies(executable.getParameterTypes().length, null));
				executableParameters.put(executable, parameterNames);
				return new MethodVisitor(Opcodes.ASM5) {
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