package org.coody.framework.minicat.container;

import java.io.IOException;

import org.coody.framework.core.util.AntUtil;
import org.coody.framework.minicat.exception.PageNotFoundException;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;
import org.coody.framework.minicat.servlet.HttpFilter;
import org.coody.framework.minicat.servlet.HttpServlet;

public final class ApplicationFilterChain {

	public ApplicationFilterChain(HttpServlet servlet) {
		this.servlet = servlet;
	}

	private int pos = 0;
	private HttpServlet servlet;

	public void doFilter(MinicatServletRequestImpl request, MinicatServletResponseImpl response) throws IOException {
		if (pos < FilterContainer.FILTER_CONTAINER.size()) {
			HttpFilter filter = FilterContainer.FILTER_CONTAINER.get(pos++);
			if (!AntUtil.isAntMatch(request.getRequestURI(), filter.getMapping())) {
				doFilter(request, response);
				return;
			}
			filter.doFilter(request, response, this);
			return;
		}
		if (servlet == null) {
			throw new PageNotFoundException("该页面未找到>>" + request.getRequestURI());
		}
		servlet.doService(request, response);
	}

}