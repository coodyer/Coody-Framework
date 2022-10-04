package org.coody.framework.rcc.container;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.model.BaseModel;

public class RccContainer {

	public static final Map<String, RccInvoker> SERVER_MAPPING = new ConcurrentHashMap<String, RccContainer.RccInvoker>();

	@SuppressWarnings("serial")
	public static class RccInvoker extends BaseModel {
		private Object bean;

		private Method method;

		public Object getBean() {
			return bean;
		}

		public void setBean(Object bean) {
			this.bean = bean;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object invoke(Object[] parameter) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return method.invoke(bean, parameter);
		}

	}
}
