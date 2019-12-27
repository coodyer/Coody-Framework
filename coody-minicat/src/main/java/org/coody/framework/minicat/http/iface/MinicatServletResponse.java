package org.coody.framework.minicat.http.iface;

import java.util.List;
import java.util.Map;

import org.coody.framework.minicat.http.stream.MiniCatOutputStream;

public interface MinicatServletResponse {

	Integer getHttpCode();

	void setHttpCode(Integer httpCode);

	boolean containsHeader(String name);

	Map<String, List<String>> getHeaders();

	void setHeader(String name, String headerLine);

	void setHeaders(Map<String, List<String>> header);

	MiniCatOutputStream getOutputStream();

	void sendRedirect(String location);
}
