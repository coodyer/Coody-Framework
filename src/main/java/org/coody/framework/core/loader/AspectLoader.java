package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.Arounds;
import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.entity.AspectEntity;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * 切面加载器
 * 
 * @author Administrator
 *
 */
public class AspectLoader implements IcopLoader {

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> cla : clazzs) {

			Annotation initBean = PropertUtil.getAnnotation(cla, InitBean.class);
			if (initBean == null) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				Arounds arounds=PropertUtil.getAnnotation(method, Arounds.class);
				if (StringUtil.isNullOrEmpty(arounds)) {
					continue;
				}
				Around [] aroundArgs=arounds.value();
				if (StringUtil.isNullOrEmpty(aroundArgs)) {
					continue;
				}
				for (Around around : aroundArgs) {
					if (around == null) {
						continue;
					}
					if (StringUtil.isAllNull(around.annotationClass(), around.classMappath(), around.annotationClass(),
							around.methodMappath())) {
						continue;
					}
					AspectEntity aspectEntity = new AspectEntity();
					// 装载切面控制方法
					aspectEntity.setAnnotationClass(around.annotationClass());
					aspectEntity.setMethodMappath(around.methodMappath());
					aspectEntity.setClassMappath(around.classMappath());
					aspectEntity.setAspectInvokeMethod(method);
					String methodKey = MethodSignUtil.getBeanKey(method);
					FrameworkConstant.writeToAspectMap(methodKey, aspectEntity);
				}
			}
		}
	}

}
