package org.coody.framework.adapt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.adapt.iface.IcopParamsAdapt;

/**
 * form表单装载到单个bean
 * @author Coody
 *
 */
public class FormToSingleBeanAdapt implements IcopParamsAdapt{

	@Override
	public Object[] doAdapt(Class<?>[] paramTypes, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
