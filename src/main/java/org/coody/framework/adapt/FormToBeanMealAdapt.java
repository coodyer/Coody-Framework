package org.coody.framework.adapt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.adapt.iface.IcopParamsAdapt;
import org.coody.framework.annotation.ParamName;
import org.coody.framework.constant.InsideTypeConstant;
import org.coody.framework.entity.BaseModel;
import org.coody.framework.entity.BeanEntity;
import org.coody.framework.entity.MvcMapping;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.RequestUtil;
import org.coody.framework.util.StringUtil;

/**
 * form表单装载到多个bean
 * 
 * @author admin
 *
 */
public class FormToBeanMealAdapt implements IcopParamsAdapt {

	@Override
	public Object[] doAdapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		if (StringUtil.isNullOrEmpty(mapping.getParamTypes())) {
			return null;
		}
		Object[] params = new Object[mapping.getParamTypes().size()];
		for (int i = 0; i < mapping.getParamTypes().size(); i++) {
			BeanEntity beanEntity = mapping.getParamTypes().get(i);
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
				params[i] = RequestUtil.getBeanAll(request, beanEntity.getFieldName(), beanEntity.getFieldType());
				continue;
			}
			if (beanEntity.getFieldType().isPrimitive()||InsideTypeConstant.INSIDE_TYPES.contains(beanEntity.getFieldType())) {
				ParamName paramName=beanEntity.getFieldType().getAnnotation(ParamName.class);
				String paraName=beanEntity.getFieldName();
				if(paramName!=null){
					paraName=paramName.value();
				}
				params[i]=PropertUtil.parseValue(request.getParameter(paraName), beanEntity.getFieldType());
				continue;
			}
		}
		return params;
	}

}