package org.coody.framework.init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coody.framework.adapt.GeneralAdapt;
import org.coody.framework.adapt.iface.IcopParamsAdapt;
import org.coody.framework.annotation.Around;
import org.coody.framework.annotation.CronTask;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.OutBean;
import org.coody.framework.annotation.ParamsAdapt;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.aspect.entity.AspectEntity;
import org.coody.framework.constant.FrameworkConstant;
import org.coody.framework.container.BeanContainer;
import org.coody.framework.container.BuiltContainer;
import org.coody.framework.container.MappingContainer;
import org.coody.framework.exception.BeanNotFoundException;
import org.coody.framework.exception.ErrorCronException;
import org.coody.framework.iface.InitFace;
import org.coody.framework.proxy.CglibProxy;
import org.coody.framework.task.TaskTrigger;
import org.coody.framework.util.AspectUtil;
import org.coody.framework.util.ClassUtil;
import org.coody.framework.util.PrintException;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

public class FrameworkRute {

	private static final Logger logger = Logger.getLogger(FrameworkRute.class);

	static CglibProxy proxy = new CglibProxy();

	public static void init(String... packets) throws Exception {
		Set<Class<?>> clazzs = new HashSet<Class<?>>();

		for (String packet : packets) {
			Set<Class<?>> clazzsTemp = ClassUtil.getClasses(packet);
			clazzs.addAll(clazzsTemp);
		}
		if (StringUtil.isNullOrEmpty(clazzs)) {
			return;
		}
		for (Class<?> clazz : BuiltContainer.INIT_BEAN) {
			clazzs.add(clazz);
		}
		initAspect(clazzs);
		initTask(clazzs);
		initClass(clazzs);
		initField();
		initMvc(clazzs);
		initRun(clazzs);

	}

