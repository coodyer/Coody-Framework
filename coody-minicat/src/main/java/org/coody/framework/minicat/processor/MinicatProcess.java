package org.coody.framework.minicat.processor;

import org.coody.framework.minicat.builder.iface.HttpBuilder;
import org.coody.framework.minicat.container.ApplicationFilterChain;
import org.coody.framework.minicat.container.ServletContainer;
import org.coody.framework.minicat.servlet.HttpServlet;

public class MinicatProcess {

	public static void doService(HttpBuilder build) throws Exception {
		HttpServlet servlet = ServletContainer.getServlet(build.getRequest().getRequestURI());
		ApplicationFilterChain chain = new ApplicationFilterChain(servlet);
		chain.doFilter(build.getRequest(), build.getResponse());

	}
}
