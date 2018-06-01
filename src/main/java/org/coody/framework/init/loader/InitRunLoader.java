package org.coody.framework.init.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coody.framework.annotation.CronTask;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.exception.ErrorCronException;
import org.coody.framework.iface.InitFace;
import org.coody.framework.init.loader.face.IcopLoader;
import org.coody.framework.task.TaskTrigger;
import org.coody.framework.util.PrintException;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

/**
 * 切面加载器
 * 
 * @author Administrator
 *
 */
public class InitRunLoader implements IcopLoader {
	

	private static final Logger logger = Logger.getLogger(IcopLoader.class);

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		for (Class<?> clazz : clazzs) {
			Annotation initBean = PropertUtil.getAnnotation(clazz, InitBean.class);
			if (initBean == null) {
				continue;
			}
			Object bean = BeanContainer.getBean(clazz);
			if (InitFace.class.isAssignableFrom(clazz)) {
				// 初始化运行
				try {
					InitFace face = (InitFace) bean;
					if (StringUtil.isNullOrEmpty(face)) {
						continue;
					}
					face.init();
				} catch (Exception e) {
					PrintException.printException(logger, e);
				}
			}
			// 执行定时任务
			Method[] methods = clazz.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask task =PropertUtil.getAnnotation(method, CronTask.class);
				if (task == null) {
					continue;
				}
				try {
					if (StringUtil.isNullOrEmpty(task.value())) {
						PrintException.printException(logger, new ErrorCronException(task.value(), method));
						continue;
					}
					TaskTrigger.nextRun(bean, method, task.value(), null);
				} catch (Exception e) {
					PrintException.printException(logger, new ErrorCronException(
							"CRON有误:" + bean.getClass() + ":" + method.getName() + ",Cron:" + task.value()));
					continue;
				}
			}
		}
	}

}
