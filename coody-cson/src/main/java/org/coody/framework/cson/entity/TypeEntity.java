package org.coody.framework.cson.entity;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.cson.exception.CsonException;

/**
 * 类型容器
 * 
 * @author Coody
 *
 */
@SuppressWarnings("serial")
public class TypeEntity implements Serializable {

	private Class<?> current;

	private List<TypeEntity> actuals;

	public TypeEntity() {
		super();
	}

	public TypeEntity(Class<?> current) {
		super();
		this.current = current;
	}

	public TypeEntity(Class<?> current, List<TypeEntity> actuals) {
		super();
		this.current = current;
		this.actuals = actuals;
	}

	public Class<?> getCurrent() {
		return current;
	}

	public void setCurrent(Class<?> current) {
		this.current = current;
	}

	public List<TypeEntity> getActuals() {
		return actuals;
	}

	public void setActuals(List<TypeEntity> actuals) {
		this.actuals = actuals;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T newInstance() {
		if (current == null) {
			return null;
		}
		try {
			if (current.isArray()) {
				
				return (T) new ArrayList();
			}
			if (current.isArray() || Collection.class.isAssignableFrom(current)) {
				if (Modifier.isAbstract(current.getModifiers())) {
					return (T) new ArrayList();
				}
				return (T) current.newInstance();
			}
			if (Map.class.isAssignableFrom(current)) {
				if (Modifier.isAbstract(current.getModifiers())) {
					return (T) new HashMap();
				}
				return (T) current.newInstance();
			}
			return (T) current.newInstance();
		} catch (Exception e) {
			throw new CsonException("对象创建失败>>" + current, e);
		}
	}

	@Override
	public String toString() {
		return "TypeEntity [current=" + current + ", actuals=" + actuals + "]";
	}

}
