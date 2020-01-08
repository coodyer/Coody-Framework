package org.coody.framework.core.loader;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.reflex.PropertUtil;

/**
 * Bean加载器
 * 
 * @author Coody
 *
 */
public class BeanLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (CommonUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (CommonUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			pool.pushTask(new Runnable() {
				@Override
				public void run() {
					AutoBuild autoBuild = PropertUtil.getAnnotation(clazz, AutoBuild.class);
					if (CommonUtil.isNullOrEmpty(autoBuild)) {
						return;
					}
					BeanAssember.initBean(clazz);
				}
			});
		}
		pool.execute();
	}

}
