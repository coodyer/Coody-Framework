package org.coody.framework.web.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.coody.framework.core.CoreApp;
import org.coody.framework.core.build.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.exception.InitException;
import org.coody.framework.core.util.StringUtil;

public class CoodyServletListen implements ServletContextListener {

	Logger logger = Logger.getLogger(CoodyServletListen.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("运行contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			String config = event.getServletContext().getInitParameter("configPath");
			if (StringUtil.isNullOrEmpty(config)) {
				throw new InitException("配置为空 >>configPath");
			}
			ConfigBuilder.builder(config);
			//载入框架配置
			CoodyConfig coodyConfig=new CoodyConfig();
			coodyConfig.init();
			//框架启动
			CoreApp.init(coodyConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
