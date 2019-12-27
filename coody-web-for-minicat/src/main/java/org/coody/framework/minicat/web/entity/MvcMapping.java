package org.coody.framework.minicat.web.entity;

import java.lang.reflect.Method;
import java.util.List;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.minicat.web.adapter.iface.CoodyParameterAdapter;

@SuppressWarnings("serial")
public class MvcMapping extends BaseModel {

	private String path;

	private Method method;

	private Object bean;

	private List<FieldEntity> parameters;

	private CoodyParameterAdapter paramsAdapt;

	private Boolean isAntPath = false;

	public Boolean getIsAntPath() {
		return isAntPath;
	}

	public void setIsAntPath(Boolean isAntPath) {
		this.isAntPath = isAntPath;
	}

	public CoodyParameterAdapter getParamsAdapt() {
		return paramsAdapt;
	}

	public void setParamsAdapt(CoodyParameterAdapter paramsAdapt) {
		this.paramsAdapt = paramsAdapt;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public List<FieldEntity> getParameters() {
		return parameters;
	}

	public void setParameters(List<FieldEntity> parameters) {
		this.parameters = parameters;
	}


}