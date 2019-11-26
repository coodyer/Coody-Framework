package org.coody.framework.jdbc.entity;

import java.util.Map;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;

@SuppressWarnings("serial")
public class DBDataBiller extends BaseModel {

	/**
	 * 表名
	 */
	private String table;

	/**
	 * 字段列表 key代表字段 value代表数据库列名
	 */
	private Map<FieldEntity, String> fields;


	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Map<FieldEntity, String> getFields() {
		return fields;
	}

	public void setFields(Map<FieldEntity, String> fields) {
		this.fields = fields;
	}

}
