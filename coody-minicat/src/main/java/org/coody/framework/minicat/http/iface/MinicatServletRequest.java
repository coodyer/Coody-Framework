package org.coody.framework.minicat.http.iface;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.coody.framework.minicat.http.MultipartFile;

public interface MinicatServletRequest {

	MultipartFile getFile(String paramName);

	String getParament(String paramName);

	Map<String, List<Object>> getParams();

	void setParams(Map<String, List<Object>> params);

	String getPostContext();

	MinicatHttpSession getSession();

	String getSessionId();

	String getBasePath();

	String getScheme();

	String getQueryString();

	Integer getContextLength();

	boolean isSessionCread();

	boolean isGzip();

	String getProtocol();

	String getMethod();

	String getRequestURI();

	String getRequestURL();

	Map<String, String> getHeader();

	InputStream getInputStream();
	
	String getCookie(String name);

}
