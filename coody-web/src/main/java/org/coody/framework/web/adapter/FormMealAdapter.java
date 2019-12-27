package org.coody.framework.web.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.core.constant.InsideTypeConstant;
import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.web.adapter.iface.CoodyParameterAdapter;
import org.coody.framework.web.annotation.ParamName;
import org.coody.framework.web.entity.MvcMapping;
import org.coody.framework.web.util.RequestUtil;

/**
 * form表单装载到多个bean
 * 
 * @author admin
 *
 */
public class FormMealAdapter extends CoodyParameterAdapter {

	@Override
	public Object[] adapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		if (StringUtil.isNullOrEmpty(mapping.getParameters())) {
			return null;
		}
		Object[] params = new Object[mapping.getParameters().size()];
		for (int i = 0; i < mapping.getParameters().size(); i++) {
			FieldEntity beanEntity = mapping.getParameters().get(i);
			if (beanEntity.getFieldType().isAssignableFrom(request.getClass())) {
				params[i] = request;
				continue;
			}
			if (beanEntity.getFieldType().isAssignableFrom(response.getClass())) {
				params[i] = response;
				continue;
			}
			if (beanEntity.getFieldType().isAssignableFrom(session.getClass())) {
				params[i] = session;
				continue;
			}
			if (BaseModel.class.isAssignableFrom(beanEntity.getFieldType())) {
				params[i] = RequestUtil.getBeanAll(request, null, beanEntity.getFieldType(), true);
				continue;
			}
			if (beanEntity.getFieldType().isPrimitive()
					|| InsideTypeConstant.INSIDE_TYPES.contains(beanEntity.getFieldType())) {
				ParamName paramNameAnnotion = beanEntity.getFieldType().getAnnotation(ParamName.class);
				String paraName = beanEntity.getFieldName();
				if (paramNameAnnotion != null) {
					paraName = paramNameAnnotion.value();
				}
				params[i] = PropertUtil.parseValue(request.getParameter(paraName), beanEntity.getFieldType());
				continue;
			}
		}
		return params;
	}

}