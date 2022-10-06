package org.coody.framework.logged.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoggedEntity  implements Serializable{

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
