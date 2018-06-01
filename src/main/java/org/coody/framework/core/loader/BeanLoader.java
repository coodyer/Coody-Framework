package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanConflictException;
import org.coody.framework.core.exception.BeanInitException;
import org.coody.framework.core.exception.BeanNameCreateException;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.proxy.CglibProxy;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * Bean加载器
 * 
 * @author Coody
 *
 */
public class BeanLoader implements IcopLoader {

	static CglibProxy proxy = new CglibProxy();

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> cla : clazzs) {
			if (cla.isAnnotation()) {
				continue;
			}
			if (cla.isInterface()) {
				continue;
			}
			if(Modifier.isAbstract(cla.getModifiers())){
				continue;
			}
			Annotation initBean = PropertUtil.getAnnotation(cla, InitBean.class);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			List<String> beanNames = BeanContainer.getBeanNames(cla);
			if (StringUtil.isNullOrEmpty(beanNames)) {
				throw new BeanNameCreateException(cla);
			}
			Object bean = proxy.getProxy(cla);
			if (bean == null) {
				throw new BeanInitException(cla);
			}
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
