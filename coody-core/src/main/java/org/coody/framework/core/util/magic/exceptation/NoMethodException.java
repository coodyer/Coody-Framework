package org.coody.framework.core.util.magic.exceptation;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class NoMethodException extends CoodyException{


	public NoMethodException(Class<?> clazz) {
		super("未找到接口方法 >>" + clazz.getName());
	}


	public NoMethodException(Class<?> clazz, Exception e) {
		super("未找到接口方法 >>" + clazz.getName(), e);
	}
}
