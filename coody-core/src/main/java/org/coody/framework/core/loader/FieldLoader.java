package org.coody.framework.core.loader;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;

/**
 * 字段加载器
 * 
 * @author Coody
 *
 */
public class FieldLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			BeanAssember.initField(bean);
		}
	}

}
