package org.coody.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单个Around注解下连接符为"且" 多个Around注解下连接符为"或"
 * 
 * @author Coody
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Arounds.class)
public @interface Around {

	/**
	 * 注解
	 * 
	 * @return
	 */
	Class<?>[] annotationClass() default {};

	/**
	 * 方法匹配(ant表达式)，可用MethodSignUtil.getMethodUnionKey(method) 获取方法标识对比
	 * 
	 * @return
	 */
	String methodMappath() default "";

	/**
	 * 类匹配(ant表达式)
	 * 
	 * @return
	 */
	String classMappath() default "";

}
