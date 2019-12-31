package org.coody.framework.minicat.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.coody.framework.core.model.BaseModel;
import org.coody.framework.core.model.FieldEntity;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.core.util.UnsafeUtil;
import org.coody.framework.minicat.http.iface.MinicatServletRequest;

/**
 * @remark HTTP工具类。
 * @author Coody
 * @email 644556636@qq.com
 * @blog 54sb.org
 */
@SuppressWarnings("unchecked")
public class RequestUtil {

	public static final String USER_SESSION = "CURR_LOGIN_USER";

	public static void setUser(MinicatServletRequest request, Object user) {
		request.getSession().put(USER_SESSION, user);
	}

	public static <T> T getUser(MinicatServletRequest request) {
		return (T) request.getSession().get(USER_SESSION);
	}

	public static boolean isUserLogin(MinicatServletRequest request) {
		Object obj = getUser(request);
		if (!StringUtil.isNullOrEmpty(obj)) {
			return true;
		}
		return false;
	}

	public static void setCode(MinicatServletRequest request, Object code) {
		request.getSession().put("sys_ver_code", code);
	}

	public static <T> T getCode(MinicatServletRequest request) {
		return (T) request.getSession().get("sys_ver_code");
	}

	/**
	 * 根据Request获取Model。排除指定参数
	 * 
	 * @param request  请求对象
	 * @param obj      实例化的Model对象
	 * @param paraArgs 指定参数
	 * @return
	 */
	public static <T> T getBeanRemove(MinicatServletRequest request, String paraName, Object obj, boolean removeEmpty,
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
	public static <T> T getBeanAccept(MinicatServletRequest request, String paraName, Object obj, boolean removeEmpty,
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
	public static <T> T getBeanAll(MinicatServletRequest request, String paraName, Object obj, boolean removeEmpty) {
		return getBean(request, obj, null, paraName, null, true, null, removeEmpty);
	}

	private static <T> T getBean(MinicatServletRequest request, Object obj, List<FieldEntity> fields, String baseName,
			String firstSuffix, Boolean isReplace, String[] paraArgs, boolean removeEmpty) {
		try {
			if (obj instanceof Class) {
				obj = UnsafeUtil.createInstance(((Class<?>) obj));
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
					paraValue = request.getParament(paraName);
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
	public static String getPostContent(MinicatServletRequest request) {
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
