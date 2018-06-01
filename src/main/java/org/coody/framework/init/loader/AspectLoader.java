package org.coody.framework.init.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.annotation.Around;
import org.coody.framework.annotation.Arounds;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.init.loader.face.IcopLoader;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

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
					String methodKey = AspectUtil.getBeanKey(method);
					FrameworkConstant.writeToAspectMap(methodKey, aspectEntity);
				}
			}
		}
	}

}
