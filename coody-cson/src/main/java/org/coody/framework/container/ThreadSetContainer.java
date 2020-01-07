package org.coody.framework.container;

import java.util.HashSet;

public class ThreadSetContainer {

	private static ThreadLocal<HashSet<Object>> THREAD_LOCAL = new ThreadLocal<HashSet<Object>>();

	public static void clear() {
		THREAD_LOCAL.remove();
	}

	public static void initThreadContainer() {
		if (THREAD_LOCAL.get() != null) {
			return;
		}
		THREAD_LOCAL.set(new HashSet<Object>());
	}

	public static boolean add(Object value) {
		initThreadContainer();
		return THREAD_LOCAL.get().add(value);
	}

}
