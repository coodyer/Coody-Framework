package org.coody.framework.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.coody.framework.core.annotation.Order;
import org.coody.framework.core.exception.base.IcopException;
import org.coody.framework.core.loader.AspectLoader;
import org.coody.framework.core.loader.BeanLoader;
import org.coody.framework.core.loader.FieldLoader;
import org.coody.framework.core.loader.InitRunLoader;
import org.coody.framework.core.loader.iface.IcopLoader;
import org.coody.framework.core.logger.BaseLogger;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

public class CoreApp {
	
	static BaseLogger logger=BaseLogger.getLogger(CoreApp.class);
	
	@SuppressWarnings("serial")
	static Map<Integer,List<Class<?>>> loadersMap=new TreeMap<Integer, List<Class<?>>>(){{
		put(1, Arrays.asList(new Class<?>[]{AspectLoader.class}));
		put(2, Arrays.asList(new Class<?>[]{BeanLoader.class}));
		put(3, Arrays.asList(new Class<?>[]{FieldLoader.class}));
		put(Integer.MAX_VALUE, Arrays.asList(new Class<?>[]{InitRunLoader.class}));
	}};
	
	public static void pushLoader(Class<?> loader){
		if(!IcopLoader.class.isAssignableFrom(loader)){
			throw new IcopException(loader.getName()+"不是加载器");
		}
		Integer seq=Integer.MAX_VALUE-1;
		Order order=loader.getClass().getAnnotation(Order.class);
		if(order!=null){
			seq=order.value();
		}
		if(loadersMap.containsKey(seq)){
			loadersMap.get(seq).add(loader);
			return;
		}
		List<Class<?>> loaderList=new ArrayList<Class<?>>();
		loaderList.add(loader);
		loadersMap.put(seq, loaderList);
	}

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
		List<Class<?>> currentLoaders=new ArrayList<Class<?>>();
		for(Integer key:loadersMap.keySet()){
			for(Class<?> clazz:loadersMap.get(key)){
				if(currentLoaders.contains(clazz)){
					continue;
				}
				currentLoaders.add(clazz);
			}
		}
		for(Class<?> loader:currentLoaders){
			logger.info(loader.getName()+":开始加载");
			long t0=System.currentTimeMillis();
			IcopLoader icopLoader=(IcopLoader) loader.newInstance();
			icopLoader.doLoader(clazzs);
			long t1=System.currentTimeMillis();
			logger.info(loader.getName()+":加载耗时>>"+(t1-t0));
		}
		
		/*long t1=System.currentTimeMillis();
		new AspectLoader().doLoader(clazzs);
		long t2=System.currentTimeMillis();
		//new TaskLoader().doLoader(clazzs);
		long t3=System.currentTimeMillis();
		new BeanLoader().doLoader(clazzs);
		long t4=System.currentTimeMillis();
		new FieldLoader().doLoader(clazzs);
		long t5=System.currentTimeMillis();
		//new MvcLoader().doLoader(clazzs);
		long t6=System.currentTimeMillis();
		new InitRunLoader().doLoader(clazzs);
		long t7=System.currentTimeMillis();

		logger.info("包扫描:"+(t1-t0));
		logger.info("切面加载:"+(t2-t1));
		logger.info("定时任务加载:"+(t3-t2));
		logger.info("bean加载:"+(t4-t3));
		logger.info("对象注入:"+(t5-t4));
		logger.info("mvc加载:"+(t6-t5));
		logger.info("InitBean加载:"+(t7-t6));
		logger.info("总耗时:"+(t7-t0));*/
	}

}
