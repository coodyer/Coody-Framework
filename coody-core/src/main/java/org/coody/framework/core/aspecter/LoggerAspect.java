package org.coody.framework.core.aspecter;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.annotation.LogHead;
import org.coody.framework.core.entity.AspectPoint;
import org.coody.framework.core.util.LoggerUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

@AutoBuild
public class LoggerAspect {

	@Around(annotationClass=LogHead.class)
	public Object logMonitor(AspectPoint able) throws Throwable{
		try {
			// AOP获取方法执行信息
			Method method = able.getAbler().getMethod();
			Class<?> clazz = PropertUtil.getClass(method);
			String module = LoggerUtil.getCurrLog();
			if (!StringUtil.isNullOrEmpty(module)) {
				module += "_";
			}
			String classLog = LoggerUtil.getClassLog(clazz);
			if (!StringUtil.isNullOrEmpty(classLog)) {
				module += classLog;
			}
			if (!StringUtil.isNullOrEmpty(module)) {
				module += ".";
			}
			String methodLog = LoggerUtil.getMethodLog(method);
			if (!StringUtil.isNullOrEmpty(methodLog)) {
				module += methodLog;
			} else {
				module += method.getName();
			}
			LoggerUtil.writeLog(module);
			return able.invoke();
		} finally {
			LoggerUtil.minusLog();
		}
	}
}
