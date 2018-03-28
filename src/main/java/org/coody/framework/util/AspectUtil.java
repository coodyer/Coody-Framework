package org.coody.framework.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coody.framework.annotation.LogHead;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.constant.GeneralFinal;
import org.coody.framework.container.ThreadContainer;
import org.coody.framework.entity.BeanEntity;

import com.alibaba.fastjson.JSON;

public class AspectUtil {


	public static String getFieldKey(Class<?> clazz, Method method, Object[] paras, String key, String[] fields) {
		if (StringUtil.isNullOrEmpty(key)) {
			key = getMethodKey(clazz, method);
			key = key.replace(".", "_");
			key = key.replace(",", "_");
		}
		StringBuilder paraKey = new StringBuilder();
		for (String field : fields) {
			Object paraValue = AspectUtil.getMethodPara(method, field, paras);
			if (StringUtil.isNullOrEmpty(paraValue)) {
				paraValue = "";
			}
			paraKey.append("_").append(JSONWriter.write(paraValue));
		}
		key = key + "_" + EncryptUtil.md5Code(paraKey.toString());
		return key;
	}

	// 将对象内所有字段名、字段值拼接成字符串，用于缓存Key
	public static String getBeanKey(Object... obj) {
		if (StringUtil.isNullOrEmpty(obj)) {
			return "";
		}
		String str = JSON.toJSONString(obj);
		return EncryptUtil.md5Code(str);
	}
	public static String getMethodUnionKey(Method method){
		String paraKey = "";
		List<BeanEntity> beanEntitys = PropertUtil.getMethodParas(method);
		if (!StringUtil.isNullOrEmpty(beanEntitys)) {
			Set<String> methodParas = new HashSet<String>();
			for (BeanEntity entity : beanEntitys) {
				String methodParaLine = entity.getFieldType().getName() + " " + entity.getFieldName();
				methodParas.add(methodParaLine);
			}
			paraKey = StringUtil.collectionMosaic(methodParas, ",");
		}
		Class<?> clazz=PropertUtil.getClass(method);
		String methodKey = clazz.getName() + "." + method.getName() + "(" + paraKey + ")";
		return methodKey;
	}
	public static String getMethodKey(Class<?> clazz, Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName()).append(".").append(method.getName());
		Class<?>[] paraTypes = method.getParameterTypes();
		sb.append("(");
		if (!StringUtil.isNullOrEmpty(paraTypes)) {
			for (int i = 0; i < paraTypes.length; i++) {
				sb.append(paraTypes[i].getName());
				if (i < paraTypes.length - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return FrameworkConstant.AUTO_CACHE_KEY + "-" + sb.toString();
	}
	public static String getClassLog(Class<?> clazz) {
		LogHead handle = clazz.getAnnotation(LogHead.class);
		if (handle == null) {
			return clazz.getSimpleName();
		}
		return handle.value();
	}
	public static String getMethodLog(Method method) {
		LogHead handle = method.getAnnotation(LogHead.class);
		if (handle == null) {
			return null;
		}
		return handle.value();
	}
	public static void writeLog(String module) {
		ThreadContainer.set(GeneralFinal.LOGGER_WRAPPER, module);
	}
	
	public static String minusLog() {
		String logHead = ThreadContainer.get(GeneralFinal.LOGGER_WRAPPER);
		if (logHead == null) {
			return "";
		}
		String tabs[] = logHead.split("_");
		if (tabs.length == 1) {
			ThreadContainer.set(GeneralFinal.LOGGER_WRAPPER, "");
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tabs.length - 1; i++) {
			if (!StringUtil.isNullOrEmpty(sb)) {
				sb.append("_");
			}
			sb.append(tabs[i]);
		}
		ThreadContainer.set(GeneralFinal.LOGGER_WRAPPER, sb.toString());
		return sb.toString();
	}
	public static Object getMethodPara(Method method, String fieldName, Object[] args) {
		List<BeanEntity> beanEntitys = PropertUtil.getMethodParas(method);
		if (StringUtil.isNullOrEmpty(beanEntitys)) {
			return "";
		}
		String[] fields = fieldName.split("\\.");
		BeanEntity entity = (BeanEntity) PropertUtil.getByList(beanEntitys, "fieldName", fields[0]);
		if (StringUtil.isNullOrEmpty(entity)) {
			return "";
		}
		Object para = args[beanEntitys.indexOf(entity)];
		if (fields.length > 1 && para != null) {
			for (int i = 1; i < fields.length; i++) {
				para = PropertUtil.getFieldValue(para, fields[i]);
			}
		}
		return para;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String field){
		return (T) ThreadContainer.get(field);
	}
	public static void writeBean(String field,Object bean){
		ThreadContainer.set(field, bean);
	}
	public static String getCurrLog() {
		return ThreadContainer.get(GeneralFinal.LOGGER_WRAPPER);
	}

}
