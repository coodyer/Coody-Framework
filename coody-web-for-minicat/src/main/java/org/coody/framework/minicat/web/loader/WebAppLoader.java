package org.coody.framework.minicat.web.loader;

import java.lang.reflect.Method;

import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.clazz.ClassUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.web.adapter.iface.CoodyParameterAdapter;
import org.coody.framework.minicat.web.annotation.ParamsAdapt;
import org.coody.framework.minicat.web.annotation.PathBinding;
import org.coody.framework.minicat.web.constant.MvcContant;
import org.coody.framework.minicat.web.container.MappingContainer;
import org.coody.framework.minicat.web.entity.MvcMapping;
import org.coody.framework.minicat.web.exception.MappingConflicException;

/**
 * MVC加载器
 * 
 * @author Coody
 *
 */
public class WebAppLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			if (CommonUtil.isNullOrEmpty(bean)) {
				continue;
			}
			Class<?> clazz = ClassUtil.getSourceClass(bean.getClass());
			PathBinding classBindings = PropertUtil.getAnnotation(clazz, PathBinding.class);
			if (CommonUtil.isNullOrEmpty(classBindings)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			ParamsAdapt clazzParamsAdapt = PropertUtil.getAnnotation(clazz, ParamsAdapt.class);
			for (String clazzBinding : classBindings.value()) {
				for (Method method : methods) {
					PathBinding methodBinding = PropertUtil.getAnnotation(method, PathBinding.class);
					if (CommonUtil.isNullOrEmpty(methodBinding)) {
						continue;
					}
					for (String bindingPath : methodBinding.value()) {
						String path = CommonUtil.formatPath(clazzBinding + "/" + bindingPath);
						if (MappingContainer.containsPath(path)) {
							throw new MappingConflicException(path);
						}
						Class<?> adaptClass = MvcContant.DEFAULT_PARAM_ADAPT;
						ParamsAdapt methodParamsAdapt = PropertUtil.getAnnotation(method, ParamsAdapt.class);
						if (methodParamsAdapt == null) {
							if (clazzParamsAdapt != null) {
								adaptClass = clazzParamsAdapt.value();
							}
						} else {
							adaptClass = methodParamsAdapt.value();
						}
						LogUtil.log
								.debug("初始化Mapping地址 >>" + path + ":" + MethodSignUtil.getKeyByMethod(clazz, method));
						MvcMapping mapping = new MvcMapping();
						mapping.setBean(bean);
						mapping.setPath(path);
						mapping.setParamsAdapt(((CoodyParameterAdapter) adaptClass.newInstance()));
						mapping.setMethod(method);
						mapping.setParameters(PropertUtil.getMethodParameters(method));
						MappingContainer.writeMapping(mapping);
					}
				}
			}
		}
	}

}