package org.coody.framework.core.util.magic.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class SourceCodeCompiler {

	
	private static final String FORMAT="public\\s+class\\s+(\\w+)";

	public static byte[] compile(String sourceCode) {
		Pattern pattern = Pattern.compile(FORMAT);
		Matcher matcher = pattern.matcher(sourceCode);
		if (!matcher.find()) {
			throw new RuntimeException("编译出错，未找到类名");
		}
		String clazz = matcher.group(1) + ".java";
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			JavaFileObject javaFileObject = MemoryJavaFileManager.makeStringSource(clazz, sourceCode);
			JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null,
					Arrays.asList(javaFileObject));
			if (!task.call()) {
				throw new RuntimeException("编译出错");
			}
			Map<String, byte[]> map = manager.getClassBytes();
			List<byte[]> result = new ArrayList<byte[]>(map.values());
			return result.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}	
}
