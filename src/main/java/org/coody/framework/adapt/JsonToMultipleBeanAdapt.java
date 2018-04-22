package org.coody.framework.adapt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.adapt.iface.IcopParamsAdapt;

/**
 * form表单装载到多个bean
 * @author admin
 *
 */
public class JsonToMultipleBeanAdapt implements IcopParamsAdapt{

	@Override
	public Object[] doAdapt(Class<?>[] paramTypes, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
