package org.coody.framework.minicat.http;

import java.util.Arrays;

import org.coody.framework.core.util.StringUtil;

public class MultipartFile {

	private String paramName;
	
	private String fileName;
	
	private byte[] fileContext;
	
	private String suffix;
	
	private String contextType;
	
	
	
	
	

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.suffix=getSuffix(fileName);
	}

	public byte[] getFileContext() {
		return fileContext;
	}

	public void setFileContext(byte[] fileContext) {
		this.fileContext = fileContext;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	private String getSuffix(String fileName) {
		if (StringUtil.isNullOrEmpty(fileName)) {
			return null;
		}
		String[] strs = fileName.split("\\.");
		return strs[strs.length - 1].toLowerCase();
	}

	@Override
	public String toString() {
		return "MultipartFile [paramName=" + paramName + ", fileName=" + fileName + ", fileContext="
				+ Arrays.toString(fileContext) + ", suffix=" + suffix + ", contextType=" + contextType + "]";
	}

	
}
