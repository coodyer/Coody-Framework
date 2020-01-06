package org.coody.framework.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.core.CoreApp;
import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.util.LogUtil;
import org.coody.framework.core.util.PrintException;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.web.adapter.iface.CoodyParameterAdapter;
import org.coody.framework.web.annotation.JsonOut;
import org.coody.framework.web.annotation.TextOut;
import org.coody.framework.web.container.HttpContainer;
import org.coody.framework.web.container.MappingContainer;
import org.coody.framework.web.entity.MvcMapping;

import com.alibaba.fastjson.JSON;

@SuppressWarnings("serial")
public class DispatServlet extends HttpServlet {

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getRequestURI();
		LogUtil.log.debug("收到请求 >>" + path);
		MvcMapping mapping = MappingContainer.getMapping(path);
		if (mapping == null) {
			response.getOutputStream().print("page not found");
			response.setStatus(404);
			return;
		}
		// 装载Request
		HttpContainer.setRequest(request);
		HttpContainer.setResponse(response);
		try {
			CoodyParameterAdapter adapt = mapping.getParamsAdapt();
			Object[] params = adapt.doAdapt(mapping, request, response, request.getSession());
			Object result = mapping.getMethod().invoke(mapping.getBean(), params);
			if (result == null) {
				return;
			}
			JsonOut jsonSerialize = mapping.getMethod().getAnnotation(JsonOut.class);
			if (jsonSerialize != null) {
				response.setHeader("Content-type", "application/Json");
				String json = JSON.toJSONString(result);
				response.getOutputStream().print(json);
				return;
			}
			TextOut textSerialize = mapping.getMethod().getAnnotation(TextOut.class);
			if (textSerialize != null) {
				response.getOutputStream().print(result.toString());
				return;
			}
			String viewFileName = StringUtil.toString(result);
			if (StringUtil.isNullOrEmpty(viewFileName)) {
				response.getOutputStream().print("page not found");
				response.setStatus(404);
				return;
			}
			response.getOutputStream().print("page not found");
			response.setStatus(404);
			return;
		} catch (InvocationTargetException e) {
			PrintException.printException(e.getTargetException());
			response.setStatus(500);
			response.getOutputStream().print("page error");
		} catch (Exception e) {
			PrintException.printException(e);
			response.setStatus(500);
			response.getOutputStream().print("page error");
		}
	}

	@Override
	public void init() {
		try {
			ConfigBuilder.builder();
			CoodyConfig config = ConfigBuilder.flush(new CoodyConfig(), CoodyConfig.PREFIX);
			// 框架启动
			CoreApp.init(config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
