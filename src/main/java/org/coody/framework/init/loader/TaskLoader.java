package org.coody.framework.init.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.annotation.CronTask;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.init.loader.face.IcopLoader;
import org.coody.framework.task.TaskTrigger;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.ClassUtil;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

/**
 * 定时任务加载器
 * 
 * @author Administrator
 *
 */
public class TaskLoader implements IcopLoader {

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Object bean:BeanContainer.getBeans()) {
			if (StringUtil.isNullOrEmpty(bean)) {
				continue;
			}
			Class<?> clazz=bean.getClass();
			if(ClassUtil.isCglibProxyClassName(bean.getClass().getName())){
				clazz=clazz.getSuperclass();
			}
			Annotation initBean = PropertUtil.getAnnotation(clazz, InitBean.class);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask cronTask = PropertUtil.getAnnotation(method, CronTask.class);
				if (StringUtil.isNullOrEmpty(cronTask) || StringUtil.isNullOrEmpty(cronTask.value())) {
					continue;
				}
				AspectEntity aspectEntity = new AspectEntity();
				// 装载切面控制方法
				aspectEntity.setAnnotationClass(new Class<?>[] { CronTask.class });
				aspectEntity.setAspectInvokeMethod(TaskTrigger.getTriggerMethod());
				FrameworkConstant.writeToAspectMap(AspectUtil.getBeanKey(TaskTrigger.getTriggerMethod()), aspectEntity);
			}
		}
	}

}
