package org.coody.framework.core.util.dynamic.classloader;

public class SimpleClassLoader extends ClassLoader {

	public Class<?> defineClassForName(String className, byte[] classData) throws ClassFormatError {
		return defineClass(className, classData, 0, classData.length);
	}
	
}
