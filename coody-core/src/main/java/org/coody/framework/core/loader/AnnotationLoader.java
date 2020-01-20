package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.annotation.FieldDeliver;
import org.coody.framework.core.annotation.MethodDeliver;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;

/**
 * 切面加载器
 * 
 * @author Coody
 *
 */
public class AnnotationLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (CommonUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (CommonUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			pool.pushTask(new Runnable() {

				@Override
				public void run() {
					try {
						cloneClassAnnotation(clazz);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		pool.execute();
	}

	// 把父类方法注解，克隆到子类注解
	public void cloneSuperAnnotationToChilder(Class<?> clazz, Class<?> childClazz) {
		if (clazz == null || clazz == Object.class) {
			return;
		}
		if (!Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers())) {
			return;
		}
		Method[] clazzMethods = clazz.getDeclaredMethods();
		Method[] childClazzMethods = childClazz.getDeclaredMethods();
		if (CommonUtil.hasNullOrEmpty(clazzMethods, childClazzMethods)) {
			return;
		}
		for (Method method : clazzMethods) {
			if (!Modifier.isAbstract(method.getModifiers())) {
				continue;
			}
			String methodKey = MethodSignUtil.getGeneralKeyByMethod(method);
			for (Method childMethod : childClazzMethods) {
				String childMethodKey = MethodSignUtil.getGeneralKeyByMethod(childMethod);
				if (!childMethodKey.equals(methodKey)) {
					continue;
				}
				Annotation[] annotations = method.getAnnotations();
				if (CommonUtil.isNullOrEmpty(annotations)) {
					continue;
				}
				try {
					PropertUtil.addAnnotations(childMethod, annotations);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 把所有类注解，克隆到方法和字段注解上
	public void cloneClassAnnotation(Class<?> clazz) throws Exception {
		if (clazz == null || clazz == Object.class) {
			return;
		}
		if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
			cloneClassAnnotation(clazz.getSuperclass());
		}
		if (clazz.getInterfaces() != null) {
			for (Class<?> interfaceClazz : clazz.getInterfaces()) {
				cloneClassAnnotation(interfaceClazz);
			}
		}
		Annotation[] annotations = clazz.getAnnotations();
		boolean hasAutoBuild = false;
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == AutoBuild.class
					|| annotation.annotationType().isAnnotationPresent(AutoBuild.class)) {
				hasAutoBuild = true;
				break;
			}
		}
		if (!hasAutoBuild) {
			LogUtil.log.debug("移除类>>" + clazz.getName());
			BeanContainer.wipeClazzFromContainer(clazz);
		}
		if (!CommonUtil.isNullOrEmpty(annotations)) {
			Set<Annotation> fieldAnnotations = new HashSet<Annotation>();
			Set<Annotation> methodAnnotations = new HashSet<Annotation>();
			for (Annotation annotation : annotations) {
				Target targe = annotation.annotationType().getAnnotation(Target.class);
				if (targe == null) {
					continue;
				}
				Set<ElementType> targeAnnotations = new HashSet<ElementType>(Arrays.asList(targe.value()));
				if (targeAnnotations.contains(ElementType.FIELD)) {
					FieldDeliver deliver = annotation.annotationType().getAnnotation(FieldDeliver.class);
					if (deliver == null) {
						continue;
					}
					fieldAnnotations.add(annotation);
					continue;
				}
				if (targeAnnotations.contains(ElementType.METHOD)) {
					MethodDeliver deliver = annotation.annotationType().getAnnotation(MethodDeliver.class);
					if (deliver == null) {
						continue;
					}
					methodAnnotations.add(annotation);
					continue;
				}
			}
			if (!CommonUtil.isNullOrEmpty(fieldAnnotations)) {
				// 字段注解克隆
				for (Field field : clazz.getDeclaredFields()) {
					PropertUtil.addAnnotations(field, fieldAnnotations.toArray(new Annotation[] {}));
				}
			}
			if (!CommonUtil.isNullOrEmpty(methodAnnotations)) {
				// 方法注解克隆
				for (Method method : clazz.getDeclaredMethods()) {
					PropertUtil.addAnnotations(method, methodAnnotations.toArray(new Annotation[] {}));
				}
			}
		}
		if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
			cloneSuperAnnotationToChilder(clazz.getSuperclass(), clazz);
		}
		if (clazz.getInterfaces() != null) {
			for (Class<?> interfaceClazz : clazz.getInterfaces()) {
				cloneSuperAnnotationToChilder(interfaceClazz, clazz);
			}
		}
	}
}
