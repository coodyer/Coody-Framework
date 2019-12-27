package org.coody.framework.minicat.processor;

import org.coody.framework.minicat.builder.iface.HttpBuilder;
import org.coody.framework.minicat.container.ApplicationFilterChain;
import org.coody.framework.minicat.container.ServletContainer;
import org.coody.framework.minicat.servlet.HttpServlet;
import org.coody.framework.minicat.threadpool.MiniCatThreadPool;

public class MinicatProcess {


	public static void doService(HttpBuilder build) throws 	Exception {
			HttpServlet servlet = ServletContainer.getServlet(build.getRequest().getRequestURI());
			ApplicationFilterChain chain=new ApplicationFilterChain(servlet);
			chain.doFilter(build.getRequest(), build.getResponse());
		
	}

	static {
		MiniCatThreadPool.MINICAT_POOL.execute(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
}
