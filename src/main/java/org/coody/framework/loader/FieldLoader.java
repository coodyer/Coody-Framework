package org.coody.framework.loader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.coody.framework.annotation.OutBean;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.exception.BeanNotFoundException;
import org.coody.framework.loader.base.IcopLoader;
import org.coody.framework.util.StringUtil;

/**
 * 字段加载器
 * @author Administrator
 *
 */
public class FieldLoader implements IcopLoader{

	@Override
	public void doLoader(Set<Class<?>> clazzs) throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			List<Field> fields = loadFields(bean.getClass());
			if (StringUtil.isNullOrEmpty(fields)) {
				continue;
			}
			for (Field field : fields) {
				OutBean writeBean = field.getAnnotation(OutBean.class);
				if (StringUtil.isNullOrEmpty(writeBean)) {
					continue;
				}
				String beanName = writeBean.beanName();
				if (StringUtil.isNullOrEmpty(beanName)) {
					beanName = field.getType().getName();
				}
				if (!BeanContainer.containsBean(beanName)) {
					throw new BeanNotFoundException(beanName, bean.getClass());
				}
				Object writeValue = null;
				field.setAccessible(true);
				writeValue = BeanContainer.getBean(beanName);
				field.set(bean, writeValue);
			}
		}
	}
	
	private static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return fields;
		}
		List<Field> childFields = loadFields(superClass);
		if (StringUtil.isNullOrEmpty(childFields)) {
			return fields;
		}
		fields.addAll(childFields);
		return fields;
	}

}
