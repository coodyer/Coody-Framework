package org.coody.framework.minicat.servlet;

import java.io.IOException;

import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;

public abstract class HttpServlet extends MiniCatHttpPart {

	public abstract void doService(MinicatServletRequestImpl request, MinicatServletResponseImpl response)
			throws IOException;

}
