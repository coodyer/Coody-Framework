package org.coody.framework.core.model;

@SuppressWarnings("serial")
public class AspectPoint extends BaseModel{

	private AspectAbler abler;
	
	private Object[] params;

	
	
	
	public AspectPoint(AspectAbler abler, Object[] params) {
		super();
		this.abler = abler;
		this.params = params;
	}

	public AspectAbler getAbler() {
		return abler;
	}

	public void setAbler(AspectAbler abler) {
		this.abler = abler;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public Object invoke() throws Throwable {
		return abler.invoke(this, params);
	}
	
}