	public static void initTask(Set<Class<?>> clas) {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {
			InitBean initBean = cla.getAnnotation(InitBean.class);
			if (StringUtil.isNullOrEmpty(initBean)) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask cronTask = method.getAnnotation(CronTask.class);
				if (StringUtil.isNullOrEmpty(cronTask) || StringUtil.isNullOrEmpty(cronTask.value())) {
					continue;
				}
				AspectEntity aspectEntity=new AspectEntity();
				//装载切面控制方法
				aspectEntity.setAnnotationClass(new Class<?>[]{CronTask.class});
				aspectEntity.setAspectInvokeMethod(TaskTrigger.getTriggerMethod());
				FrameworkConstant.writeToAspectMap(AspectUtil.getBeanKey(TaskTrigger.getTriggerMethod()), aspectEntity);
			}
		}
	}

	public static void initAspect(Set<Class<?>> clas) {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {

			if (cla.getAnnotation(InitBean.class)==null&&cla.getAnnotation(PathBinding.class)==null) {
				continue;
			}
			Method[] methods = cla.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				Around[] arounds = method.getAnnotationsByType(Around.class);
				if(StringUtil.isNullOrEmpty(arounds)){
					continue;
				}
				for (Around around : arounds) {
					if (around == null) {
						continue;
					}
					if (StringUtil.isAllNull(around.annotationClass(), around.classMappath(), around.annotationClass(),
							around.methodMappath())) {
						continue;
					}
					AspectEntity aspectEntity=new AspectEntity();
					//装载切面控制方法
					aspectEntity.setAnnotationClass(around.annotationClass());
					aspectEntity.setMethodMappath(around.methodMappath());
					aspectEntity.setClassMappath(around.classMappath());
					aspectEntity.setAspectInvokeMethod(method);
					String methodKey=AspectUtil.getBeanKey(method);
					FrameworkConstant.writeToAspectMap(methodKey, aspectEntity);
				}
			}
		}
	}

	public static void initClass(Set<Class<?>> clas) throws Exception {
		if (StringUtil.isNullOrEmpty(clas)) {
			return;
		}
		for (Class<?> cla : clas) {
			List<String> beanNames = BeanContainer.getBeanNames(cla);
			if (StringUtil.isNullOrEmpty(beanNames)) {
				continue;
			}
			Object bean = proxy.getProxy(cla);
			for (String beanName : beanNames) {
				if (StringUtil.isNullOrEmpty(beanName)) {
					continue;
				}
				if (BeanContainer.containsBean(beanName)) {
					logger.error("存在重复的bean:" + beanName);
					continue;
				}
				BeanContainer.writeBean(beanName, bean);
			}
		}
	}

	public static void initRun(Set<Class<?>> clas) {
		for (Class<?> clazz : clas) {
			InitBean initBean = clazz.getAnnotation(InitBean.class);
			if (initBean == null) {
				continue;
			}
			Object bean = BeanContainer.getBean(clazz);
			if (InitFace.class.isAssignableFrom(bean.getClass())) {
				// 初始化运行
				try {
					InitFace face = (InitFace) bean;
					if (StringUtil.isNullOrEmpty(face)) {
						continue;
					}
					face.init();
				} catch (Exception e) {
					PrintException.printException(logger, e);
				}
			}
			// 执行定时任务
			Method[] methods = clazz.getDeclaredMethods();
			if (StringUtil.isNullOrEmpty(methods)) {
				continue;
			}
			for (Method method : methods) {
				CronTask task = method.getAnnotation(CronTask.class);
				if (task == null) {
					continue;
				}
				try {
					if (StringUtil.isNullOrEmpty(task.value())) {
						PrintException.printException(logger, new ErrorCronException(
								"CRON有误:" + bean.getClass() + ":" + method.getName() + ",Cron:" + task.value()));
						continue;
					}
					TaskTrigger.nextRun(bean, method, task.value(), null);
				} catch (Exception e) {
					PrintException.printException(logger, new ErrorCronException(
							"CRON有误:" + bean.getClass() + ":" + method.getName() + ",Cron:" + task.value()));
					continue;
				}
			}
		}
	}

	public static void initField() throws Exception {
		for (Object bean : BeanContainer.getBeans()) {
			List<Field> fields = loadFields(bean.getClass());
			if (StringUtil.isNullOrEmpty(fields)) {
				continue;
			}
			for (Field field : fields) {
				OutBean writeBean = field.getAnnotation(OutBean.class);
				if (StringUtil.isNullOrEmpty(writeBean)) {
					continue;
				}
				String beanName = writeBean.beanName();
				if (StringUtil.isNullOrEmpty(beanName)) {
					beanName = field.getType().getName();
				}
				if (!BeanContainer.containsBean(beanName)) {
					throw new BeanNotFoundException(
							"注入失败:" + bean.getClass() + ",未找到Bean:" + field.getName() + "(" + field.getType() + ")");
				}
				Object writeValue = null;
				field.setAccessible(true);
				writeValue = BeanContainer.getBean(beanName);
				field.set(bean, writeValue);
			}
		}
	}

	public static void initMvc(Set<Class<?>> clazzs) throws Exception {
		for (Class<?> clazz : clazzs) {
			Object bean = BeanContainer.getBean(clazz);
			if (StringUtil.isNullOrEmpty(bean)) {
				continue;
			}
			PathBinding classBindings = clazz.getAnnotation(PathBinding.class);
			if (StringUtil.isNullOrEmpty(classBindings)) {
				continue;
			}
			Method[] methods = clazz.getDeclaredMethods();
			ParamsAdapt clazzParamsAdapt=clazz.getAnnotation(ParamsAdapt.class);
			for (String clazzBinding : classBindings.value()) {
				for (Method method : methods) {
					PathBinding methodBinding = method.getAnnotation(PathBinding.class);
					if (StringUtil.isNullOrEmpty(methodBinding)) {
						continue;
					}
					for (String bindingPath : methodBinding.value()) {
						String path = StringUtil.formatPath(clazzBinding + "/" + bindingPath);
						if (MappingContainer.containsPath(path)) {
							logger.error("该地址已注册:" + path);
							continue;
						}
						Class<?> adaptClass=GeneralAdapt.class;
						ParamsAdapt methodParamsAdapt=method.getAnnotation(ParamsAdapt.class);
						if(methodParamsAdapt==null){
							if(clazzParamsAdapt!=null){
								adaptClass=clazzParamsAdapt.value();
							}
						}else{
							adaptClass=methodParamsAdapt.value();
						}
						MappingContainer.MvcMapping mapping = new MappingContainer.MvcMapping();
						mapping.setBean(bean);
						mapping.setPath(path);
						mapping.setParamsAdapt(((IcopParamsAdapt)adaptClass.newInstance()));
						mapping.setMethod(method);
						mapping.setParamTypes(PropertUtil.getMethodParas(method));
						MappingContainer.writeMapping(mapping);
					}
				}
			}
		}
	}

	public static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return fields;
		}
		List<Field> childFields = loadFields(superClass);
		if (StringUtil.isNullOrEmpty(childFields)) {
			return fields;
		}
		fields.addAll(childFields);
		return fields;
	}
}
