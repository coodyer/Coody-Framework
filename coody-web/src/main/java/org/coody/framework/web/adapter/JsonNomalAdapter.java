package org.coody.framework.web.adapter;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.Cson;
import org.coody.framework.adapter.TypeAdapter;
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
 * json装载到多个bean,以beanname为准
 * 
 * @author admin
 *
 */
public class JsonNomalAdapter extends CoodyParameterAdapter {

	@Override
	public Object[] adapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		if (StringUtil.isNullOrEmpty(mapping.getParameters())) {
			return null;
		}
		Object[] params = new Object[mapping.getParameters().size()];
		HashMap<String, Object> paraMap = null;
		String context = RequestUtil.getPostContent(request);
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
				ParamName paramName = beanEntity.getFieldType().getAnnotation(ParamName.class);
				String paraName = beanEntity.getFieldName();
				if (paramName != null) {
					paraName = paramName.value();
				}
				if (StringUtil.isNullOrEmpty(paraName)) {
					params[i] = Cson.toObject(context, beanEntity.getFieldType());
					continue;
				}
				if (paraMap == null) {
					paraMap = Cson.toObject(context, new TypeAdapter<HashMap<String, Object>>() {
					});
					if (paraMap == null) {
						paraMap = new HashMap<String, Object>();
					}
				}
				String json = StringUtil.toString(paraMap.get(paraName));
				params[i] = Cson.toObject(json, beanEntity.getFieldType());
				continue;
			}
			if (beanEntity.getFieldType().isPrimitive()
					|| InsideTypeConstant.INSIDE_TYPES.contains(beanEntity.getFieldType())) {
				ParamName paramNameAnnotion = beanEntity.getFieldType().getAnnotation(ParamName.class);
				String paraName = beanEntity.getFieldName();
				if (paramNameAnnotion != null) {
					paraName = paramNameAnnotion.value();
				}
				if (paraMap == null) {
					paraMap = Cson.toObject(context, new TypeAdapter<HashMap<String, Object>>() {
					});
					if (paraMap == null) {
						paraMap = new HashMap<String, Object>();
					}
				}
				Object value = paraMap.get(paraName);
				params[i] = PropertUtil.parseValue(value, beanEntity.getFieldType());
				continue;
			}
		}
		return params;
	}

}
