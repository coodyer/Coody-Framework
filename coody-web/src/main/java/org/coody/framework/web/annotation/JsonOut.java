package org.coody.framework.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coody.framework.core.annotation.MethodDeliver;

/**
 * JSON输出
 * @author admin
 *
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
@MethodDeliver
public @interface JsonOut {

}
