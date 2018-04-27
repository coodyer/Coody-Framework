package org.coody.framework.exception;

@SuppressWarnings("serial")
public class MappingConflicException extends IcopException{


	public MappingConflicException(String path) {
		super("Mapping地址已存在:" + path);
	}


	public MappingConflicException(String path, Exception e) {
		super("Mapping地址已存在:" + path, e);
	}
}
