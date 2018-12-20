package org.coody.framework.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

/**
 * 参数名操作工具
 * 
 * @author Coody
 *
 *         2018年12月20日
 * 
 * @blog 54sb.org
 */
public class ParameterNameUtil {

	private static final Map<Class<?>, Map<Executable, List<String>>> classExecutableCache = new WeakHashMap<Class<?>, Map<Executable, List<String>>>();

	/**
	 * 加载指定方法的参数名
	 * 
	 * @param executable
	 * @return
	 */
	public static List<String> getParameters(Executable executable) {
		Class<?> clazz = PropertUtil.getClass(executable);
		Map<Executable, List<String>> executableParameters = getExecutableParameters(clazz);
		return executableParameters.get(executable);
	}

	/**
	 * 加载所有方法和参数
	 * 
	 * @param clazz
	 * @return
	 */
	public static Map<Executable, List<String>> getExecutableParameters(Class<?> clazz) {
		Map<Executable, List<String>> executableParameters = classExecutableCache.get(clazz);
		if (executableParameters != null) {
			return executableParameters;
		}
		executableParameters = new HashMap<Executable, List<String>>();
		try {
			ClassReader reader = ParameterNameUtil.createClassReader(clazz);

			ParameterNameUtil.ExecutableParameterNameVisitor visitor = new ParameterNameUtil.ExecutableParameterNameVisitor(
					clazz);
			reader.accept(visitor, 0);

			Map<String, Exception> exceptions = visitor.getExceptions();
			if (exceptions.size() == 1) {
				throw new RuntimeException((Exception) exceptions.values().iterator().next());
			}
			if (!exceptions.isEmpty()) {
				throw new RuntimeException(exceptions.toString());
			}

			executableParameters = visitor.getExecutableParameters();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		classExecutableCache.put(clazz, executableParameters);
		return executableParameters;
	}

	private static ClassReader createClassReader(Class<?> declaringClass) throws IOException {
		InputStream in = null;
		try {
			ClassLoader classLoader = declaringClass.getClassLoader();
			in = classLoader.getResourceAsStream(declaringClass.getName().replace('.', '/') + ".class");
			ClassReader reader = new ClassReader(in);
			return reader;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private static class ExecutableParameterNameVisitor extends CoodyVisitor {

		private final Map<Executable, List<String>> executableParameters = new HashMap<Executable, List<String>>();
		private final Map<String, Exception> exceptions = new HashMap<String, Exception>();
		private final Map<String, Executable> executableMap = new HashMap<String, Executable>();

		public ExecutableParameterNameVisitor(Class<?> clazz) {
			List<Constructor<?>> constructors = new ArrayList<Constructor<?>>(
					Arrays.<Constructor<?>>asList(clazz.getConstructors()));
			constructors.addAll(Arrays.asList(clazz.getDeclaredConstructors()));
			for (Constructor<?> constructor : constructors) {
				Type[] types = new Type[constructor.getParameterTypes().length];
				for (int j = 0; j < types.length; j++) {
					types[j] = Type.getType(constructor.getParameterTypes()[j]);
				}
				executableMap.put(constructor.getName()+Type.getMethodDescriptor(Type.VOID_TYPE, types), constructor);
			}
			List<Method> methods = new ArrayList<Method>(Arrays.asList(clazz.getMethods()));
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			for (Method method : methods) {
				executableMap.put(method.getName()+Type.getMethodDescriptor(method), method);
			}
		}

		public Map<Executable, List<String>> getExecutableParameters() {
			return executableParameters;
		}

		public Map<String, Exception> getExceptions() {
			return exceptions;
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			try {
				final List<String> parameterNames;
				Executable executable = executableMap.get(name+desc);
				if (executable == null) {
					return null;
				}
				parameterNames = new ArrayList<String>(executable.getParameterTypes().length);
				parameterNames.addAll(Collections.<String>nCopies(executable.getParameterTypes().length, null));
				executableParameters.put(executable, parameterNames);
				return new MethodVisitor(Opcodes.ASM7) {
					public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
							int index) {
						if (Modifier.isStatic(executable.getModifiers())) {
							if (index > parameterNames.size() - 1) {
								return;
							}
							parameterNames.set(index, name);
							return;
						}
						if (index > 0) {
							if(index>parameterNames.size()){
								return;
							}
							parameterNames.set(index - 1, name);
							return;
						}
					}
				};
			} catch (Exception e) {
				this.exceptions.put(signature, e);
			}
			return null;
		}
	}

	/**
	 * 
	 * @author Coody
	 *
	 *         2018年12月20日
	 * 
	 * @blog 54sb.org
	 */
	public static class CoodyVisitor extends ClassVisitor {

		protected AnnotationVisitor annotationVisitor = new AnnotationVisitor(Opcodes.ASM7) {
			@Override
			public void visit(String name, Object value) {
				CoodyVisitor.this.visit(name, value);
			}

			@Override
			public void visitEnum(String name, String describe, String value) {
				CoodyVisitor.this.visitEnum(name, describe, value);
			}

			@Override
			public AnnotationVisitor visitAnnotation(String name, String describe) {
				return CoodyVisitor.this.visitAnnotation(name, describe);
			}

			@Override
			public AnnotationVisitor visitArray(String name) {
				return CoodyVisitor.this.visitArray(name);
			}

			@Override
			public void visitEnd() {
				CoodyVisitor.this.visitEnd();
			}
		};

		protected final FieldVisitor fieldVisitor = new FieldVisitor(Opcodes.ASM7) {
			@Override
			public AnnotationVisitor visitAnnotation(String describe, boolean visible) {
				return CoodyVisitor.this.visitAnnotation(describe, visible);
			}

			@Override
			public void visitAttribute(Attribute attribute) {
				CoodyVisitor.this.visitAttribute(attribute);
			}

			@Override
			public void visitEnd() {
				CoodyVisitor.this.visitEnd();
			}
		};
		protected final MethodVisitor methodVisitor = new MethodVisitor(Opcodes.ASM7) {
			@Override
			public AnnotationVisitor visitAnnotationDefault() {
				return CoodyVisitor.this.visitAnnotationDefault();
			}

			@Override
			public AnnotationVisitor visitAnnotation(String describe, boolean visible) {
				return CoodyVisitor.this.visitAnnotation(describe, visible);
			}

			@Override
			public AnnotationVisitor visitParameterAnnotation(int parameter, String describe, boolean visible) {
				return CoodyVisitor.this.visitMethodParameterAnnotation(parameter, describe, visible);
			}

			@Override
			public void visitAttribute(Attribute attribute) {
				CoodyVisitor.this.visitAttribute(attribute);
			}

			@Override
			public void visitCode() {
				CoodyVisitor.this.visitCode();
			}

			@Override
			public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
				CoodyVisitor.this.visitFrame(type, nLocal, local, nStack, stack);
			}

			@Override
			public void visitInsn(int opcode) {
				CoodyVisitor.this.visitInsn(opcode);
			}

			@Override
			public void visitJumpInsn(int i, Label label) {
				CoodyVisitor.this.visitJumpInsn(i, label);
			}

			@Override
			public void visitLabel(Label label) {
				CoodyVisitor.this.visitLabel(label);
			}

			@Override
			public void visitLdcInsn(Object cst) {
				CoodyVisitor.this.visitLdcInsn(cst);
			}

			@Override
			public void visitIincInsn(int var, int increment) {
				CoodyVisitor.this.visitIincInsn(var, increment);
			}

			@Override
			public void visitTableSwitchInsn(int i, int i2, Label label, Label... labels) {
				CoodyVisitor.this.visitTableSwitchInsn(i, i2, label, labels);
			}

			@Override
			public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
				CoodyVisitor.this.visitLookupSwitchInsn(label, ints, labels);
			}

			@Override
			public void visitMultiANewArrayInsn(String describe, int dims) {
				CoodyVisitor.this.visitMultiANewArrayInsn(describe, dims);
			}

			@Override
			public void visitTryCatchBlock(Label label, Label label2, Label label3, String s) {
				CoodyVisitor.this.visitTryCatchBlock(label, label2, label3, s);
			}

			@Override
			public void visitLocalVariable(String s, String s2, String s3, Label label, Label label2, int i) {
				CoodyVisitor.this.visitLocalVariable(s, s2, s3, label, label2, i);
			}

			@Override
			public void visitLineNumber(int i, Label label) {
				CoodyVisitor.this.visitLineNumber(i, label);
			}

			@Override
			public void visitMaxs(int maxStack, int maxLocals) {
				CoodyVisitor.this.visitMaxs(maxStack, maxLocals);
			}

			@Override
			public void visitEnd() {
				CoodyVisitor.this.visitEnd();
			}

			@Override
			public void visitIntInsn(int opcode, int operand) {
				CoodyVisitor.this.visitIntInsn(opcode, operand);
			}

			@Override
			public void visitVarInsn(int opcode, int var) {
				CoodyVisitor.this.visitVarInsn(opcode, var);
			}

			@Override
			public void visitTypeInsn(int opcode, String type) {
				CoodyVisitor.this.visitTypeInsn(opcode, type);
			}

			@Override
			public void visitFieldInsn(int opcode, String owner, String name, String describe) {
				CoodyVisitor.this.visitFieldInsn(opcode, owner, name, describe);
			}

			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String describe) {
				CoodyVisitor.this.visitMethodInsn(opcode, owner, name, describe);
			}

			@Override
			public void visitMethodInsn(int opcode, String owner, String name, String describe, boolean itf) {
				CoodyVisitor.this.visitMethodInsn(opcode, owner, name, describe);
			}

			@Override
			public void visitInvokeDynamicInsn(String s, String s2, Handle handle, Object... objects) {
				CoodyVisitor.this.visitInvokeDynamicInsn(s, s2, handle, objects);
			}
		};

