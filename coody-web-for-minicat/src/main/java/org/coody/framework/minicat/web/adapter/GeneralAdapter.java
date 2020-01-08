package org.coody.framework.minicat.web.adapter;

import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.minicat.http.iface.MinicatServletRequest;
import org.coody.framework.minicat.http.iface.MinicatServletResponse;
import org.coody.framework.minicat.http.iface.MinicatHttpSession;
import org.coody.framework.minicat.web.adapter.iface.CoodyParameterAdapter;
import org.coody.framework.minicat.web.entity.MvcMapping;

/**
 * 装载request、response、session等参数
 * 
 * @author admin
 *
 */
public class GeneralAdapter extends CoodyParameterAdapter {

	@Override
	public Object[] adapt(MvcMapping mapping, MinicatServletRequest request, MinicatServletResponse response,
			MinicatHttpSession session) {
		if (CommonUtil.isNullOrEmpty(mapping.getParameters())) {
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
		}
		return params;
	}
}
