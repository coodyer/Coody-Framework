package org.coody.framework.minicat.constant;

import java.util.Arrays;
import java.util.List;

public class HttpMethod {
	
	public static final String GET="GET";

	public static final String POST="POST";
	
	public static final String PUT="PUT";
	
	public static final String OPTIONS="OPTIONS";
	
	public static final String HEAD="HEAD";
	
	public static final String DELETE="DELETE";
	
	public static final String TRACE="TRACE";
	
	public static final String CONNECT="CONNECT";
	
	public static final List<String> HAS_BODY_METHODS=Arrays.asList(new String[]{POST,PUT,DELETE,TRACE,CONNECT});
}
