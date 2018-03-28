package org.coody.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时任务注解
 * @author admin
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
public @interface CronTask {

	String value() ;
}
