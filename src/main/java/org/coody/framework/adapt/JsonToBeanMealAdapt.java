package org.coody.framework.adapt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.adapt.iface.IcopParamsAdapt;
import org.coody.framework.entity.MvcMapping;

/**
 * form表单混合装载
 * @author admin
 *
 */
public class JsonToBeanMealAdapt implements IcopParamsAdapt{

	@Override
	public Object[] doAdapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
