package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
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
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (StringUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			pool.pushTask(new Runnable() {
				@Override
				public void run() {
					Annotation autoBuild = PropertUtil.getAnnotation(clazz, AutoBuild.class);
					if (StringUtil.isNullOrEmpty(autoBuild)) {
						return;
					}
					BeanAssember.initBean(clazz);
				}
			});
		}
		pool.execute();
	}

}
