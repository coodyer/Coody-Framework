package org.coody.framework.minicat.servlet;

import java.io.IOException;
import java.io.InputStream;

import org.coody.framework.core.util.file.FileUtils;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;

public class ResourceServlet extends HttpServlet {

	@Override
	public void doService(MinicatServletRequestImpl request, MinicatServletResponseImpl response) throws IOException {
		InputStream inputStream = Thread.class.getResourceAsStream(request.getRequestURI());
		if (inputStream == null) {
			response.setHttpCode(404);
			return;
		}
		response.getOutputStream().write(FileUtils.inputStreamToBytes(inputStream));
	}

	@Override
	public void init() throws Exception {
	}

}
