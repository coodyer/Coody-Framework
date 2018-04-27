package org.coody.framework.loader;

import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.annotation.CronTask;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.loader.base.IcopLoader;
import org.coody.framework.task.TaskTrigger;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.StringUtil;

/**
 * 定时任务加载器
 * @author Administrator
 *
 */
public class TaskLoader implements IcopLoader{

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> cla : clazzs) {
			InitBean initBean = cla.getAnnotation(InitBean.class);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask cronTask = method.getAnnotation(CronTask.class);
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
