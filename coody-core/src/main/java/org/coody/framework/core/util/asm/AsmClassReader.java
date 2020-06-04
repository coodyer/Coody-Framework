package org.coody.framework.core.util.asm;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;

public class AsmClassReader {

	public final static Map<String, byte[]> clazzByteMap = new ConcurrentHashMap<String, byte[]>();

	public static ClassReader getAsmClassReader(Class<?> clazz) throws IOException {
		if (clazzByteMap.containsKey(clazz.getName())) {
			return new ClassReader(clazzByteMap.get(clazz.getName()));
		}
		return new ClassReader(
				clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class"));
	}

}
