package org.coody.framework.minicat.builder.iface;

import java.io.IOException;
import java.text.MessageFormat;

import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.exception.BadRequestException;
import org.coody.framework.minicat.exception.PageNotFoundException;
import org.coody.framework.minicat.exception.ResponseNotInitException;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;
import org.coody.framework.minicat.http.MinicatServletResponseImpl;
import org.coody.framework.minicat.processor.MinicatProcess;
import org.coody.framework.minicat.util.GZIPUtils;

public abstract class HttpBuilder {

	protected MinicatServletRequestImpl request;

	protected MinicatServletResponseImpl response;

	protected static final String splitFlag = "\r\n\r\n";

	public MinicatServletRequestImpl getRequest() {
		return request;
	}

	public void setRequest(MinicatServletRequestImpl request) {
		this.request = request;
	}

	public MinicatServletResponseImpl getResponse() {
		return response;
	}

	public void setResponse(MinicatServletResponseImpl response) {
		this.response = response;
	}

	public void buildResponse() throws IOException {
		if (response == null) {
			response = new MinicatServletResponseImpl();
		}
		buildResponse(response.getHttpCode(), response.getOutputStream().toByteArray());
	}

	public void buildResponse(byte[] data) throws IOException {
		if (response == null) {
			response = new MinicatServletResponseImpl();
		}
		buildResponse(response.getHttpCode(), data);
	}

	public void buildResponse(int httpCode, String msg) throws IOException {
		if (response == null) {
			response = new MinicatServletResponseImpl();
		}
		buildResponse(httpCode, msg.getBytes(MiniCatConfig.encode));
	}

	public void buildResponse(int httpCode, byte[] data) throws IOException {
		if (response == null) {
			response = new MinicatServletResponseImpl();
		}
		buildResponseHeader();
		if (MiniCatConfig.openGzip) {
			// 压缩数据
			data = GZIPUtils.compress(data);
		}
		Integer contextLength = 0;
		if (data != null) {
			contextLength = data.length;
		}
		response.setHeader("Content-Length", contextLength.toString());
		StringBuilder responseHeader = new StringBuilder("HTTP/1.1 ").append(httpCode).append(" ");
		if (httpCode == 302) {
			responseHeader.append("Found");
		}
		responseHeader.append("\r\n");
		for (String key : response.getHeaders().keySet()) {
			for (String header : response.getHeader(key)) {
				responseHeader.append(key).append(": ").append(header).append("\r\n");
			}
		}
		responseHeader.append("\r\n");
		response.getOutputStream().reset();
		response.getOutputStream().write(responseHeader.toString().getBytes(MiniCatConfig.encode));
		if (!CommonUtil.isNullOrEmpty(data)) {
			response.getOutputStream().write(data);
		}
	}

	public void buildResponseHeader() throws IOException {
		if (response == null) {
			throw new ResponseNotInitException("Response尚未初始化");
		}
		response.setHeader("Connection", "close");
		response.setHeader("Server", "MiniCat/1.0 By Coody");
		if (!response.containsHeader("Content-Type")) {
			switch (request.getSuffix()) {
			case "js":
				response.setHeader("Content-Type", "application/x-javascript;charset=" + MiniCatConfig.encode);
				break;
			case "css":
				response.setHeader("Content-Type", "text/css;charset=" + MiniCatConfig.encode);
				break;
			default:
				response.setHeader("Content-Type", "text/html;charset=" + MiniCatConfig.encode);
				break;
			}
		}
		if (MiniCatConfig.openGzip) {
			response.setHeader("Content-Encoding", "gzip");
		}
		if (request != null && request.isSessionCread()) {
			String cookie = MessageFormat.format("{0}={1}; path=/ ; HttpOnly", MiniCatConfig.sessionIdField,
					request.getSessionId());
			response.setHeader("Set-Cookie", cookie);
		}
	}

	public abstract void buildRequestHeader();

	private void destroy() {
		if (response != null && response.getOutputStream() != null) {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (request != null && request.getInputStream() != null) {
			try {
				request.getInputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void builder(String ip) {
		try {
			buildRequest();
			if (!MiniCatConfig.method.contains(this.request.getMethod())) {
				buildResponse(400, "400 bad request");
				return;
			}
			buildRequestHeader();
			request.setSuffix("");
			
			
			try {
				String[] address=ip.split(":");
				request.setClientIp(address[0].replace("/", ""));
				request.setClientPort(Integer.valueOf(address[1]));
			} catch (Exception e) {
				buildResponse(400, "400 bad request");
				return;
			}
			
			if (request.getRequestURI() != null && request.getRequestURI().contains(".")) {
				request.setSuffix(request.getRequestURI()
						.substring(request.getRequestURI().lastIndexOf(".") + 1, request.getRequestURI().length())
						.toLowerCase());
			}
		} catch (BadRequestException e) {
			e.printStackTrace();
			try {
				buildResponse(400, "400 bad request");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				buildResponse(500, "error execution");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (PageNotFoundException e) {
			try {
				buildResponse(404, "page not found!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				buildResponse(500, "error execution");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void invoke() {
		try {
			this.response = new MinicatServletResponseImpl();
			MinicatProcess.doService(this);
			buildResponse();
		} catch (BadRequestException e) {
			e.printStackTrace();
			try {
				buildResponse(400, "400 bad request");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				buildResponse(500, "error execution");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (PageNotFoundException e) {
			try {
				buildResponse(404, "page not found!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				buildResponse(500, "error execution");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public void flushAndClose() {
		try {
			flush();
		} catch (IOException e) {
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			destroy();
		}
	}

	protected abstract void buildRequest() throws Exception;

	protected abstract void flush() throws IOException;

}
