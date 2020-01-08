package org.coody.framework.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否模糊查询
 * @author Coody
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBVague {
	
	/**
	 * 模糊表达式  默认为前后置模糊表达式
	 * @return
	 */
	String value() default "%#{0}%";
	
}
