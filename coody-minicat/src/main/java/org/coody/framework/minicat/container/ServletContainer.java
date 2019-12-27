package org.coody.framework.minicat.container;

import java.util.HashMap;
import java.util.Map;

import org.coody.framework.core.util.AntUtil;
import org.coody.framework.minicat.servlet.HttpServlet;

public class ServletContainer {

	private static final Map<String, HttpServlet> SERVLET_CONTAINER = new HashMap<String, HttpServlet>();

	private static final Map<String, HttpServlet> SERVLET_ANT_CONTAINER = new HashMap<String, HttpServlet>();

	public static HttpServlet getServlet(String path) {
		HttpServlet servlet = SERVLET_CONTAINER.get(path);
		if (servlet != null) {
			return servlet;
		}
		for (String patt : SERVLET_ANT_CONTAINER.keySet()) {
			if (AntUtil.isAntMatch(path, patt)) {
				return SERVLET_ANT_CONTAINER.get(patt);
			}
		}
		return null;
	}

	public static void putServlet(String path, HttpServlet servlet) {
		if (AntUtil.isAntPatten(path)) {
			SERVLET_ANT_CONTAINER.put(path, servlet);
			return;
		}
		SERVLET_CONTAINER.put(path, servlet);
	}

}
