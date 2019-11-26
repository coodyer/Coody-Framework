package org.coody.framework.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.coody.framework.cache.instance.LocalCache;
import org.coody.framework.cache.instance.iface.CoodyCacheFace;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface CacheWrite {
	String key() default "";

	int time() default 10;

	String[] fields() default "";

	Class<? extends CoodyCacheFace> engine() default LocalCache.class;
}
