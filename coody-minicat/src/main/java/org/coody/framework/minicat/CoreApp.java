package org.coody.framework.minicat;

import java.io.IOException;
import java.net.URISyntaxException;

import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.logger.BaseLogger;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.minicat.annotation.Filter;
import org.coody.framework.minicat.annotation.Servlet;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.container.FilterContainer;
import org.coody.framework.minicat.container.ServletContainer;
import org.coody.framework.minicat.servlet.HttpFilter;
import org.coody.framework.minicat.servlet.HttpServlet;
import org.coody.framework.minicat.servlet.MiniCatHttpPart;
import org.coody.framework.minicat.socket.iface.MiniCatService;

public class CoreApp {

	static BaseLogger logger = BaseLogger.getLogger(CoreApp.class);
	
	public static void init(Class<?>... clazzs) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, URISyntaxException {
		long startTime = System.currentTimeMillis();
		// 加載配置文件
		ConfigBuilder.builder();
		ConfigBuilder.flush(new MiniCatConfig(), MiniCatConfig.PREFIX);
		// 框架启动
		MiniCatService miniCatService = (MiniCatService) Class.forName(MiniCatConfig.engine).newInstance();
		logger.info("引用模式>>" + miniCatService.getClass().getName());
		try {
			if (StringUtil.isNullOrEmpty(clazzs)) {
				System.err.println("初始化Servlet为空");
				return;
			}
			// 打开端口
			miniCatService.openPort(MiniCatConfig.port, MiniCatConfig.sessionTimeout);
			for (Class<?> clazz : clazzs) {
				if (!MiniCatHttpPart.class.isAssignableFrom(clazz)) {
					continue;
				}
				Servlet servletFlag = clazz.getAnnotation(Servlet.class);
				if (servletFlag != null && !StringUtil.isNullOrEmpty(servletFlag.value())) {
					HttpServlet servlet = (HttpServlet) clazz.getDeclaredConstructor().newInstance();
					logger.info("注册Servlet>>" + clazz.getName() + ">>" + servletFlag.value());
					ServletContainer.putServlet(servletFlag.value(), servlet);
					servlet.init();
				}
				Filter filterFlag = clazz.getAnnotation(Filter.class);
				if (filterFlag != null && !StringUtil.isNullOrEmpty(filterFlag.value())) {
					HttpFilter filter = (HttpFilter) clazz.getDeclaredConstructor().newInstance();
					filter.setMapping(filterFlag.value());
					logger.info("注册Filter>>" + clazz.getName() + ">>" + filterFlag.value());
					FilterContainer.pushFilter(filter);
					filter.init();
				}
				logger.info("MiniCat:" + MiniCatConfig.port + "启动完成,耗时>>"
						+ (System.currentTimeMillis() - startTime) + "ms");
				// 处理请求
				miniCatService.doService();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
