package org.coody.framework.mvc.exception;

import org.coody.framework.core.exception.base.IcopException;

@SuppressWarnings("serial")
public class MappingConflicException extends IcopException{


	public MappingConflicException(String path) {
		super("Mapping地址已存在:" + path);
	}


	public MappingConflicException(String path, Exception e) {
		super("Mapping地址已存在:" + path, e);
	}
}
