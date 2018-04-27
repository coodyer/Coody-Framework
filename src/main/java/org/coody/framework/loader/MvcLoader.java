package org.coody.framework.loader;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.adapt.iface.IcopParamsAdapt;
import org.coody.framework.annotation.ParamsAdapt;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.config.IcopConfig;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.container.MappingContainer;
import org.coody.framework.entity.MvcMapping;
import org.coody.framework.exception.MappingConflicException;
import org.coody.framework.loader.base.IcopLoader;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

/**
 * MVC加载器
 * 
 * @author Administrator
 *
 */
public class MvcLoader implements IcopLoader {

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		for (Class<?> clazz : clazzs) {
			Object bean = BeanContainer.getBean(clazz);
			if (StringUtil.isNullOrEmpty(bean)) {
				continue;
			}
			PathBinding classBindings = clazz.getAnnotation(PathBinding.class);
			if (StringUtil.isNullOrEmpty(classBindings)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			ParamsAdapt clazzParamsAdapt = clazz.getAnnotation(ParamsAdapt.class);
			for (String clazzBinding : classBindings.value()) {
				for (Method method : methods) {
					PathBinding methodBinding = method.getAnnotation(PathBinding.class);
					if (StringUtil.isNullOrEmpty(methodBinding)) {
						continue;
					}
					for (String bindingPath : methodBinding.value()) {
						String path = StringUtil.formatPath(clazzBinding + "/" + bindingPath);
						if (MappingContainer.containsPath(path)) {
							throw new MappingConflicException(path);
						}
						Class<?> adaptClass = IcopConfig.DEFAULT_PARAM_ADAPT;
						ParamsAdapt methodParamsAdapt = method.getAnnotation(ParamsAdapt.class);
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
