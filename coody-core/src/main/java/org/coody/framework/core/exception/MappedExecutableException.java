package org.coody.framework.core.exception;

import java.util.Collection;

import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.util.CommonUtil;

@SuppressWarnings("serial")
public class MappedExecutableException extends CoodyException {

	public MappedExecutableException(Class<?> clazz, Collection<String> parameters) {
		super("未找到匹配的构造函数>>" + clazz + " build from (" + CommonUtil.splicing(parameters, ",") + ")");
	}
}
