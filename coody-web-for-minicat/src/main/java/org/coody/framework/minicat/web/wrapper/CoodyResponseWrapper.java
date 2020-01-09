package org.coody.framework.minicat.web.wrapper;

import java.util.List;
import java.util.Map;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.minicat.http.iface.MinicatServletResponse;
import org.coody.framework.minicat.http.stream.MiniCatOutputStream;
import org.coody.framework.minicat.web.container.HttpContainer;

@AutoBuild
public class CoodyResponseWrapper implements MinicatServletResponse {

	@Override
	public Integer getHttpCode() {
		return HttpContainer.getResponse().getHttpCode();
	}

	@Override
	public void setHttpCode(Integer httpCode) {
		HttpContainer.getResponse().setHttpCode(httpCode);
	}

	@Override
	public boolean containsHeader(String name) {
		return HttpContainer.getResponse().containsHeader(name);
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		return HttpContainer.getResponse().getHeaders();
	}

	@Override
	public void setHeader(String name, String headerLine) {
		HttpContainer.getResponse().setHeader(name, headerLine);
	}

	@Override
	public void setHeaders(Map<String, List<String>> header) {
		HttpContainer.getResponse().setHeaders(header);
	}

	@Override
	public MiniCatOutputStream getOutputStream() {
		return HttpContainer.getResponse().getOutputStream();
	}

	@Override
	public void sendRedirect(String location) {
		HttpContainer.getResponse().sendRedirect(location);
	}

	@Override
	public void setCookie(String name, String line) {
		HttpContainer.getResponse().setCookie(name, line);
	}

}
