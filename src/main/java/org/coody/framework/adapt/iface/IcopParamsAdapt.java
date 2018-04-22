package org.coody.framework.adapt.iface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 参数适配器
 * @author admin
 *
 */
public interface IcopParamsAdapt {

	/**
	 * 参数自动装载，目前支持简单的HTTP参数
	 * @param paramTypes
	 * @param paras
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	public Object[] doAdapt(Class<?> []paramTypes,HttpServletRequest request,HttpServletResponse response,HttpSession session);
}
