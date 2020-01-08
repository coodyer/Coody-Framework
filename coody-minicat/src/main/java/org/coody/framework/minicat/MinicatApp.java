package org.coody.framework.minicat;

import java.io.IOException;
import java.net.URISyntaxException;

import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.util.LogUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.minicat.annotation.Filter;
import org.coody.framework.minicat.annotation.Servlet;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.container.FilterContainer;
import org.coody.framework.minicat.container.ServletContainer;
import org.coody.framework.minicat.servlet.HttpFilter;
import org.coody.framework.minicat.servlet.HttpServlet;
import org.coody.framework.minicat.servlet.MiniCatHttpPart;
import org.coody.framework.minicat.servlet.ResourceServlet;
import org.coody.framework.minicat.socket.iface.MiniCatService;

public class MinicatApp {

	public static void init(Class<?>... clazzs) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, URISyntaxException {
		long startTime = System.currentTimeMillis();
		// 加載配置文件
		ConfigBuilder.builder();
		ConfigBuilder.flush(new MiniCatConfig(), MiniCatConfig.PREFIX);
		// 框架启动
		MiniCatService miniCatService = (MiniCatService) Class.forName(MiniCatConfig.engine).newInstance();
		LogUtil.log.info("引用模式>>" + miniCatService.getClass().getName());
		try {
			if (StringUtil.isNullOrEmpty(clazzs)) {
				System.err.println("初始化Servlet为空");
				return;
			}
			// 启动静态资源servlet
			if (!StringUtil.isNullOrEmpty(MiniCatConfig.resources)) {
				HttpServlet servlet = new ResourceServlet();
				LogUtil.log.info("注册Servlet>>" + ResourceServlet.class.getName() + ">>" + MiniCatConfig.resources);
				String servletPath = MiniCatConfig.resources;
				String[] mappings = servletPath.split(MiniCatConfig.split);
				for (String mapping : mappings) {
					ServletContainer.putServlet(mapping, servlet);
				}
				servlet.init();
			}
			for (Class<?> clazz : clazzs) {
				if (!MiniCatHttpPart.class.isAssignableFrom(clazz)) {
					continue;
				}
				Servlet servletFlag = clazz.getAnnotation(Servlet.class);
				if (servletFlag != null && !StringUtil.isNullOrEmpty(servletFlag.value())) {
					HttpServlet servlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
					LogUtil.log.info("注册Servlet>>" + clazz.getName() + ">>" + servletFlag.value());
					String servletPath = servletFlag.value();
					String[] mappings = servletPath.split(MiniCatConfig.split);
					for (String mapping : mappings) {
						ServletContainer.putServlet(mapping, servlet);
					}
					servlet.init();
				}
				Filter filterFlag = clazz.getAnnotation(Filter.class);
				if (filterFlag != null && !StringUtil.isNullOrEmpty(filterFlag.value())) {
					HttpFilter filter = (HttpFilter) clazz.getDeclaredConstructor().newInstance();
					filter.setMapping(filterFlag.value());
					LogUtil.log.info("注册Filter>>" + clazz.getName() + ">>" + filterFlag.value());
					FilterContainer.pushFilter(filter);
					filter.init();
				}
			}
			// 打开端口
			miniCatService.openPort(MiniCatConfig.port, MiniCatConfig.sessionTimeout);
			LogUtil.log.info(
					"MiniCat:" + MiniCatConfig.port + "启动完成,耗时>>" + (System.currentTimeMillis() - startTime) + "ms");
			// 处理请求
			miniCatService.doService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
