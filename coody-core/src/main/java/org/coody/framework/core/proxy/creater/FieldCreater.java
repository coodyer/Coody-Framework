package org.coody.framework.core.proxy.creater;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class FieldCreater {

	public static void addFieldAndGetSetMethod(ClassWriter writer, String className, Class<?> fieldType,
			String fieldName) {
		FieldVisitor fieldVisitor = writer.visitField(Opcodes.ACC_PRIVATE, fieldName, Type.getDescriptor(fieldType),
				null, null);
		fieldVisitor.visitEnd();

		// 创建set方法
		String methodNameForSet = "set" + firstUpperCase(fieldName);
		String methodDesc = "(" + Type.getDescriptor(fieldType) + ")V";
		MethodVisitor setMethodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, methodNameForSet, methodDesc, null,
				null);
		setMethodVisitor.visitCode();
		setMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		setMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
		setMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, className, fieldName, Type.getDescriptor(fieldType));
		setMethodVisitor.visitInsn(Opcodes.RETURN);
		setMethodVisitor.visitMaxs(2, 2);
		setMethodVisitor.visitEnd();

		// 创建get方法
		String methodNameForGet = "get" + firstUpperCase(fieldName);
		MethodVisitor getMethodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, methodNameForGet,
				"()" + Type.getType(fieldType).getDescriptor(), null, null);
		getMethodVisitor.visitCode();
		getMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		getMethodVisitor.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, Type.getDescriptor(fieldType));
		getMethodVisitor.visitInsn(Opcodes.ARETURN);
		getMethodVisitor.visitMaxs(2, 2);
		getMethodVisitor.visitEnd();

	}

	private static String firstUpperCase(String string) {
		String first = string.substring(0, 1);
		String after = string.substring(1);
		first = first.toUpperCase();
		return first + after;
	}

}