		public CoodyVisitor() {
			super(Opcodes.ASM7);
		}

		protected AnnotationVisitor visitAnnotationDefault() {
			return annotationVisitor;
		}

		protected AnnotationVisitor visitArray(String name) {
			return annotationVisitor;
		}

		protected AnnotationVisitor visitAnnotation(String name, String describe) {
			return annotationVisitor;
		}

		protected void visitEnum(String name, String describe, String value) {
		}

		protected void visit(String name, Object value) {
			// no-op
		}

		protected void visitVarInsn(int opcode, int var) {
			// no-op
		}

		protected void visitTypeInsn(int opcode, String type) {
			// no-op
		}

		protected void visitFieldInsn(int opcode, String owner, String name, String describe) {
			// no-op
		}

		protected void visitMethodInsn(int opcode, String owner, String name, String describe) {
			// no-op
		}

		protected void visitInvokeDynamicInsn(String s, String s2, Handle handle, Object[] objects) {
			// no-op
		}

		protected void visitIntInsn(int opcode, int operand) {
			// no-op
		}

		protected void visitJumpInsn(int i, Label label) {
			// no-op
		}

		protected void visitLabel(Label label) {
			// no-op
		}

		protected void visitLdcInsn(Object cst) {
			// no-op
		}

		protected void visitIincInsn(int var, int increment) {
			// no-op
		}

