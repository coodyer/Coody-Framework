package org.coody.framework.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coody.framework.cache.instance.LocalCache;
import org.coody.framework.cache.instance.iface.CoodyCacheFace;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Repeatable(CacheWipes.class)
public @interface CacheWipe {

	String key();

	String[] fields() default "";

	Class<? extends CoodyCacheFace> engine() default LocalCache.class;

}
