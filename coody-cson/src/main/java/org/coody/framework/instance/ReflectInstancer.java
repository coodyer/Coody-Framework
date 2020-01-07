package org.coody.framework.instance;

import org.coody.framework.exception.CsonException;
import org.coody.framework.instance.iface.CsonInstancer;

public class ReflectInstancer implements CsonInstancer {

	@Override
	public <T> T createInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new CsonException("对象创建失败", e);
		}

	}

}
