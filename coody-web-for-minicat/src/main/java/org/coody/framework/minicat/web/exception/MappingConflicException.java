package org.coody.framework.minicat.web.exception;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class MappingConflicException extends CoodyException{


	public MappingConflicException(String path) {
		super("Mapping地址已存在 >>" + path);
	}


	public MappingConflicException(String path, Exception e) {
		super("Mapping地址已存在 >>" + path, e);
	}
}
