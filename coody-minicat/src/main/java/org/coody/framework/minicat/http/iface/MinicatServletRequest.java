package org.coody.framework.minicat.http.iface;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.coody.framework.minicat.http.MultipartFile;

public interface MinicatServletRequest {

	MultipartFile getFile(String parameter);

	String getParament(String parameter);

	Map<String, List<Object>> getParamenters();

	void setParamenters(Map<String, List<Object>> paramenters);

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

	String getClientIp();

	Integer getClientPort();
}
