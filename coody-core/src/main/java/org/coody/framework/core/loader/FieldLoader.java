package org.coody.framework.core.loader;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;

/**
 * 字段加载器
 * 
 * @author Coody
 *
 */
public class FieldLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Object bean : BeanContainer.getBeans()) {
			pool.pushTask(new Runnable() {
				@Override
				public void run() {
					try {
						BeanAssember.initField(bean);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.execute();
	}

}
