package org.coody.framework.core.point;

import org.coody.framework.core.entity.BaseModel;

@SuppressWarnings("serial")
public class AspectAble extends BaseModel{

	private AspectPoint point;
	
	private Object[] params;

	
	
	
	public AspectAble(AspectPoint point, Object[] params) {
		super();
		this.point = point;
		this.params = params;
	}

	public AspectPoint getPoint() {
		return point;
	}

	public void setPoint(AspectPoint point) {
		this.point = point;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public Object invoke() throws Throwable {
		return point.invoke(this, params);
	}
	
}
