package org.coody.framework.checker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.checker.annotation.ParamCheck;
import org.coody.framework.checker.exception.FormatErrorException;
import org.coody.framework.checker.exception.NullableException;
import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.match.MatchUtil;
import org.coody.framework.core.util.reflex.PropertUtil;

/**
 * 
 * @author Coody
 * @date 2018年10月31日
 */
public class CheckerProcessor {

	private static Map<Class<?>, Map<String, CheckEntity>> VERFICATION_CONTAINER = new ConcurrentHashMap<Class<?>, Map<String, CheckEntity>>();

	private static Map<String, CheckEntity> getVerficationInfo(Class<?> clazz) {
		if (VERFICATION_CONTAINER.containsKey(clazz)) {
			return VERFICATION_CONTAINER.get(clazz);
		}
		List<FieldEntity> entitys = PropertUtil.getBeanFields(clazz);
		Map<String, CheckEntity> checkMap = new HashMap<String, CheckEntity>(8);
		for (FieldEntity field : entitys) {
			ParamCheck check = field.getSourceField().getAnnotation(ParamCheck.class);
			if (check == null) {
				continue;
			}
			CheckEntity checkEntity = new CheckEntity();
			checkEntity.setField(field);
			checkEntity.setParamCheck(check);
			checkMap.put(field.getFieldName(), checkEntity);
			if (BaseModel.class.isAssignableFrom(field.getFieldType())) {
				Map<String, CheckEntity> childCheckMap = getVerficationInfo(field.getFieldType());
				if (!CommonUtil.isNullOrEmpty(childCheckMap)) {
					for (String key : childCheckMap.keySet()) {
						checkMap.put(field.getFieldName() + "." + key, childCheckMap.get(key));
					}
				}
				continue;
			}
		}
		VERFICATION_CONTAINER.put(clazz, checkMap);
		return checkMap;
	}

	public static void doVerfication(BaseModel model) {
		if (model == null) {
			return;
		}
		Map<String, CheckEntity> checkInfo = getVerficationInfo(model.getClass());
		for (String fieldName : checkInfo.keySet()) {
			CheckEntity checkEntity = checkInfo.get(fieldName);
			Object obj = checkEntity.getField().get(model);
			String error = checkEntity.getParamCheck().errorMsg();
			// 数据可空验证
			if (!checkEntity.getParamCheck().allowNull()) {
				if (CommonUtil.isNullOrEmpty(obj)) {
					if (CommonUtil.isNullOrEmpty(error)) {
						error = "参数不能为空";
					}
					String errorMsg = error + ":" + fieldName;
					throw new NullableException(errorMsg);
				}
			}
			if (CommonUtil.isNullOrEmpty(obj)) {
				if (!CommonUtil.isNullOrEmpty(checkEntity.getParamCheck().orNulls())) {
					String[] orNulls = checkEntity.getParamCheck().orNulls();
					String currentNode = getCurrentNode(fieldName);
					if (!CommonUtil.isNullOrEmpty(currentNode)) {
						currentNode += ".";
					}
					for (int i = 0; i < orNulls.length; i++) {
						orNulls[i] = currentNode + orNulls[i];
					}
					List<Object> values = PropertUtil.getFieldValues(model, orNulls);
					if (CommonUtil.allIsNullOrEmpty(values.toArray())) {
						if (CommonUtil.isNullOrEmpty(error)) {
							error = "参数不能同时为空";
						}
						String errorMsg = error + ":" + fieldName + ":"
								+ checkEntity.getParamCheck().orNulls().toString();
						throw new NullableException(errorMsg);
					}
				}
				continue;
			}
			if (CommonUtil.isNullOrEmpty(checkEntity.getParamCheck().format())) {
				continue;
			}
			// 数据格式验证
			String currMatcher = null;
			for (String matcher : checkEntity.getParamCheck().format()) {
				if (MatchUtil.isMatcher(obj.toString(), matcher)) {
					currMatcher = null;
					break;
				}
				currMatcher = matcher;
			}
			if (!CommonUtil.isNullOrEmpty(currMatcher)) {
				if (CommonUtil.isNullOrEmpty(error)) {
					error = "参数格式不正确";
				}
				String errorMsg = error + ":" + fieldName + ":" + obj.toString() + ",format:(" + currMatcher + ")";
				throw new FormatErrorException(errorMsg);
			}
		}
		return;
	}

	private static String getCurrentNode(String fieldName) {
		if (!fieldName.contains(".")) {
			return "";
		}
		return fieldName.substring(0, fieldName.lastIndexOf("."));
	}

	@SuppressWarnings({ "serial" })
	private static class CheckEntity extends BaseModel {

		public CheckEntity() {
		}

		private ParamCheck paramCheck;

		private FieldEntity field;

		public ParamCheck getParamCheck() {
			return paramCheck;
		}

		public void setParamCheck(ParamCheck paramCheck) {
			this.paramCheck = paramCheck;
		}

		public FieldEntity getField() {
			return field;
		}

		public void setField(FieldEntity field) {
			this.field = field;
		}
	}

	public static void main(String[] args) throws IOException {
	}
}
