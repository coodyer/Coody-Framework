package org.coody.framework.core.aspecter;

import java.lang.reflect.Method;

import org.apache.log4j.MDC;
import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.annotation.LogFlag;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

@AutoBuild
public class LoggerAspect {

	@Around(annotationClass = LogFlag.class)
	public Object logMonitor(AspectPoint able) throws Throwable {
		try {
			// AOP启动监听
			Method method = able.getAbler().getMethod();
			Class<?> clazz = able.getAbler().getClazz();
			String module = getCurrentFlag();
			if (module == null) {
				module = "";
			}
			if (!StringUtil.isNullOrEmpty(module)) {
				if (module.endsWith("]")) {
					module = module.substring(0, module.length() - 1);
				}
				module += ".";
			}
			String classLog = getClassFlag(clazz);
			if (!StringUtil.isNullOrEmpty(classLog)) {
				module += classLog;
			}
			if (!StringUtil.isNullOrEmpty(classLog)) {
				module += ".";
			}
			String methodLog = getMethodFlag(method);
			if (!StringUtil.isNullOrEmpty(methodLog)) {
				module += methodLog;
			} else {
				module += method.getName();
			}
			writeLogFlag(module);
			return able.invoke();
		} finally {
			wipeCurrentFlag();
		}
	}

	public static String getMethodFlag(Method method) {
		LogFlag flag = PropertUtil.getAnnotation(method, LogFlag.class);
		if (flag != null) {
			return flag.value();
		}
		return "";
	}

	public static String getClassFlag(Class<?> clazz) {
		LogFlag flag = PropertUtil.getAnnotation(clazz, LogFlag.class);
		if (flag != null) {
			return flag.value();
		}
		return "";
	}

	public static void writeLogFlag(String module) {
		if (!module.startsWith("[")) {
			module = "[" + module;
		}
		if (!module.endsWith("]")) {
			module = module + "]";
		}
		MDC.put("logFlag", module);
	}

	public static String getCurrentFlag() {
		return (String) MDC.get("logFlag");
	}

	public static String wipeCurrentFlag() {
		String logFlag = (String) MDC.get("logFlag");
		if (logFlag == null) {
			return "";
		}
		String[] tabs = logFlag.split(">");
		if (tabs.length == 1) {
			MDC.put("logFlag", "");
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tabs.length - 1; i++) {
			if (!StringUtil.isNullOrEmpty(sb)) {
				sb.append(">");
			}
			sb.append(tabs[i]);
		}
		MDC.put("logFlag", sb.toString());
		return sb.toString();
	}
}
