package org.coody.framework.jdbc.entity;


public class JDBCEntity {

	private String sql;
	
	private Object[]parameters;
	
	public JDBCEntity(String sql,Object[] parameters){
		this.sql=sql;
		this.parameters=parameters;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	
	
}
