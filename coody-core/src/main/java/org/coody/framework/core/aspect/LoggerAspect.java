package org.coody.framework.core.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.annotation.LogHead;
import org.coody.framework.core.point.AspectPoint;
import org.coody.framework.core.util.LoggerUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

@InitBean
public class LoggerAspect {

	
	/**
	 * 日志标头设置
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass=LogHead.class)
	public Object logMonitor(AspectPoint wrapper) throws Throwable{
		try {
			// AOP获取方法执行信息
			Method method = wrapper.getMethod();
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
			return wrapper.invoke();
		} finally {
			LoggerUtil.minusLog();
		}
	}
}
