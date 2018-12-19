package org.coody.framework.task.trigger;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.entity.AspectPoint;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.task.annotation.CronTask;
import org.coody.framework.task.container.TaskContainer;
import org.coody.framework.task.container.TaskContainer.TaskEntity;
import org.coody.framework.task.cron.CronExpression;
import org.coody.framework.task.threadpool.TaskThreadPool;

@AutoBuild
public class TaskTrigger implements InitBeanFace {

	private static Map<Method, ZonedDateTime> cronExpressionMap = new ConcurrentHashMap<Method, ZonedDateTime>();
	static Logger logger = Logger.getLogger(TaskTrigger.class);

	public static Method getTriggerMethod() {
		Method[] methods = TaskTrigger.class.getDeclaredMethods();
		if (StringUtil.isNullOrEmpty(methods)) {
			return null;
		}
		for (Method method : methods) {
			if ("taskTrigger".equals(method.getName())) {
				return method;
			}
		}
		return null;
	}

	public static void trigger(Object bean, Method method, String cron, ZonedDateTime zonedDateTime) {
		CronExpression express = new CronExpression(cron);
		if (zonedDateTime == null) {
			zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.UTC);
		}
		zonedDateTime = express.nextTimeAfter(zonedDateTime);
		cronExpressionMap.put(method, zonedDateTime);
		Date nextTime = Date.from(zonedDateTime.toInstant());
		long timeRage = nextTime.getTime() - System.currentTimeMillis();
		TaskThreadPool.TASK_POOL.schedule(new Runnable() {
			@Override
			public void run() {
				Object[] params = {};
				try {
					method.invoke(bean, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, timeRage, TimeUnit.MILLISECONDS);
	}

	/**
	 * 定时任务管理
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass = CronTask.class)
	public Object taskTrigger(AspectPoint point) throws Throwable {
		Method method = point.getAbler().getMethod();
		CronTask cronTask = method.getAnnotation(CronTask.class);
		Object bean = point.getAbler().getBean();
		String cron = cronTask.value();
		try {
			return point.invoke();
		} finally {
			ZonedDateTime zonedDateTime = cronExpressionMap.get(method);
			trigger(bean, method, cron, zonedDateTime);
		}
	}

	public void init() {
		for (TaskEntity task : TaskContainer.getTaskEntitys()) {
			Object bean = BeanContainer.getBean(task.getClazz());
			TaskTrigger.trigger(bean, task.getMethod(), task.getCron(), null);
		}
	}

	public static void main(String[] args) {
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.UTC);
		
		CronExpression express = new CronExpression("0/1 * * * * ? ");
		long t1 = System.currentTimeMillis();
		zonedDateTime = express.nextTimeAfter(zonedDateTime);
		
		System.out.println(System.currentTimeMillis() - t1);
	}

}
