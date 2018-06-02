package org.coody.framework.core.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.container.TaskContainer;
import org.coody.framework.core.container.TaskContainer.TaskEntity;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.PrintException;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.task.trigger.TaskTrigger;

/**
 * 切面加载器
 * 
 * @author Coody
 *
 */
public class InitRunLoader implements IcopLoader {

	private static final Logger logger = Logger.getLogger(InitRunLoader.class);

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		List<Runnable> inits = new ArrayList<Runnable>();
		for (Object bean : BeanContainer.getBeans()) {
			if (bean instanceof InitBeanFace) {
				// 初始化运行
				try {
					InitBeanFace face = (InitBeanFace) bean;
					inits.add(new Runnable() {
						@Override
						public void run() {
							face.init();
						}
					});
				} catch (Exception e) {
					PrintException.printException(logger, e);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(inits)) {
			new ThreadBlockPool().execute(inits);
		}
		// 执行定时任务
		for (TaskEntity task : TaskContainer.getTaskEntitys()) {
			Object bean = BeanContainer.getBean(task.getClazz());
			TaskTrigger.nextRun(bean, task.getMethod(), task.getCron(), null);
		}
	}

}
