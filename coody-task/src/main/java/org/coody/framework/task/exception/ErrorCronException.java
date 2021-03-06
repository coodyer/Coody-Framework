package org.coody.framework.task.exception;

import java.lang.reflect.Method;

import org.coody.framework.core.exception.base.CoodyException;

@SuppressWarnings("serial")
public class ErrorCronException extends CoodyException {

	public ErrorCronException(String cron) {
		super("Cron表达式有误:" + cron);
	}

	public ErrorCronException(String cron, Method method) {
		super(method.getName() + ":Cron表达式有误>>" + cron);
	}

	public ErrorCronException(String cron, Exception e) {
		super("Cron表达式有误:" + cron, e);
	}
}
