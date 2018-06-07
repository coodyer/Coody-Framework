package org.coody.framework.web.listen;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.coody.framework.core.CoreApp;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.util.StringUtil;

public class IcopServletListen implements ServletContextListener {

	Logger logger = Logger.getLogger(IcopServletListen.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("运行contextDestroyed");
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			String packet = event.getServletContext().getInitParameter("scanPacket");
			if (StringUtil.isNullOrEmpty(packet)) {
				logger.error("启动参数:scanPacket为空");
				return;
			}
			String initLoader=event.getServletContext().getInitParameter("initLoader");
			String []loaders=initLoader.split(",");
			for(String loader:loaders){
				CoreApp.pushLoader((IcopLoader)Class.forName(loader).newInstance());
			}
			String[] packets = packet.split(",");
			CoreApp.init(packets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}