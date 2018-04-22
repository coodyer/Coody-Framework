package org.coody.framework.wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.container.HttpContainer;

@SuppressWarnings("deprecation")
@InitBean
public class IcopResponseWrapper implements ServletResponse, HttpServletResponse {

	public void addCookie(Cookie arg0) {
		HttpContainer.getResponse().addCookie(arg0);

	}

	public void addDateHeader(String arg0, long arg1) {
		HttpContainer.getResponse().addDateHeader(arg0, arg1);

	}

	public void addHeader(String arg0, String arg1) {
		HttpContainer.getResponse().addHeader(arg0, arg1);

	}

	public void addIntHeader(String arg0, int arg1) {
		HttpContainer.getResponse().addIntHeader(arg0, arg1);

	}

	public boolean containsHeader(String arg0) {
		return HttpContainer.getResponse().containsHeader(arg0);
	}

	public String encodeRedirectURL(String arg0) {
		return HttpContainer.getResponse().encodeRedirectURL(arg0);
	}

	public String encodeRedirectUrl(String arg0) {
		return HttpContainer.getResponse().encodeRedirectUrl(arg0);
	}

	public String encodeURL(String arg0) {
		return HttpContainer.getResponse().encodeURL(arg0);
	}

	public String encodeUrl(String arg0) {
		return HttpContainer.getResponse().encodeUrl(arg0);
	}

	public void sendError(int arg0) throws IOException {
		HttpContainer.getResponse().sendError(arg0);
	}

	public void sendError(int arg0, String arg1) throws IOException {
		HttpContainer.getResponse().sendError(arg0, arg1);
	}

	public void sendRedirect(String arg0) throws IOException {
		HttpContainer.getResponse().sendRedirect(arg0);
		;
	}

	public void setDateHeader(String arg0, long arg1) {
		HttpContainer.getResponse().setDateHeader(arg0, arg1);
	}

	public void setHeader(String arg0, String arg1) {
		HttpContainer.getResponse().setHeader(arg0, arg1);

	}

	public void setIntHeader(String arg0, int arg1) {
		HttpContainer.getResponse().setIntHeader(arg0, arg1);

	}

	public void setStatus(int arg0) {
		HttpContainer.getResponse().setStatus(arg0);

	}

	public void setStatus(int arg0, String arg1) {
		HttpContainer.getResponse().setStatus(arg0);

	}

	public void flushBuffer() throws IOException {
		HttpContainer.getResponse().flushBuffer();

	}

	public int getBufferSize() {
		return HttpContainer.getResponse().getBufferSize();
	}

	public String getCharacterEncoding() {
		return HttpContainer.getResponse().getCharacterEncoding();
	}

	public String getContentType() {
		return HttpContainer.getResponse().getContentType();
	}

	public Locale getLocale() {
		return HttpContainer.getResponse().getLocale();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return HttpContainer.getResponse().getOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		return HttpContainer.getResponse().getWriter();
	}

	public boolean isCommitted() {
		return HttpContainer.getResponse().isCommitted();
	}

	public void reset() {
		HttpContainer.getResponse().reset();

	}

	public void resetBuffer() {
		HttpContainer.getResponse().resetBuffer();

	}

	public void setBufferSize(int arg0) {
		HttpContainer.getResponse().setBufferSize(arg0);

	}

	public void setCharacterEncoding(String arg0) {
		HttpContainer.getResponse().setCharacterEncoding(arg0);

	}

	public void setContentLength(int arg0) {
		HttpContainer.getResponse().setContentLength(arg0);

	}

	public void setContentType(String arg0) {
		HttpContainer.getResponse().setContentType(arg0);

	}

	public void setLocale(Locale arg0) {
		HttpContainer.getResponse().setLocale(arg0);

	}

}
