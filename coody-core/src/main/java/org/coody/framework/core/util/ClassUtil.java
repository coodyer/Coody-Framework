package org.coody.framework.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.coody.framework.core.threadpool.ThreadBlockPool;

/**
 * Copy From Internet
 */
public class ClassUtil {

	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	public static boolean isCglibProxyClassName(String className) {
		return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
	}

	public static Class<?> getSourceClass(Class<?> clazz) {
		if (ClassUtil.isCglibProxyClassName(clazz.getName())) {
			clazz = clazz.getSuperclass();
			return getSourceClass(clazz);
		}
		return clazz;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack 包名
	 */
	public static Set<Class<?>> getClasses(String pack) {
		Set<String> clazzNameSet = getClasseNames(pack);
		if (clazzNameSet.isEmpty()) {
			return null;
		}

		CopyOnWriteArraySet<Class<?>> queue = new CopyOnWriteArraySet<Class<?>>();
		ThreadBlockPool pool = new ThreadBlockPool(100, 7200);
		for (String clazzName : clazzNameSet) {
			pool.pushTask(new Runnable() {
				@Override
				public void run() {
					try {
						Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzName);
						queue.add(clazz);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.execute();
		return queue;
	}

	public static Set<String> getClasseNames(String pack) {
		Set<String> classes = new LinkedHashSet<String>();
		boolean recursive = true;
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equalsIgnoreCase(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
					continue;
				}
				if (!"jar".equalsIgnoreCase(protocol)) {
					continue;
				}
				try {
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if (name.charAt(0) == '/') {
							name = name.substring(1);
						}
						if (!name.startsWith(packageDirName)) {
							continue;
						}
						int idx = name.lastIndexOf('/');
						if (idx != -1) {
							packageName = name.substring(0, idx).replace('/', '.');
						}
						if (!((idx != -1) || recursive)) {
							continue;
						}
						if (entry.isDirectory()) {
							continue;
						}
						if (name.endsWith(".class")) {
							String className = name.substring(packageName.length() + 1, name.length() - 6);
							classes.add(packageName + '.' + className);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName 包名
	 * @param packagePath 包路径
	 * @param recursive   是否递归
	 * @param classes     类
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<String> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
				continue;
			}
			String className = file.getName().substring(0, file.getName().length() - 6);
			classes.add(packageName + '.' + className);
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Set<Class<?>> set = getClasses("org.coody.framework");
		System.out.println(set.size());
		System.out.println(System.currentTimeMillis() - start);
	}
}
