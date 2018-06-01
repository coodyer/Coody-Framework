package icop.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.coody.framework.annotation.CacheWipe;
import org.coody.framework.annotation.CacheWipes;

public class Test {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method method=Test.class.getDeclaredMethod("rccTest", null);
		Annotation annotation=method.getAnnotation(CacheWipe.class);
		System.out.println(annotation.annotationType().isAnnotationPresent(CacheWipes.class));
	}
	@CacheWipe(key="")
	private void rccTest() {
		// TODO Auto-generated method stub

	}

}
