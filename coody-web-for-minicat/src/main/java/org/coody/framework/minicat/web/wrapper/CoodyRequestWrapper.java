package org.coody.framework.minicat.web.wrapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.minicat.http.MultipartFile;
import org.coody.framework.minicat.http.iface.MinicatHttpSession;
import org.coody.framework.minicat.http.iface.MinicatServletRequest;
import org.coody.framework.minicat.web.container.HttpContainer;

@AutoBuild
public class CoodyRequestWrapper implements MinicatServletRequest {

	@Override
	public MultipartFile getFile(String paramName) {
		return HttpContainer.getRequest().getFile(paramName);
	}

	@Override
	public String getParament(String paramName) {
		return HttpContainer.getRequest().getParament(paramName);
	}

	@Override
	public Map<String, List<Object>> getParams() {
		return HttpContainer.getRequest().getParams();
	}

	@Override
	public void setParams(Map<String, List<Object>> params) {
		HttpContainer.getRequest().setParams(params);
	}

	@Override
	public String getPostContext() {
		return HttpContainer.getRequest().getPostContext();
	}

	@Override
	public MinicatHttpSession getSession() {
		return HttpContainer.getRequest().getSession();
	}

	@Override
	public String getSessionId() {
		return HttpContainer.getRequest().getSessionId();
	}

	@Override
	public String getScheme() {
		return HttpContainer.getRequest().getScheme();
	}

	@Override
	public String getQueryString() {
		return HttpContainer.getRequest().getQueryString();
	}

	@Override
	public Integer getContextLength() {
		return HttpContainer.getRequest().getContextLength();
	}

	@Override
	public boolean isSessionCread() {
		return HttpContainer.getRequest().isSessionCread();
	}

	@Override
	public boolean isGzip() {
		return HttpContainer.getRequest().isGzip();
	}

	@Override
	public String getProtocol() {
		return HttpContainer.getRequest().getProtocol();
	}

	@Override
	public String getMethod() {
		return HttpContainer.getRequest().getMethod();
	}

	@Override
	public String getRequestURI() {
		return HttpContainer.getRequest().getRequestURI();
	}

	@Override
	public String getRequestURL() {
		return HttpContainer.getRequest().getRequestURL();
	}

	@Override
	public Map<String, String> getHeader() {
		return HttpContainer.getRequest().getHeader();
	}

	@Override
	public InputStream getInputStream() {
		return HttpContainer.getRequest().getInputStream();
	}

	@Override
	public String getBasePath() {
		return HttpContainer.getRequest().getBasePath();
	}

	@Override
	public String getCookie(String name) {
		return HttpContainer.getRequest().getCookie(name);
	}

}
