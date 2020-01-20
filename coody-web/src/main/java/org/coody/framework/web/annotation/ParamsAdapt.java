package org.coody.framework.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coody.framework.core.annotation.MethodDeliver;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME) 
@MethodDeliver
public @interface ParamsAdapt {
	
	Class<?> value() ;
}
