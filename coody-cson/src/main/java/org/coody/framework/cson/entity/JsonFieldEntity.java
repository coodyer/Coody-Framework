package org.coody.framework.cson.entity;

import java.io.Serializable;
import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class JsonFieldEntity implements Serializable {

	private Field field;

	private Boolean isIgonre;

	private String format;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Boolean getIsIgonre() {
		return isIgonre;
	}

	public void setIsIgonre(Boolean isIgonre) {
		this.isIgonre = isIgonre;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
