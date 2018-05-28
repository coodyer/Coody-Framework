package org.coody.framework.exception;

@SuppressWarnings("serial")
public class NoMethodException extends IcopException{


	public NoMethodException(Class<?> clazz) {
		super("未找到接口方法:" + clazz.getName());
	}


	public NoMethodException(Class<?> clazz, Exception e) {
		super("未找到接口方法:" + clazz.getName(), e);
	}
}
