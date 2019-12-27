package org.coody.framework.minicat.web.container;

import org.coody.framework.core.container.ThreadContainer;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;
import org.coody.framework.minicat.web.constant.MvcContant;

public class HttpContainer {
	

	public static void setRequest(MinicatServletRequestImpl request){
		ThreadContainer.set(MvcContant.REQUEST_WRAPPER, request);
	}
	
	public static MinicatServletRequestImpl getRequest(){
		return ThreadContainer.get(MvcContant.REQUEST_WRAPPER);
	}
	
	public static void setResponse(MinicatServletResponseImpl response){
		ThreadContainer.set(MvcContant.RESPONSE_WRAPPER, response);
	}
	
	public static MinicatServletResponseImpl getResponse(){
		return ThreadContainer.get(MvcContant.RESPONSE_WRAPPER);
	}
}
