package org.coody.framework.minicat.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.adapter.ParamentAdapt;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.container.SessionContainer;
import org.coody.framework.minicat.http.iface.MinicatHttpSession;
import org.coody.framework.minicat.http.iface.MinicatServletRequest;
import org.coody.framework.minicat.util.ByteUtils;
import org.coody.framework.minicat.util.GZIPUtils;

public class MinicatServletRequestImpl implements MinicatServletRequest {

	private String method = "GET";

	private String protocol;

	private String requestURI;

	private String requestURL;

	private Map<String, String> header;

	private ByteArrayInputStream inputStream;

	private String sessionId;

	private boolean isGzip = false;

	private boolean isSessionCread = false;

	private String scheme = (MiniCatConfig.port == 443 ? "https" : "http");

	private String basePath;

	private Map<String, List<Object>> params;

	private String queryString = "";

	private Integer contextLength = 0;

	private String suffix;

	public MultipartFile getFile(String paramName) {
		if (params == null) {
			initParams();
		}
		List<Object> paramValues = params.get(paramName);
		if (CommonUtil.isNullOrEmpty(paramValues)) {
			return null;
		}
		Object value = paramValues.get(0);
		if (!MultipartFile.class.isAssignableFrom(value.getClass())) {
			return null;
		}
		return (MultipartFile) value;
	}

	public String getParament(String paramName) {
		if (params == null) {
			initParams();
		}
		List<Object> paramValues = params.get(paramName);
		if (CommonUtil.isNullOrEmpty(paramValues)) {
			return null;
		}
		Object value = paramValues.get(0);
		if (MultipartFile.class.isAssignableFrom(value.getClass())) {
			try {
				return new String(((MultipartFile) value).getFileContext(), MiniCatConfig.encode);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		return value.toString();
	}

	public Map<String, List<Object>> getParams() {
		if (CommonUtil.isNullOrEmpty(params)) {
			initParams();
		}
		return params;
	}

	public void setParams(Map<String, List<Object>> params) {
		this.params = params;
	}

	private void initParams() {
		params = ParamentAdapt.buildGeneralParams(queryString);
		if (header.containsKey("Content-Type") && header.get("Content-Type").contains("multipart/form-data")) {
			String line = header.get("Content-Type");
			String[] dabbles = line.split(";");
			String boundary = "";
			for (String dabble : dabbles) {
				int index = dabble.indexOf("=");
				if (index < 1 || index > dabble.length()) {
					continue;
				}
				String name = dabble.substring(0, dabble.indexOf("=")).trim();
				String value = dabble.substring(dabble.indexOf("=") + 1);
				if (name.equals("boundary")) {
					boundary = value;
				}
			}
			byte[] data = ByteUtils.getBytes(inputStream, contextLength);
			Map<String, List<Object>> paramMap = ParamentAdapt.buildMultipartParams(data, boundary);
			params = mergeParaMap(params, paramMap);
		} else {
			String postContext = getPostContext();
			if (!CommonUtil.isNullOrEmpty(postContext)) {
				Map<String, List<Object>> paramMap = ParamentAdapt.buildGeneralParams(postContext);
				params = mergeParaMap(params, paramMap);
			}
		}
	}

	private Map<String, List<Object>> mergeParaMap(Map<String, List<Object>> paraMap1,
			Map<String, List<Object>> paraMap2) {

		if (CommonUtil.isNullOrEmpty(paraMap1)) {
			return paraMap2;
		}
		if (CommonUtil.isNullOrEmpty(paraMap2)) {
			return paraMap1;
		}
		for (String key : paraMap1.keySet()) {
			if (!paraMap2.containsKey(key)) {
				paraMap2.put(key, paraMap1.get(key));
				continue;
			}
			paraMap2.get(key).addAll(paraMap1.get(key));
		}
		return paraMap2;
	}

	public String getPostContext() {
		try {
			byte[] data = ByteUtils.getBytes(inputStream, contextLength);
			if (data == null) {
				return null;
			}
			if (isGzip) {
				data = GZIPUtils.uncompress(data);
			}
			return new String(data, MiniCatConfig.encode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public MinicatHttpSession getSession() {
		sessionId = getSessionId();
		MinicatSessionImpl session = SessionContainer.getSession(sessionId);
		if (!CommonUtil.isNullOrEmpty(session)) {
			return session;
		}
		isSessionCread = true;
		sessionId = SessionContainer.createSessionId();
		session = SessionContainer.initSession(sessionId);
		return session;
	}

	public String getSessionId() {
		if (!CommonUtil.isNullOrEmpty(sessionId)) {
			return sessionId;
		}
		if (CommonUtil.isNullOrEmpty(header)) {
			return null;
		}
		String cookie = header.get("Cookie");
		if (CommonUtil.isNullOrEmpty(cookie)) {
			return null;
		}
		if (!cookie.contains(MiniCatConfig.sessionIdField)) {
			return null;
		}
		String[] cookies = cookie.split(";");
		for (String line : cookies) {
			if (!line.contains(MiniCatConfig.sessionIdField)) {
				continue;
			}
			int index = line.indexOf("=");
			sessionId = line.substring(index + 1).trim();
			return sessionId;
		}
		return null;
	}

	public MinicatServletRequestImpl() throws IOException {
	}

	public String getBasePath() {
		return basePath;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getScheme() {
		return scheme;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public Integer getContextLength() {
		return contextLength;
	}

	public void setContextLength(Integer contextLength) {
		this.contextLength = contextLength;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public boolean isSessionCread() {
		return isSessionCread;
	}

	public void setSessionCread(boolean isSessionCread) {
		this.isSessionCread = isSessionCread;
	}

	public boolean isGzip() {
		return isGzip;
	}

	public void setGzip(boolean isGzip) {
		this.isGzip = isGzip;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public void setRequestURI(String requestURI) {
		if (requestURI == null) {
			requestURI = "";
		}
		requestURI = requestURI.replace("\\", "/");
		while (requestURI.contains("//")) {
			requestURI = requestURI.replace("//", "/");
		}
		if (requestURI.trim().equals("/") || requestURI.trim().equals("")) {
			requestURI = MiniCatConfig.WELCOME_PATH;
		}
		this.requestURI = requestURI;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public Map<String, String> getHeader() {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		return header;
	}

	public void setHeader(String name, String value) {
		if (header == null) {
			header = new HashMap<String, String>();
		}
		header.put(name, value);
	}

	public ByteArrayInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String getCookie(String name) {
		String cookie = header.get("Cookie");
		if (CommonUtil.isNullOrEmpty(cookie)) {
			return null;
		}
		String[] cookies = cookie.split(";");
		for (String line : cookies) {
			int index = line.indexOf("=");
			if (index < 1) {
				continue;
			}
			String cookieName = line.substring(0, index);
			if (cookieName.trim().equals(name)) {
				return line.substring(index + 1, line.length()).trim();
			}
		}
		return null;
	}
}
