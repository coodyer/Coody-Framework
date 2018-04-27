package org.coody.framework.loader;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.annotation.Around;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.loader.base.IcopLoader;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.StringUtil;

/**
 * 切面加载器
 * @author Administrator
 *
 */
public class AspectLoader implements IcopLoader{

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> cla : clazzs) {

			if (cla.getAnnotation(InitBean.class) == null && cla.getAnnotation(PathBinding.class) == null) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				Around[] arounds = method.getAnnotationsByType(Around.class);
				if (StringUtil.isNullOrEmpty(arounds)) {
					continue;
				}
				for (Around around : arounds) {
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
