package org.coody.framework.elock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ELock {

	/**
	 * 锁基础名称
	 * @return
	 */
	String name() default "";
	/**
	 * 可选变量
	 * @return
	 */
	String[] fields() default {};
	/**
	 * 超时时间
	 * @return
	 */
	int waitTime() default 30;
	/**
	 * 屏蔽错误，为true则不抛出
	 * @return
	 */
	boolean igonreException() default false;
}
