package org.coody.framework.exception;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ErrorCronException extends IcopException {

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
