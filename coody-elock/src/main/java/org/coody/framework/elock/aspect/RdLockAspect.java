package org.coody.framework.elock.aspect;

import java.lang.reflect.Method;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.entity.AspectPoint;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.elock.LockHandle;
import org.coody.framework.elock.annotation.DLock;

/**
 * 
 * @author Coody
 * @date 2018年11月13日
 */
public class RdLockAspect {

	/**
	 * 分布式锁
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass=DLock.class)
	public Object rdLock(AspectPoint point) throws Throwable {
		String lockName = null;
			// AOP启动监听
			Method method = point.getAbler().getMethod();
			Class<?> clazz = point.getAbler().getClazz();

			DLock dlock = method.getAnnotation(DLock.class);
			lockName = dlock.name();
			if (StringUtil.isNullOrEmpty(lockName)) {
				lockName = MethodSignUtil.getMethodKey(clazz, method);
			}
			Object[] paras = point.getParams();
			if (dlock.fields() != null && dlock.fields().length > 0) {
				lockName = MethodSignUtil.getFieldKey(clazz,method, paras, lockName,dlock.fields());
			}
			LockHandle.lock(lockName,dlock.waitTime());
			try {
				return point.invoke();
			} catch (Exception e) {
				if (!dlock.igonreException()) {
					throw e;
				}
				return null;
			}
	}

	

}
