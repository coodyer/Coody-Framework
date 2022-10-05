package org.coody.framework.logged.function.invoke;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.logged.function.iface.LoggedFunction;

@SuppressWarnings("serial")
public class FunctionInvoker extends BaseModel {

	private LoggedFunction function;

	private String parameter;

	public LoggedFunction getFunction() {
		return function;
	}

	public void setFunction(LoggedFunction function) {
		this.function = function;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

}
