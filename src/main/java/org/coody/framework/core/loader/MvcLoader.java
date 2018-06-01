package org.coody.framework.core.loader;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.core.config.IcopConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.mvc.adapt.iface.IcopParamsAdapt;
import org.coody.framework.mvc.annotation.ParamsAdapt;
import org.coody.framework.mvc.annotation.PathBinding;
import org.coody.framework.mvc.container.MappingContainer;
import org.coody.framework.mvc.entity.MvcMapping;
import org.coody.framework.mvc.exception.MappingConflicException;

/**
 * MVC加载器
 * 
 * @author Administrator
 *
 */
public class MvcLoader implements IcopLoader {

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		for (Object bean:BeanContainer.getBeans()) {
			if (StringUtil.isNullOrEmpty(bean)) {
				continue;
			}
			Class<?> clazz=bean.getClass();
			if(ClassUtil.isCglibProxyClassName(bean.getClass().getName())){
				clazz=clazz.getSuperclass();
			}
			PathBinding classBindings = PropertUtil.getAnnotation(clazz, PathBinding.class);
			if (StringUtil.isNullOrEmpty(classBindings)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			ParamsAdapt clazzParamsAdapt = PropertUtil.getAnnotation(clazz, ParamsAdapt.class);
			for (String clazzBinding : classBindings.value()) {
				for (Method method : methods) {
					PathBinding methodBinding = PropertUtil.getAnnotation(method, PathBinding.class);
					if (StringUtil.isNullOrEmpty(methodBinding)) {
						continue;
					}
					for (String bindingPath : methodBinding.value()) {
						String path = StringUtil.formatPath(clazzBinding + "/" + bindingPath);
						if (MappingContainer.containsPath(path)) {
							throw new MappingConflicException(path);
						}
						Class<?> adaptClass = IcopConfig.DEFAULT_PARAM_ADAPT;
						ParamsAdapt methodParamsAdapt = PropertUtil.getAnnotation(method, ParamsAdapt.class);
						if (methodParamsAdapt == null) {
							if (clazzParamsAdapt != null) {
								adaptClass = clazzParamsAdapt.value();
							}
						} else {
							adaptClass = methodParamsAdapt.value();
						}
						MvcMapping mapping = new MvcMapping();
						mapping.setBean(bean);
						mapping.setPath(path);
						mapping.setParamsAdapt(((IcopParamsAdapt) adaptClass.newInstance()));
						mapping.setMethod(method);
						mapping.setParamTypes(PropertUtil.getMethodParas(method));
						MappingContainer.writeMapping(mapping);
					}
				}
			}
		}
	}

}
