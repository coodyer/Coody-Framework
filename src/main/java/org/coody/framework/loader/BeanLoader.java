package org.coody.framework.loader;

import java.util.List;
import java.util.Set;

import org.coody.framework.container.BeanContainer;
import org.coody.framework.exception.BeanConflictException;
import org.coody.framework.loader.base.IcopLoader;
import org.coody.framework.proxy.CglibProxy;
import org.coody.framework.util.StringUtil;

/**
 * Bean加载器
 * @author Administrator
 *
 */
public class BeanLoader implements IcopLoader{
	

	static CglibProxy proxy = new CglibProxy();

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> cla : clazzs) {
			List<String> beanNames = BeanContainer.getBeanNames(cla);
			if (StringUtil.isNullOrEmpty(beanNames)) {
				continue;
			}
			Object bean = proxy.getProxy(cla);
			for (String beanName : beanNames) {
				if (StringUtil.isNullOrEmpty(beanName)) {
					continue;
				}
				if (BeanContainer.containsBean(beanName)) {
					throw new BeanConflictException(beanName);
				}
				BeanContainer.writeBean(beanName, bean);
			}
		}
	}

}
