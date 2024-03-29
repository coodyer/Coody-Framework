package org.coody.framework.minicat.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.coody.framework.core.util.ByteUtils;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.builder.iface.HttpBuilder;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.exception.BadRequestException;
import org.coody.framework.minicat.exception.MiniCatException;
import org.coody.framework.minicat.exception.NotConnectionException;
import org.coody.framework.minicat.exception.RequestNotInitException;
import org.coody.framework.minicat.http.MinicatServletRequestImpl;

public class BioHttpBuilder extends HttpBuilder {

	private Socket socket;

	public BioHttpBuilder(Socket socket) {
		if (socket == null) {
			throw new NotConnectionException("未实例化Socket");
		}
		this.socket = socket;
	}

	@Override
	protected void buildRequest() throws Exception {
		this.request = new MinicatServletRequestImpl();
	}

	@Override
	protected void flush() throws IOException {
		byte[] data = response.getOutputStream().toByteArray();
		if (CommonUtil.isNullOrEmpty(data)) {
			return;
		}
		if (!socket.isClosed()) {
			socket.getOutputStream().write(data);
			socket.getOutputStream().flush();
			socket.getOutputStream().close();
		}
	}

	public void buildRequestHeader() {
		if (request == null) {
			throw new RequestNotInitException("Request尚未初始化");
		}
		try {
			byte[] data = ByteUtils.getBytes(socket.getInputStream(), MiniCatConfig.maxHeaderLength);
			if (CommonUtil.isNullOrEmpty(data)) {
				return;
			}
			boolean isReadEnd = data.length < MiniCatConfig.maxHeaderLength;
			String headerContext = new String(data, "iso-8859-1");
			String bodyContext = null;
			if (headerContext.contains(splitFlag)) {
				bodyContext = headerContext.substring(headerContext.indexOf(splitFlag) + splitFlag.length());
				headerContext = headerContext.substring(0, headerContext.indexOf(splitFlag));
			}
			headerContext += splitFlag;
			String[] headers = headerContext.split("\r\n");
			if (headers.length < 2) {
				throw new BadRequestException("错误的请求报文");
			}
			String line = headers[0];
			while (line.contains("  ")) {
				line = line.replace("  ", " ");
			}
			String[] vanguards = line.trim().split(" ");
			if (vanguards.length != 3) {
				throw new BadRequestException("错误的请求报文");
			}
			request.setMethod(vanguards[0].toUpperCase());
			String requestURI = vanguards[1];
			if (requestURI.contains("?")) {
				int index = requestURI.indexOf("?");
				if (index < requestURI.length() - 1) {
					request.setQueryString(requestURI.substring(index + 1));
				}
				requestURI = requestURI.substring(0, index);
			}
			request.setRequestURI(requestURI);
			request.setProtocol(vanguards[2]);
			for (int i = 1; i < headers.length; i++) {
				String header = headers[i];
				int index = header.indexOf(":");
				if (index < 1) {
					throw new BadRequestException("错误的请求头部:" + line);
				}
				String name = header.substring(0, index).trim();
				String value = header.substring(index + 1).trim();
				if (CommonUtil.hasNullOrEmpty(name, value)) {
					continue;
				}
				request.setHeader(name, value);
				if (name.equalsIgnoreCase("Content-Encoding")) {
					if (value.contains("gzip")) {
						request.setGzip(true);
					}
				}
//				if (name.equalsIgnoreCase("Cookie")) {
//					if (value != null && value.contains(MiniCatConfig.sessionIdField)) {
//						request.setSessionCread(true);
//					}
//				}
				if (name.equalsIgnoreCase("Host")) {
					request.setBasePath(request.getScheme() + "://" + value);
					if (requestURI.startsWith(request.getBasePath())) {
						requestURI = requestURI.substring(request.getBasePath().length());
						request.setRequestURI(requestURI);
					}
				}
				if (name.equalsIgnoreCase("Content-Length")) {
					request.setContextLength(Integer.valueOf(value));
				}
			}
			try {
				if (isReadEnd || request.getContextLength() < 1) {
					if (!CommonUtil.isNullOrEmpty(bodyContext)) {
						request.setInputStream(new ByteArrayInputStream(bodyContext.getBytes("iso-8859-1")));
					}
					return;
				}
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				try {
					byte[] bodyData = bodyContext.getBytes("iso-8859-1");
					byteArrayOutputStream.write(bodyData);
					int remainLength = request.getContextLength() - bodyData.length;
					byte[] remainData = ByteUtils.getBytes(socket.getInputStream(), remainLength);
					byteArrayOutputStream.write(remainData);
					request.setInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
				} finally {
					byteArrayOutputStream.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (MiniCatException e) {
			throw e;
		} catch (IOException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
