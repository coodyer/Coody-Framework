package org.coody.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单个Around注解下连接符为"且"
 * 多个Around注解下连接符为"或"
 * @author Coody
 * @date 2018年3月21日
 * @blog http://54sb.org
 * @email 644556636@qq.com
 */
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME) 
public @interface Arounds {
	Around[] value() default {};
}
