package org.coody.framework.core.proxy.constant;

import org.objectweb.asm.Opcodes;

public class ProxyConstant {

	public static final int ASM_VERSION = Opcodes.ASM7;
	
	public static final int ASM_JDK_VERSION = Opcodes.V1_8;
	// 动态生成代理类的前缀
	public static final String PROXY_CLASSNAME_SUFFIX = "$Coody";
	//目标对象
	public static final String PROXY_TARGET_OBJECT= "$Coody";
	// 字段名
	public static final String FIELD_INVOCATIONHANDLER = "invocationHandler";
	// 方法名
	public static final String METHOD_SETTER = "setInvocationHandler_Coody";

	public static final String METHOD_INVOKE = "invokeInvocationHandler_Coody";

	public static final String METHOD_INVOKE_DESC = "(Lorg/coody/framework/core/proxy/iface/Proxy;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";

	public static final String METHOD_FIELD_PREFIX = "method";
}
