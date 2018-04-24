package org.coody.framework.adapt;

import java.util.HashMap;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * json表单混合装载到bean。谁有谁得
 * 
 * @author admin
 *
 */
public class JsonMealAdapt implements IcopParamsAdapt {

	@Override
	public Object[] doAdapt(MvcMapping mapping, HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		if (StringUtil.isNullOrEmpty(mapping.getParamTypes())) {
			return null;
		}
		Object[] params = new Object[mapping.getParamTypes().size()];
		HashMap<String, Object> paraMap=null;
		String context = RequestUtil.getPostContent(request);
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
				params[i] = JSON.parseObject(context, beanEntity.getFieldType());
				continue;
			}
			if (beanEntity.getFieldType().isPrimitive()
					|| InsideTypeConstant.INSIDE_TYPES.contains(beanEntity.getFieldType())) {
				if(paraMap==null){
					paraMap=JSON.parseObject(
							context,new TypeReference<HashMap<String, Object>>(){} );
					if(paraMap==null){
						paraMap=new HashMap<String,Object>();
					}
				}
				ParamName paramNameAnnotion = beanEntity.getFieldType().getAnnotation(ParamName.class);
				String paraName = beanEntity.getFieldName();
				if (paramNameAnnotion != null) {
					paraName = paramNameAnnotion.value();
				}
				params[i] = PropertUtil.parseValue(paraMap.get(paraName), beanEntity.getFieldType());
				continue;
			}
		}
		return params;
	}
}
