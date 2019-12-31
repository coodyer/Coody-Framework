package org.coody.framework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.core.util.UnsafeUtil;

/**
 * @remark HTTP工具类。
 * @author Coody
 * @email 644556636@qq.com
 * @blog 54sb.org
 */
@SuppressWarnings("unchecked")
public class RequestUtil {

	/**
	 * 根据Request获取Model。排除指定参数
	 * 
	 * @param request  请求对象
	 * @param obj      实例化的Model对象
	 * @param paraArgs 指定参数
	 * @return
	 */
	public static <T> T getBeanRemove(HttpServletRequest request, String paraName, Object obj, boolean removeEmpty,
			String... paraArgs) {
		return getBean(request, obj, null, paraName, null, true, paraArgs, removeEmpty);
	}

	/**
	 * 根据Request获取Model。接受指定参数
	 * 
	 * @param request  请求对象
	 * @param obj      实例化的Model对象
	 * @param paraArgs 指定参数
	 * @return
	 */
	public static <T> T getBeanAccept(HttpServletRequest request, String paraName, Object obj, boolean removeEmpty,
			String... paraArgs) {
		return getBean(request, obj, null, paraName, null, false, paraArgs, removeEmpty);
	}

	/**
	 * 根据Request获取Model所有参数
	 * 
	 * @param request 请求对象
	 * @param obj     实例化的Model对象
	 * @return
	 */
	public static <T> T getBeanAll(HttpServletRequest request, String paraName, Object obj, boolean removeEmpty) {
		return getBean(request, obj, null, paraName, null, true, null, removeEmpty);
	}

	private static <T> T getBean(HttpServletRequest request, Object obj, List<FieldEntity> fields, String baseName,
			String firstSuffix, Boolean isReplace, String[] paraArgs, boolean removeEmpty) {
		try {
			if (obj instanceof Class) {
				obj = UnsafeUtil.createInstance((Class<?>) obj);
			}
			firstSuffix = StringUtil.isNullOrEmpty(firstSuffix) ? "" : (firstSuffix + ".");
			isReplace = StringUtil.isNullOrEmpty(isReplace) ? false : isReplace;
			baseName = StringUtil.isNullOrEmpty(baseName) ? "" : (baseName + ".");
			List<String> paras = null;
			if (!StringUtil.isNullOrEmpty(paraArgs)) {
				paras = new ArrayList<String>();
				for (String tmp : paraArgs) {
					if (StringUtil.isNullOrEmpty(tmp)) {
						continue;
					}
					String[] tab = tmp.split("\\.");
					for (String tmpTab : tab) {
						paras.add(tmpTab);
					}
				}
			}
			if (StringUtil.isNullOrEmpty(fields)) {
				// 获取对象字段属性
				fields = PropertUtil.getBeanFields(obj);
			}
			firstSuffix = (firstSuffix == null) ? "" : firstSuffix;
			Object childObj, paraValue = null;
			String paraName = null;
			for (FieldEntity entity : fields) {
				try {
					paraName = firstSuffix + baseName + entity.getFieldName();
					if (!StringUtil.isNullOrEmpty(paras)) {
						if (isReplace) {
							if (paras.contains(paraName)) {
								continue;
							}
						}
						if (!isReplace) {
							if (!paras.contains(paraName)) {
								continue;
							}
						}
					}
					if (BaseModel.class.isAssignableFrom(entity.getFieldType())) {
						childObj = UnsafeUtil.createInstance(entity.getFieldType());
						childObj = getBean(request, childObj, null, paraName, firstSuffix, isReplace, paraArgs,
								removeEmpty);
						PropertUtil.setProperties(obj, entity.getFieldName(), childObj);
						continue;
					}
					paraValue = request.getParameter(paraName);
					if (paraValue == null) {
						continue;
					}
					if (removeEmpty && StringUtil.isNullOrEmpty(paraValue)) {
						continue;
					}
					PropertUtil.setProperties(obj, entity.getFieldName(), paraValue);

				} catch (Exception e) {
				}
			}
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取POST请求参数中数据
	 * 
	 * @param request
	 * @throws IOException
	 */
	public static String getPostContent(HttpServletRequest request) {
		String content = null;
		try {
			content = inputStream2String(request.getInputStream());
			content = URLDecoder.decode(content, "UTF-8");
		} catch (Exception e) {

		}
		return content;
	}

	public static String inputStream2String(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

}
