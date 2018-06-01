package org.coody.framework.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coody.framework.core.container.BuiltContainer;
import org.coody.framework.core.loader.AspectLoader;
import org.coody.framework.core.loader.BeanLoader;
import org.coody.framework.core.loader.FieldLoader;
import org.coody.framework.core.loader.InitRunLoader;
import org.coody.framework.core.loader.MvcLoader;
import org.coody.framework.core.loader.TaskLoader;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.StringUtil;

public class FrameworkRute {

	public static void init(String... packets) throws Exception {
		List<String> packetArgs=new ArrayList<String>(Arrays.asList(packets));
		packetArgs.add("org.coody.framework");
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
		for (String packet : packetArgs) {
			Set<Class<?>> clazzsTemp = ClassUtil.getClasses(packet);
			clazzs.addAll(clazzsTemp);
		}
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> clazz : BuiltContainer.INIT_BEAN) {
			clazzs.add(clazz);
		}
		new AspectLoader().doLoader(clazzs);
		new TaskLoader().doLoader(clazzs);
		new BeanLoader().doLoader(clazzs);
		new FieldLoader().doLoader(clazzs);
		new MvcLoader().doLoader(clazzs);
		new InitRunLoader().doLoader(clazzs);

	}

}
