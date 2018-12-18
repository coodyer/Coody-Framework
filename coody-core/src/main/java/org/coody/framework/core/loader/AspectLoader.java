package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.Arounds;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.entity.AspectEntity;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * 切面加载器
 * 
 * @author Coody
 *
 */
public class AspectLoader implements CoodyLoader {

	private static final Logger logger = Logger.getLogger(AspectLoader.class);
	@Override
	public void doLoader() throws Exception {
		if (StringUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		for (Class<?> cla : BeanContainer.getClazzContainer()) {
			if (cla.isAnnotation()) {
				continue;
			}
			if (StringUtil.isNullOrEmpty(cla.getAnnotations())) {
				continue;
			}
			Annotation initBean = PropertUtil.getAnnotation(cla, AutoBuild.class);
			if (initBean == null) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				if(Modifier.isStatic(method.getModifiers())||Modifier.isAbstract(method.getModifiers())){
					continue;
				}
				if(StringUtil.isNullOrEmpty(method.getAnnotations())){
					continue;
				}
				List<Annotation> arounds=PropertUtil.getAnnotations(method, Around.class);
				if (StringUtil.isNullOrEmpty(arounds)) {
					List<Annotation> aroundParents=PropertUtil.getAnnotations(method, Arounds.class);
					if(StringUtil.isNullOrEmpty(aroundParents)){
						continue;
					}
					arounds=new ArrayList<Annotation>();
					for(Annotation aroundParent:aroundParents){
						Annotation[] aroundTemps=PropertUtil.getAnnotationValue(aroundParent,"value");
						if(StringUtil.isNullOrEmpty(aroundTemps)){
							continue;
						}
						arounds.addAll(Arrays.asList(aroundTemps));
					}
				}
				for (Annotation around : arounds) {
					Map<String, Object> annotationValueMap=PropertUtil.getAnnotationValueMap(around);
					Class<?>[] annotationClass= (Class<?>[]) annotationValueMap.get("annotationClass");
					String classMappath=(String) annotationValueMap.get("classMappath");
					String methodMappath=(String) annotationValueMap.get("methodMappath");
					if (StringUtil.isAllNull(annotationClass, classMappath, methodMappath)) {
						continue;
					}
					Boolean masturbation=(Boolean) annotationValueMap.get("masturbation");
					logger.debug("初始化切面方法 >>"+MethodSignUtil.getMethodKey(cla, method));
					AspectEntity aspectEntity = new AspectEntity();
					// 装载切面控制方法
					aspectEntity.setAnnotationClass(annotationClass);
					aspectEntity.setMethodMappath(methodMappath);
					aspectEntity.setClassMappath(classMappath);
					aspectEntity.setAspectInvokeMethod(method);
					aspectEntity.setMasturbation(masturbation);
					aspectEntity.setAspectClazz(cla);
					String methodKey = MethodSignUtil.getMethodUnionKey(method);
					FrameworkConstant.writeToAspectMap(methodKey, aspectEntity);
				}
			}
		}
	}

}
