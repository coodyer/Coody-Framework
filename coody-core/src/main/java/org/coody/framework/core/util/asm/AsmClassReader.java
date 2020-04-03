package org.coody.framework.core.util.asm;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;

public class AsmClassReader {

	public final static Map<String, byte[]> clazzByteMap = new ConcurrentHashMap<String, byte[]>();

	public static ClassReader getAsmClassReader(String name) throws IOException {
		if (clazzByteMap.containsKey(name)) {
			return new ClassReader(clazzByteMap.get(name));
		}
		return new ClassReader(name);
	}

}
