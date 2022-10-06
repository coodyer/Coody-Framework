package org.coody.framework.logged.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoggedWriteEntity implements Serializable {

	private String file;

	private String msg;

	private String level;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
