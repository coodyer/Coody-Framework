package org.coody.framework.tester;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UserInfo {

	private LocalDateTime localDateTime;

	private int code;

	private Integer id;

	private String email;

	private String password;

	private ResCodeEnum resCode;
	private Integer status;

	private Date createTime;

	private String[] attrs;

	private List<String> list;

	private SettingInfo setting;

	private Map<Object, Object> map;

	private Boolean isAllow;

	public Boolean getIsAllow() {
		return isAllow;
	}

	public void setIsAllow(Boolean isAllow) {
		this.isAllow = isAllow;
	}

	public ResCodeEnum getResCode() {
		return resCode;
	}

	public void setResCode(ResCodeEnum resCode) {
		this.resCode = resCode;
	}

	public Map<Object, Object> getMap() {
		return map;
	}

	public void setMap(Map<Object, Object> map) {
		this.map = map;
	}

	public SettingInfo getSetting() {
		return setting;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public void setSetting(SettingInfo setting) {
		this.setting = setting;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String[] getAttrs() {
		return attrs;
	}

	public void setAttrs(String[] attrs) {
		this.attrs = attrs;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
