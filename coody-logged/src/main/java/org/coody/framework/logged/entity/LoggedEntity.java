package org.coody.framework.logged.entity;

import org.coody.framework.core.model.BaseModel;

@SuppressWarnings("serial")
public class LoggedEntity extends BaseModel {

	private String level;

	private String msg;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
