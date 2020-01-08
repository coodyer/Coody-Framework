package org.coody.framework.task.loader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.annotation.Order;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.task.annotation.CronTask;
import org.coody.framework.task.container.TaskContainer;

/**
 * 定时任务加载器
 * 
 * @author Coody
 *
 */
@Order(4)
public class TaskLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (CommonUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (clazz.isInterface()) {
				continue;
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}
			if (clazz.isEnum()) {
				continue;
			}
			AutoBuild initBean = PropertUtil.getAnnotation(clazz, AutoBuild.class);
			if (CommonUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			if (CommonUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask cronTask = PropertUtil.getAnnotation(method, CronTask.class);
				if (cronTask == null) {
					continue;
				}
				LogUtil.log.debug("初始化定时任务 >>" + cronTask.value() + ":" + MethodSignUtil.getKeyByMethod(clazz, method));
				TaskContainer.setTaskEntity(clazz, method, cronTask.value());
			}
		}
	}

}
