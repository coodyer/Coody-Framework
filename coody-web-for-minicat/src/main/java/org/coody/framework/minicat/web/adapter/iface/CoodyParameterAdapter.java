package org.coody.framework.minicat.web.adapter.iface;

import java.util.Map;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.ant.AntUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.http.iface.MinicatHttpSession;
import org.coody.framework.minicat.http.iface.MinicatServletRequest;
import org.coody.framework.minicat.http.iface.MinicatServletResponse;
import org.coody.framework.minicat.web.entity.MvcMapping;

/**
 * 参数适配器
 * 
 * @author admin
 *
 */
public abstract class CoodyParameterAdapter {

	/**
	 * 参数自动装载，目前支持简单的HTTP参数
	 * 
	 * @param mapping
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	public Object[] doAdapt(MvcMapping mapping, MinicatServletRequest request, MinicatServletResponse response,
			MinicatHttpSession session) {

		Object[] parameters = adapt(mapping, request, response, session);
		// 装在URL参数
		if (mapping.getIsAntPath()) {
			if (CommonUtil.isNullOrEmpty(mapping.getParameters())) {
				return parameters;
			}
			Map<String, String> extractTemplateVariables = AntUtil.extractTemplateVariables(mapping.getPath(),
					request.getRequestURI());
			if (CommonUtil.isNullOrEmpty(extractTemplateVariables)) {
				return parameters;
			}
			for (int i = 0; i < mapping.getParameters().size(); i++) {
				FieldEntity entity = mapping.getParameters().get(i);
				String parameter = extractTemplateVariables.get(entity.getFieldName());
				if (parameter == null) {
					continue;
				}
				parameters[i] = PropertUtil.parseValue(parameter, entity.getFieldType());
			}
		}
		return parameters;
	}

	public abstract Object[] adapt(MvcMapping mapping, MinicatServletRequest request, MinicatServletResponse response,
			MinicatHttpSession session);
}
