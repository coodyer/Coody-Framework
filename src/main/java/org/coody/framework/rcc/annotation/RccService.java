package org.coody.framework.rcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coody.framework.core.annotation.InitBean;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) 
@InitBean
public @interface RccService {

	String value() default "";
	
	boolean annoExtends() default true;
}
