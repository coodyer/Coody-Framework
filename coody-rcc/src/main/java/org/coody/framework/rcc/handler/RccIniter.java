package org.coody.framework.rcc.handler;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.threadpool.SysThreadPool;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.rcc.annotation.RccService;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.instance.RccKeepInstance;
import org.coody.framework.rcc.registry.iface.RccRegistry;

@AutoBuild
public class RccIniter implements InitBeanFace {

	@Override
	public void init() throws Exception {
		RccRegistry registry = BeanContainer.getBean(RccRegistry.class);
		if (registry == null) {
			throw new RccException("未找到注册中心");
		}
		SysThreadPool.THREAD_POOL.execute(new Runnable() {

			@Override
			public void run() {
				RccKeepInstance.signaler.doService(RccConfig.port);
			}
		});
		SysThreadPool.THREAD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				ThreadBlockPool pool = new ThreadBlockPool(100, 60);
				for (Class<?> clazz : BeanContainer.getClazzContainer()) {
					if (clazz.isAnnotation()) {
						continue;
					}
					if (CommonUtil.isNullOrEmpty(clazz.getAnnotations())) {
						continue;
					}
					if (clazz.isInterface()) {
						continue;
					}
					RccService rcc = PropertUtil.getAnnotation(clazz, RccService.class);
					if (rcc == null) {
						continue;
					}
					Set<Method> methods = PropertUtil.getMethods(clazz);
					for (Method method : methods) {
						pool.pushTask(new Runnable() {
							@Override
							public void run() {
								String key = String.format("%s-%s", rcc.value(),
										MethodSignUtil.getGeneralKeyByMethod(method));
								registry.register(key, RccConfig.host, RccConfig.port, RccConfig.pr);
							}
						});
					}
				}
				pool.execute();
			}
		});
	}

}
