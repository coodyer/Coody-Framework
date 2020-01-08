package org.coody.framework.minicat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingQueue;

import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.threadpool.SysThreadPool;
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

	public static LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();

	public static Long startTime = null;

	public static void init(Class<?>... clazzs) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, URISyntaxException {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			LogUtil.log.error("初始化Servlet为空");
			return;
		}
		startTime = System.currentTimeMillis();
		// 加載配置文件
		ConfigBuilder.builder();
		ConfigBuilder.flush(new MiniCatConfig(), MiniCatConfig.PREFIX);
		// 框架启动
		MiniCatService miniCatService = (MiniCatService) Class.forName(MiniCatConfig.engine).newInstance();
		LogUtil.log.info("引用模式>>" + miniCatService.getClass().getName());
		SysThreadPool.THREAD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				try {
					initMinicatService(miniCatService, clazzs);
				} catch (Exception e) {
					LogUtil.log.error("启动出错", e);
				}
			}
		});
		try {
			miniCatService.openPort(MiniCatConfig.port, MiniCatConfig.sessionTimeout);
			queue.offer(1);
		} catch (Exception e) {
			LogUtil.log.error("启动失败", e);
			queue.offer(0);
		}
	}

	private static void initMinicatService(MiniCatService miniCatService, Class<?>... clazzs) throws Exception {
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
				initMiniCatHttpPart(servlet);
			}
			Filter filterFlag = clazz.getAnnotation(Filter.class);
			if (filterFlag != null && !StringUtil.isNullOrEmpty(filterFlag.value())) {
				HttpFilter filter = (HttpFilter) clazz.getDeclaredConstructor().newInstance();
				filter.setMapping(filterFlag.value());
				LogUtil.log.info("注册Filter>>" + clazz.getName() + ">>" + filterFlag.value());
				FilterContainer.pushFilter(filter);
				initMiniCatHttpPart(filter);
			}
		}
		Integer serverOpen = queue.take();
		if (serverOpen == 0) {
			return;
		}
		LogUtil.log
				.info("MiniCat:" + MiniCatConfig.port + "启动完成,耗时>>" + (System.currentTimeMillis() - startTime) + "ms");
		// 处理请求
		miniCatService.doService();

	}

	private static void initMiniCatHttpPart(MiniCatHttpPart part) {
		try {
			part.init();
		} catch (Exception e) {
			throw new RuntimeException("HTTP服务组件初始化失败>>" + part.getClass().getName(), e);
		}
	}

}
