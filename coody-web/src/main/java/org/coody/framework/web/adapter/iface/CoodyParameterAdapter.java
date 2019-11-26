package org.coody.framework.web.adapter.iface;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.AntUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.web.entity.MvcMapping;

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
	public Object[] doAdapt(MvcMapping mapping, HttpServletRequest request,HttpServletResponse response,
			HttpSession session) {

		Object[] parameters = adapt(mapping, request, response, session);
		// 装在URL参数
		if (mapping.getIsAntPath()) {
			if (StringUtil.isNullOrEmpty(mapping.getParameters())) {
				return parameters;
			}
			Map<String, String> extractTemplateVariables = AntUtil.extractTemplateVariables(mapping.getPath(),
					request.getRequestURI());
			if (StringUtil.isNullOrEmpty(extractTemplateVariables)) {
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

	public abstract Object[] adapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session);
}
