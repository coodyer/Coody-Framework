package org.coody.framework.adapt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.adapt.iface.IcopParamsAdapt;
import org.coody.framework.util.StringUtil;

/**
 * 装载request、response、session等参数
 * 
 * @author admin
 *
 */
public class GeneralAdapt implements IcopParamsAdapt{

	@Override
	public Object[] doAdapt(Class<?>[] paramTypes, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		if (StringUtil.isNullOrEmpty(paramTypes)) {
			return null;
		}
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paraType = paramTypes[i];
			if (paraType.isAssignableFrom(request.getClass())) {
				params[i] = request;
				continue;
			}
			if (paraType.isAssignableFrom(response.getClass())) {
				params[i] = response;
				continue;
			}
			if (paraType.isAssignableFrom(session.getClass())) {
				params[i] = session;
				continue;
			}
		}
		return params;
	}
}
