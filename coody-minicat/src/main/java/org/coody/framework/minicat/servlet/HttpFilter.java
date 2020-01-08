package org.coody.framework.minicat.servlet;

import java.io.IOException;

import org.coody.framework.minicat.container.ApplicationFilterChain;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;

public abstract class HttpFilter extends MiniCatHttpPart {

	private String mapping;

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}


	public abstract void doFilter(MinicatServletRequestImpl request, MinicatServletResponseImpl response,
			ApplicationFilterChain chain) throws IOException;
}
