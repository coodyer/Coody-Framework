package org.coody.framework.core.config;

import java.util.ArrayList;
import java.util.List;

import org.coody.framework.mvc.adapt.FormNomalAdapt;

public class IcopConfig {

	/**
	 * 要扫描的包
	 */
	public static final List<String> SCANNER_PACKET=new ArrayList<String>();
	
	/**
	 * 要附加的类
	 */
	public static final List<Class<?>> INIT_CLAZZS=new ArrayList<Class<?>>();
	
	/**
	 * MVC默认适配器
	 */
	public static Class<?> DEFAULT_PARAM_ADAPT=FormNomalAdapt.class;
	
	
}
