package org.coody.framework.loader.base;

import java.util.Set;

public interface IcopLoader {

	/***
	 * 进行加载
	 * @param clazzs
	 * @throws Exception
	 */
	public void doLoader(Set<Class<?>> clazzs) throws Exception;
}
