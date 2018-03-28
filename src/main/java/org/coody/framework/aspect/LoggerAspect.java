package org.coody.framework.aspect;

import java.lang.reflect.Method;

import org.coody.framework.annotation.Around;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.LogHead;
import org.coody.framework.point.AspectPoint;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

@InitBean
public class LoggerAspect {

	
	/**
	 * 日志标头设置
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass=LogHead.class)
	public Object transacted(AspectPoint wrapper) throws Throwable{
		try {
			// AOP获取方法执行信息
			Method method = wrapper.getMethod();
			Class<?> clazz = PropertUtil.getClass(method);
			String module = AspectUtil.getCurrLog();
			if (!StringUtil.isNullOrEmpty(module)) {
				module += "_";
			}
			String classLog = AspectUtil.getClassLog(clazz);
			if (!StringUtil.isNullOrEmpty(classLog)) {
				module += classLog;
			}
			if (!StringUtil.isNullOrEmpty(module)) {
				module += ".";
			}
			String methodLog = AspectUtil.getMethodLog(method);
			if (!StringUtil.isNullOrEmpty(methodLog)) {
				module += methodLog;
			} else {
				module += method.getName();
			}
			AspectUtil.writeLog(module);
			return wrapper.invoke();
		} finally {
			AspectUtil.minusLog();
		}
	}
}
