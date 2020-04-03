package org.coody.framework.core.util.dynamic;

import java.util.Set;

import org.coody.framework.core.util.dynamic.classloader.ClassLoaderContainer;
import org.coody.framework.core.util.dynamic.compiler.SourceCodeCompiler;

public class DynamicContainerMaker {

	public static Class<?> makeDynamicContainer(String packager, String clazzer, Set<String> fields) {
		String sourceCode = makeSourceCoude(packager, clazzer, fields);
		byte[] clazzBytes = SourceCodeCompiler.compile(sourceCode);
		return ClassLoaderContainer.simpleClassLoader.defineClassForName(packager + "." + clazzer, clazzBytes);
	}

	private static String makeSourceCoude(String packager, String clazz, Set<String> fields) {
		StringBuilder sourceCode = new StringBuilder();
		sourceCode.append("package ").append(packager).append(";").append("\r\n");
		sourceCode.append("public class ").append(clazz)
				.append(" implements com.xr8848.souler.common.utils.magic.iface.DynamicContainer{").append("\r\n")
				.append("\r\n");

		// 生成字段列表
		for (String field : fields) {
			String realField = makeActuallyFieldName(field);
			sourceCode.append("	private Object ").append(realField).append(";\r\n").append("\r\n");
		}
		sourceCode.append("\r\n");
		// 生成get方法
		sourceCode.append("	public <T> T get(String field) {").append("\r\n");
		sourceCode.append("		switch (field) {").append("\r\n");
		for (String field : fields) {
			sourceCode.append("		  case \"").append(field).append("\":").append("\r\n");
			String realField = makeActuallyFieldName(field);
			sourceCode.append("		    return (T)").append(realField).append(";\r\n");
		}
		sourceCode.append("		  default:").append("\r\n");
		sourceCode.append("		    break;").append("\r\n");
		sourceCode.append(" 		 }").append("\r\n");
		sourceCode.append("		return null;").append("\r\n");
		sourceCode.append(" 	}").append("\r\n");
		// 生成set方法
		sourceCode.append("	public boolean set(String field,Object value) {").append("\r\n");
		sourceCode.append("		switch (field) {").append("\r\n");
		for (String field : fields) {
			sourceCode.append("		  case \"").append(field).append("\":").append("\r\n");
			String realField = makeActuallyFieldName(field);
			sourceCode.append("		   ").append(realField).append(" = value").append(";\r\n");
			sourceCode.append("		   return true").append(";\r\n");
		}
		sourceCode.append("		  default:").append("\r\n");
		sourceCode.append("		    break;").append("\r\n");
		sourceCode.append(" 		 }").append("\r\n");
		sourceCode.append("		return false;").append("\r\n");
		sourceCode.append(" 	}").append("\r\n");
		sourceCode.append(" }").append("\r\n");
		return sourceCode.toString();
	}

	private static String makeActuallyFieldName(String field) {
		String actuallyField = field.replace(".", "_").replace("	", "").replace(" ", "");
		if (isNumeric(field)) {
			field = "c" + field;
		}
		return actuallyField;
	}

	private static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
