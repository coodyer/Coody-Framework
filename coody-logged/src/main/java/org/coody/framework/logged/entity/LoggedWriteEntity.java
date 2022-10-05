package org.coody.framework.logged.entity;

import org.coody.framework.core.model.BaseModel;

@SuppressWarnings("serial")
public class LoggedWriteEntity extends BaseModel {

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
