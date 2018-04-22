package org.coody.framework.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.container.HttpContainer;

@SuppressWarnings({ "unchecked", "rawtypes","deprecation" })
@InitBean
public class IcopRequestWrapper implements ServletRequest,HttpServletRequest {

	public String getParameter(String name) {
		return HttpContainer.getRequest().getParameter(name);
	}

	public String getHeader(String name) {
		return HttpContainer.getRequest().getHeader(name);
	}

	/**
	 * 覆盖getHeaderNames方法，避免穷举head参数名引发的xss
	 */

	public Enumeration<String> getHeaderNames() {
		return HttpContainer.getRequest().getHeaderNames();
	}

	/**
	 * 覆盖getParameterNames方法，避免穷举参数名引发的xss
	 */
	
	public Enumeration<String> getParameterNames() {
		return HttpContainer.getRequest().getParameterNames();
	}

	/**
	 * 覆盖getParameterMap方法，避免穷举参数名或值引发的xss
	 */
	
	public Map<String, String[]> getParameterMap() {
		return HttpContainer.getRequest().getParameterMap();
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getOrgRequest() {
		return HttpContainer.getRequest();
	}

	
	public Object getAttribute(String arg0) {
		return HttpContainer.getRequest().getAttribute(arg0);
	}

	
	public Enumeration getAttributeNames() {
		return HttpContainer.getRequest().getAttributeNames();
	}

	
	public String getCharacterEncoding() {
		return HttpContainer.getRequest().getCharacterEncoding();
	}

	
	public int getContentLength() {
		return HttpContainer.getRequest().getContentLength();
	}

	
	public String getContentType() {
		return HttpContainer.getRequest().getContentType();
	}

	
	public ServletInputStream getInputStream() throws IOException {
		return HttpContainer.getRequest().getInputStream();
	}

	
	public String getLocalAddr() {
		return HttpContainer.getRequest().getLocalAddr();
	}

	
	public String getLocalName() {
		return HttpContainer.getRequest().getLocalName();
	}

	
	public int getLocalPort() {
		return HttpContainer.getRequest().getLocalPort();
	}

	
	public Locale getLocale() {
		return HttpContainer.getRequest().getLocale();
	}

	
	public Enumeration getLocales() {
		return HttpContainer.getRequest().getLocales();
	}

	
	public String[] getParameterValues(String arg0) {
		return HttpContainer.getRequest().getParameterValues(arg0);
	}

	
	public String getProtocol() {
		return HttpContainer.getRequest().getProtocol();
	}

	
	public BufferedReader getReader() throws IOException {
		return HttpContainer.getRequest().getReader();
	}

	
	public String getRealPath(String arg0) {
		return HttpContainer.getRequest().getRealPath(arg0);
	}

	
	public String getRemoteAddr() {
		return HttpContainer.getRequest().getRemoteAddr();
	}

	
	public String getRemoteHost() {
		return HttpContainer.getRequest().getRemoteHost();
	}

	
	public int getRemotePort() {
		return HttpContainer.getRequest().getRemotePort();
	}

	
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return HttpContainer.getRequest().getRequestDispatcher(arg0);
	}

	
	public String getScheme() {
		return HttpContainer.getRequest().getScheme();
	}

	
	public String getServerName() {
		return HttpContainer.getRequest().getServerName();
	}

	
	public int getServerPort() {
		return HttpContainer.getRequest().getServerPort();
	}

	
	public boolean isSecure() {
		return HttpContainer.getRequest().isSecure();
	}

	public void removeAttribute(String arg0) {
		HttpContainer.getRequest().removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		HttpContainer.getRequest().setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		HttpContainer.getRequest().setCharacterEncoding(arg0);
	}

	public String getAuthType() {
		return HttpContainer.getRequest().getAuthType();
	}

	public String getContextPath() {
		return HttpContainer.getRequest().getContextPath();
	}

	public Cookie[] getCookies() {
		return HttpContainer.getRequest().getCookies();
	}

	public long getDateHeader(String arg0) {
		return HttpContainer.getRequest().getDateHeader(arg0);
	}

	public Enumeration getHeaders(String arg0) {
		return HttpContainer.getRequest().getHeaders(arg0);
	}

	
	public int getIntHeader(String arg0) {
		return HttpContainer.getRequest().getIntHeader(arg0);
	}

	
	public String getMethod() {
		return HttpContainer.getRequest().getMethod();
	}

	
	public String getPathInfo() {
		return HttpContainer.getRequest().getPathInfo();
	}

	
	public String getPathTranslated() {
		return HttpContainer.getRequest().getPathTranslated();
	}

	
	public String getQueryString() {
		return HttpContainer.getRequest().getQueryString();
	}

	
	public String getRemoteUser() {
		return HttpContainer.getRequest().getRemoteUser();
	}

	
	public String getRequestURI() {
		return HttpContainer.getRequest().getRequestURI();
	}

	
	public StringBuffer getRequestURL() {
		return HttpContainer.getRequest().getRequestURL();
	}

	
	public String getRequestedSessionId() {
		return HttpContainer.getRequest().getRequestedSessionId();
	}

	
	public String getServletPath() {
		return HttpContainer.getRequest().getServletPath();
	}

	
	public HttpSession getSession() {
		return HttpContainer.getRequest().getSession();
	}

	
	public HttpSession getSession(boolean arg0) {
		return HttpContainer.getRequest().getSession(arg0);
	}

	
	public Principal getUserPrincipal() {
		return HttpContainer.getRequest().getUserPrincipal();
	}

	
	public boolean isRequestedSessionIdFromCookie() {
		return HttpContainer.getRequest().isRequestedSessionIdFromCookie();
	}

	
	public boolean isRequestedSessionIdFromURL() {
		return HttpContainer.getRequest().isRequestedSessionIdFromURL();
	}

	
	public boolean isRequestedSessionIdFromUrl() {
		return HttpContainer.getRequest().isRequestedSessionIdFromUrl();
	}

	
	public boolean isRequestedSessionIdValid() {
		return HttpContainer.getRequest().isRequestedSessionIdValid();
	}

	
	public boolean isUserInRole(String arg0) {
		return HttpContainer.getRequest().isUserInRole(arg0);
	}

}
