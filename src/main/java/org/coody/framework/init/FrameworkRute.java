package org.coody.framework.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.coody.framework.container.BuiltContainer;
import org.coody.framework.loader.AspectLoader;
import org.coody.framework.loader.BeanLoader;
import org.coody.framework.loader.FieldLoader;
import org.coody.framework.loader.InitRunLoader;
import org.coody.framework.loader.MvcLoader;
import org.coody.framework.loader.TaskLoader;
import org.coody.framework.util.ClassUtil;
import org.coody.framework.util.StringUtil;

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
