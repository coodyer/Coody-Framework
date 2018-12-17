package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * Bean加载器
 * 
 * @author Coody
 *
 */
public class BeanLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (StringUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (clazz.isInterface()) {
				continue;
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}
			if (clazz.isEnum()) {
				continue;
			}
			if (StringUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			Annotation autoBuild = PropertUtil.getAnnotation(clazz, AutoBuild.class);
			if (StringUtil.isNullOrEmpty(autoBuild)) {
				continue;
			}
			BeanAssember.initBean(clazz);
		}
	}

}
