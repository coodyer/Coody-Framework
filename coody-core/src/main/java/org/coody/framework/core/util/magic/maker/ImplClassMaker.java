package org.coody.framework.core.util.magic.maker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.abnormal.PrintException;
import org.coody.framework.core.util.magic.classloader.ClassLoaderContainer;
import org.coody.framework.core.util.magic.exceptation.NoMethodException;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ImplClassMaker {

	@SuppressWarnings("serial")
	private static final Map<Class<?>, String> RETURN_TYPE_MAPPING = new HashMap<Class<?>, String>() {
		{
			put(Integer.class, "I");
			put(Float.class, "F");
			put(Long.class, "L");
			put(Character.class, "C");
			put(Double.class, "D");
			put(Byte.class, "B");
			put(Short.class, "S");
			put(Boolean.class, "Z");
			put(void.class, "V");
		}
	};

	private static String[] formatClazzs(Class<?>... clazzs) {
		if (CommonUtil.isNullOrEmpty(clazzs)) {
			return null;
		}
		List<String> classNames = new ArrayList<String>(clazzs.length);
		for (Class<?> clazz : clazzs) {
			String className = clazz.getName().replace('.', '/');
			classNames.add(className);
		}
		return classNames.toArray(new String[] {});
	}

	private static int getOpcodes() {
		try {
			String javaVersion = System.getProperty("java.version");
			String javaPrefix = javaVersion.substring(0, 4);
			switch (javaPrefix) {
			case "1.8.":
				return Opcodes.V1_8;
			case "1.7.":
				return Opcodes.V1_7;
			case "1.6.":
				return Opcodes.V1_6;
			default:
				return Opcodes.V1_8;
			}
		} catch (Exception e) {
			PrintException.printException(e);
		}
		return Opcodes.V1_8;
	}

	static ClassWriter createClassWriter(Class<?> implClazz, String className) {
		className = className.replace('.', '/');
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		// 根据运行环境选择JDK版本并生成类
		String[] interfaces = formatClazzs(implClazz);
		cw.visit(getOpcodes(), Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", interfaces);
		// 生成切面所拦截的注解
		String rccName = "L" + implClazz.getName().replace(".", "/") + ";";
		AnnotationVisitor aVisitor = cw.visitAnnotation(rccName, true);
		aVisitor.visitEnd();
		MethodVisitor constructor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		// 这里请看截图
		constructor.visitVarInsn(Opcodes.ALOAD, 0);
		// 执行父类的init初始化
		constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// 从当前方法返回void
		constructor.visitInsn(Opcodes.RETURN);
		constructor.visitMaxs(1, 1);
		constructor.visitEnd();
		return cw;
	}

	static ClassWriter createMethod(Class<?> implClazz, ClassWriter cw, String methodName, Class<?>[] paramtypes,
			Class<?> returnType, Class<?>[] exceptionClazzs, Annotation[] annotations) {
		String[] exceptions = formatClazzs(exceptionClazzs);
		// 解析出参类型
		String returnDesc = RETURN_TYPE_MAPPING.get(returnType);
		if (returnDesc == null) {
			returnDesc = "L" + returnType.getName().replace('.', '/') + ";";
		}
		// 解析入参类型
		String[] inputParaClassNames = formatClazzs(paramtypes);
		String inputParaContext = "";
		if (!CommonUtil.isNullOrEmpty(inputParaClassNames)) {
			StringBuilder sbContext = new StringBuilder();
			for (String paraClassName : inputParaClassNames) {
				sbContext.append("L");
				sbContext.append(paraClassName);
				sbContext.append(";");
			}
			inputParaContext = sbContext.toString();
		}
		MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, methodName, "(" + inputParaContext + ")" + returnDesc,
				null, exceptions);
		// 生成切面所拦截的注解
		String rccName = "L" + implClazz.getName().replace(".", "/") + ";";
		AnnotationVisitor av = mv.visitAnnotation(rccName, true);
		av.visitEnd();
		// copy抽象类方法持有注解
		if (!CommonUtil.isNullOrEmpty(annotations)) {
			try {
				for (Annotation annotation : annotations) {
					String annotationName = "L" + annotation.annotationType().getName().replace('.', '/') + ";";
					AnnotationVisitor avTemp = mv.visitAnnotation(annotationName, true);
					Map<String, Object> annotationData = PropertUtil.getAnnotationValueMap(annotation);
					if (!CommonUtil.isNullOrEmpty(annotationData)) {
						for (String key : annotationData.keySet()) {
							avTemp.visit(key, annotationData.get(key));
						}
					}
					avTemp.visitEnd();
				}
			} catch (Exception e) {
				throw new CoodyException("注解解析失败", e);
			}
		}
		if (returnType == void.class) {
			// 无参
			mv.visitInsn(Opcodes.RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
			return cw;
		}
		// 带参返回null
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitInsn(Opcodes.ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		return cw;
	}

	/**
	 * 添加一个 public 的类成员
	 * 
	 * @param fieldName 类成员名
	 * @param fieldDesc 类成员类型描述
	 */
	public static void createField(ClassWriter classWriter, String fieldName, Class<?> fieldType) {
		String type = RETURN_TYPE_MAPPING.get(fieldType);
		if (CommonUtil.isNullOrEmpty(type)) {
			type = "L" + fieldType.getName().replace('.', '/') + ";";
		}
		FieldVisitor fv = classWriter.visitField(Opcodes.ACC_PRIVATE, fieldName, type, null, null);
		fv.visitEnd();
	}

	/**
	 * 为接口创建实现类
	 * 
	 * @param clazz
	 * @throws Exception
	 */
	public static Class<?> createInterfaceImpl(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		if (CommonUtil.isNullOrEmpty(methods)) {
			throw new NoMethodException(clazz);
		}
		// 创建实现类
		String className = clazz.getName() + "Impl";
		ClassWriter classWriter = createClassWriter(clazz, className);

		for (Method method : methods) { // 创建实现方法 classWriter =
			createMethod(clazz, classWriter, method.getName(), method.getParameterTypes(), method.getReturnType(),
					method.getExceptionTypes(), method.getAnnotations());
		}
		return ClassLoaderContainer.simpleClassLoader.defineClassForName(className, classWriter.toByteArray());
	}
}
