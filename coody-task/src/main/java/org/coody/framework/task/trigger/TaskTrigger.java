package org.coody.framework.task.trigger;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.task.container.TaskContainer;
import org.coody.framework.task.container.TaskContainer.TaskEntity;
import org.coody.framework.task.cron.CronExpression;
import org.coody.framework.task.threadpool.TaskThreadPool;

@AutoBuild
public class TaskTrigger implements InitBeanFace {

	private static Map<Method, ZonedDateTime> cronExpressionMap = new ConcurrentHashMap<Method, ZonedDateTime>();

	public static Method getTriggerMethod() {
		Method[] methods = TaskTrigger.class.getDeclaredMethods();
		if (CommonUtil.isNullOrEmpty(methods)) {
			return null;
		}
		for (Method method : methods) {
			if ("taskTrigger".equals(method.getName())) {
				return method;
			}
		}
		return null;
	}

	public static void trigger(Object bean, Method method, String cron) {
		Date nextTime = getNextTime(method, cron);

		long timeRange = nextTime.getTime() - System.currentTimeMillis();

		TaskThreadPool.TASK_POOL.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					triggerNext(bean, method, cron);
				} catch (Exception e) {
					LogUtil.log.error("任务执行出错", e);
				}
			}
		}, timeRange, TimeUnit.MILLISECONDS);
	}

	private static Date getNextTime(Method method, String cron) {
		ZonedDateTime zonedDateTime = cronExpressionMap.get(method);
		CronExpression express = new CronExpression(cron);
		if (zonedDateTime == null) {
			zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.UTC);
		}
		zonedDateTime = express.nextTimeAfter(zonedDateTime);
		cronExpressionMap.put(method, zonedDateTime);
		Date nextTime = Date.from(zonedDateTime.toInstant());
		return nextTime;
	}

	private static void triggerNext(Object bean, Method method, String cron) {
		Object[] params = {};
		try {
			method.invoke(bean, params);
		} catch (Exception e) {
			LogUtil.log.error("任务执行出错", e);
		} finally {
			trigger(bean, method, cron);
		}
	}

	public void init() {
		if (CommonUtil.isNullOrEmpty(TaskContainer.getTaskEntitys())) {
			return;
		}
		ThreadBlockPool threadBlockPool = new ThreadBlockPool(TaskContainer.getTaskEntitys().size(), 60);
		for (TaskEntity task : TaskContainer.getTaskEntitys()) {
			Object bean = BeanContainer.getBean(task.getClazz());
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					TaskTrigger.trigger(bean, task.getMethod(), task.getCron());
				}
			};
			threadBlockPool.pushTask(runnable);
		}
		threadBlockPool.execute();
	}
}
