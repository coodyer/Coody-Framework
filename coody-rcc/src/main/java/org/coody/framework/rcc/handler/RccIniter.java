package org.coody.framework.rcc.handler;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.rcc.annotation.RccClient;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.registry.iface.RccRegistry;

public class RccIniter implements InitBeanFace {

	private static RccRegistry registry;

	private static RccConfig config;

	@Override
	public void init() throws Exception {
		if (registry == null) {
			registry = BeanContainer.getBean(RccRegistry.class);
		}
		if (registry == null) {
			throw new RccException("未找到注册中心");
		}
		if (config == null) {
			registry = BeanContainer.getBean(RccConfig.class);
		}
		if (config == null) {
			throw new RccException("未找到RccConfig配置");
		}
		Set<Method> methods = PropertUtil.getMethods(this.getClass());
		for (Method method : methods) {
			RccClient rccClient = PropertUtil.getAnnotation(method, RccClient.class);
			if (rccClient == null) {
				continue;
			}
			registry.register(MethodSignUtil.getMethodUnionKey(method), config.getHost(), config.getPort(),
					config.getPr());
		}

	}

}
