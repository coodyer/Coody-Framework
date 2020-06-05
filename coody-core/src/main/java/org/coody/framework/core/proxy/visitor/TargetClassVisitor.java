package org.coody.framework.core.proxy.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.coody.framework.core.proxy.constant.ProxyConstant;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TargetClassVisitor extends ClassVisitor {

	private boolean isFinal;
	private List<MethodBean> methods = new ArrayList<>();
	private List<MethodBean> declaredMethods = new ArrayList<>();
	private List<MethodBean> constructors = new ArrayList<>();

	public TargetClassVisitor() {
		super(ProxyConstant.ASM_VERSION);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL) {
			isFinal = true;
		}
		if (superName != null) {
			List<MethodBean> beans = initMethodBeanByParent(superName);
			if (beans != null && !beans.isEmpty()) {
				for (MethodBean bean : beans) {
					if (!methods.contains(bean)) {
						methods.add(bean);
					}
				}
			}
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
			String[] exceptions) {
		if ("<init>".equals(name)) {
			MethodBean constructor = new MethodBean(access, name, descriptor);
			constructors.add(constructor);
			return super.visitMethod(access, name, descriptor, signature, exceptions);
		}
		if (!"<clinit>".equals(name)) {
			if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
					|| (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}
			MethodBean methodBean = new MethodBean(access, name, descriptor);
			declaredMethods.add(methodBean);
			if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
				methods.add(methodBean);
			}
			return super.visitMethod(access, name, descriptor, signature, exceptions);
		}
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}

	public boolean isFinal() {
		return isFinal;
	}

	public List<MethodBean> getTotalMethods() {
		Map<String, MethodBean> methodMap = new TreeMap<String, TargetClassVisitor.MethodBean>();
		for (MethodBean method : methods) {
			methodMap.put(method.methodDesc, method);
		}
		for (MethodBean method : declaredMethods) {
			methodMap.put(method.methodDesc, method);
		}
		return new ArrayList<TargetClassVisitor.MethodBean>(methodMap.values());
	}

	public List<MethodBean> getMethods() {
		return methods;
	}

	public List<MethodBean> getConstructors() {
		return constructors;
	}

	private List<MethodBean> initMethodBeanByParent(String superName) {
		try {
			if (superName != null && !superName.isEmpty()) {
				if (superName.equals("java/lang/Object")) {
					return null;
				}
				ClassReader reader = new ClassReader(
						Thread.currentThread().getContextClassLoader().getResourceAsStream(superName + ".class"));
				TargetClassVisitor visitor = new TargetClassVisitor();
				reader.accept(visitor, ClassReader.SKIP_DEBUG);
				List<MethodBean> beans = new ArrayList<>();
				for (MethodBean methodBean : visitor.methods) {
					// 跳过 final 和 static
					if ((methodBean.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL
							|| (methodBean.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
						continue;
					}
					// 只要 public
					if ((methodBean.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
						beans.add(methodBean);
					}
				}
				return beans;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class MethodBean {

		public int access;
		public String methodName;
		public String methodDesc;

		public MethodBean() {
		}

		public MethodBean(int access, String methodName, String methodDesc) {
			this.access = access;
			this.methodName = methodName;
			this.methodDesc = methodDesc;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof MethodBean)) {
				return false;
			}
			MethodBean bean = (MethodBean) obj;
			if (access == bean.access && methodName != null && bean.methodName != null
					&& methodName.equals(bean.methodName) && methodDesc != null && bean.methodDesc != null
					&& methodDesc.equals(bean.methodDesc)) {
				return true;
			}
			return false;
		}
	}
}