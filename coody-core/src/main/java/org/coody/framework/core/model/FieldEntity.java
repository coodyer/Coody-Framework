package org.coody.framework.core.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.coody.framework.core.util.StringUtil;

@SuppressWarnings("serial")
public class FieldEntity extends BaseModel {

	private String fieldName;
	private Object fieldValue;
	/**
	 * 字段内存地址
	 */
	private Long fieldOffset;
	private Class<?> fieldType;
	private Annotation[] fieldAnnotations;
	private Field sourceField;

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<?> clazz) {
		if (StringUtil.isNullOrEmpty(fieldAnnotations)) {
			return null;
		}
		for (Annotation annotation : fieldAnnotations) {
			if (clazz.isAssignableFrom(annotation.annotationType())) {
				return (T) annotation;
			}
		}
		return null;
	}

	public Long getFieldOffset() {
		return fieldOffset;
	}

	public void setFieldOffset(Long fieldOffset) {
		this.fieldOffset = fieldOffset;
	}

	public Field getSourceField() {
		return sourceField;
	}

	public void setSourceField(Field sourceField) {
		this.sourceField = sourceField;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue() {
		return (T) fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public Annotation[] getFieldAnnotations() {
		return fieldAnnotations;
	}

	public void setFieldAnnotations(Annotation[] fieldAnnotations) {
		this.fieldAnnotations = fieldAnnotations;
	}

}
