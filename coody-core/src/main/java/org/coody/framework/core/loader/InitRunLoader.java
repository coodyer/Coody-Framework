package org.coody.framework.core.loader;

import java.util.ArrayList;
import java.util.List;

import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.abnormal.PrintException;

/**
 * 切面加载器
 * 
 * @author Coody
 *
 */
public class InitRunLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		List<Runnable> inits = new ArrayList<Runnable>();
		for (Object bean : BeanContainer.getBeans()) {
			if (bean instanceof InitBeanFace) {
				// 初始化运行
				try {
					LogUtil.log.debug("初始化执行 >>" + bean.getClass().getName());
					InitBeanFace face = (InitBeanFace) bean;
					inits.add(new Runnable() {
						@Override
						public void run() {
							try {
								face.init();
							} catch (Exception e) {
								PrintException.printException(e);
							}
						}
					});
				} catch (Exception e) {
					PrintException.printException(e);
				}
			}
		}
		if (!CommonUtil.isNullOrEmpty(inits)) {
			new ThreadBlockPool().execute(inits);
		}
	}

}