		protected void visitTableSwitchInsn(int i, int i2, Label label, Label[] labels) {
			// no-op
		}

		protected void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
			// no-op
		}

		protected void visitMultiANewArrayInsn(String describe, int dims) {
			// no-op
		}

		protected void visitTryCatchBlock(Label label, Label label2, Label label3, String s) {
			// no-op
		}

		protected void visitLocalVariable(String s, String s2, String s3, Label label, Label label2, int i) {
			// no-op
		}

		protected void visitLineNumber(int i, Label label) {
			// no-op
		}

		protected void visitMaxs(int maxStack, int maxLocals) {
			// no-op
		}

		protected void visitInsn(int opcode) {
			// no-op
		}

		protected void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
			// no-op
		}

		protected void visitCode() {
			// no-op
		}

		protected AnnotationVisitor visitMethodParameterAnnotation(int parameter, String describe, boolean visible) {
			return annotationVisitor;
		}

		protected AnnotationVisitor visitParameterAnnotation(int parameter, String describe, boolean visible) {
			return annotationVisitor;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			if (cv != null) {
				cv.visit(version, access, name, signature, superName, interfaces);
			}
		}

		@Override
		public void visitSource(String source, String debug) {
			if (cv != null) {
				cv.visitSource(source, debug);
			}
		}

		@Override
		public void visitOuterClass(String owner, String name, String describe) {
			if (cv != null) {
				cv.visitOuterClass(owner, name, describe);
			}
		}

		@Override
		public AnnotationVisitor visitAnnotation(String describe, boolean visible) {
			return annotationVisitor;
		}

		@Override
		public void visitAttribute(Attribute attr) {
			if (cv != null) {
				cv.visitAttribute(attr);
			}
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
			if (cv != null) {
				cv.visitInnerClass(name, outerName, innerName, access);
			}
		}

		@Override
		public FieldVisitor visitField(int access, String name, String describe, String signature, Object value) {
			return fieldVisitor;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String describe, String signature,
				String[] exceptions) {
			return methodVisitor;
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String describe, boolean visible) {
			return annotationVisitor;
		}

		@Override
		public void visitEnd() {
			if (cv != null) {
				cv.visitEnd();
			}
		}

		public AnnotationVisitor annotationVisitor() {
			return annotationVisitor;
		}

		public FieldVisitor fieldVisitor() {
			return fieldVisitor;
		}

		public MethodVisitor methodVisitor() {
			return methodVisitor;
		}
	}
}