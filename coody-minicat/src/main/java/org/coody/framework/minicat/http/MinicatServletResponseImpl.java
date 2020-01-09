package org.coody.framework.minicat.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.minicat.http.iface.MinicatServletResponse;
import org.coody.framework.minicat.http.stream.MiniCatOutputStream;

public class MinicatServletResponseImpl implements MinicatServletResponse {

	private Integer httpCode = 200;

	private Map<String, List<String>> header;

	private MiniCatOutputStream outputStream = new MiniCatOutputStream();

	public Integer getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
	}

	public boolean containsHeader(String name) {
		if (header == null) {
			return false;
		}
		return header.containsKey(name);
	}

	public Map<String, List<String>> getHeaders() {
		if (header == null) {
			return null;
		}
		return header;
	}

	public List<String> getHeader(String name) {
		if (header == null) {
			return null;
		}
		return header.get(name);
	}

	public void setHeader(String name, String headerLine) {
		if (header == null) {
			header = new ConcurrentHashMap<String, List<String>>();
		}
		if (!header.containsKey(name)) {
			header.put(name, new ArrayList<String>());
		}
		header.get(name).add(headerLine);
		return;
	}

	public void setHeaders(Map<String, List<String>> header) {
		this.header = header;
	}

	public void setOutputStream(MiniCatOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public MiniCatOutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void sendRedirect(String location) {
		setHttpCode(302);
		setHeader("Location", location);
	}

	@Override
	public void setCookie(String name, String line) {
		setHeader("Set-Cookie", name + "=" + line + "; path=/ ; HttpOnly");
	}

}
