package org.coody.framework.core.util.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coody.framework.core.util.CommonUtil;

public class MatchUtil {

	private static final String MOBILE_PATTEN = "^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0,5-9]))\\d{8}$";

	private static final String LEGAL_PATTEN = "[A-Za-z0-9_]{3,16}";

	private static final String EMAIL_PATTEN = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

	private static final String MD5_PATTEN = "[A-Za-z0-9_]{16,40}";

	public static boolean isMobile(String mobile) {
		if (CommonUtil.isNullOrEmpty(mobile)) {
			return false;
		}
		Pattern p = Pattern.compile(MOBILE_PATTEN);
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	public static boolean isLegal(String str) {
		if (CommonUtil.isNullOrEmpty(str)) {
			return false;
		}
		Pattern p = Pattern.compile(LEGAL_PATTEN);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static boolean isEmail(String email) {
		if (CommonUtil.isNullOrEmpty(email)) {
			return false;
		}
		Pattern p = Pattern.compile(EMAIL_PATTEN);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isMd5(String md5) {
		if (CommonUtil.isNullOrEmpty(md5)) {
			return false;
		}
		Pattern p = Pattern.compile(MD5_PATTEN);
		Matcher m = p.matcher(md5);
		return m.matches();
	}

	public static Boolean isMatcher(String val, String matcher) {
		Pattern p = Pattern.compile(matcher);
		Matcher m = p.matcher(val);
		return m.matches();
	}

	public static boolean isMessyCode(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ((int) c == 0xfffd) {
				return true;
			}
		}
		return false;
	}

	// 判断是否为数字
	public static Boolean isNumber(String str) {
		if (CommonUtil.isNullOrEmpty(str)) {
			return false;
		}
		try {
			Integer.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static Boolean isParameterMatch(String val, String mateher) {
		try {
			if (CommonUtil.isNullOrEmpty(val)) {
				return false;
			}
			List<String> paraNames = getParameters(mateher);
			mateher = formatMatchContext(mateher, paraNames);
			return isMatcher(val, mateher);
		} catch (Exception e) {

			return false;
		}
	}

	public static String formatMatchContext(String matchContext, List<String> parameterNames) {
		String exportPat = "([A-Za-z0-9_]+)";
		for (String parameterName : parameterNames) {
			matchContext = matchContext.replace("${" + parameterName + "}", exportPat);
		}
		return matchContext;
	}

	public static List<String> exporeMatcheds(String context, String matchContext) {
		String exportPat = "([A-Za-z0-9_]+)";
		String[] pattenTrunk = matchContext.split("\\(\\[A-Za-z0-9_\\]\\+\\)");
		List<String> results = new ArrayList<String>();
		String mapper = "";
		for (int i = 0; i < pattenTrunk.length; i++) {
			mapper += pattenTrunk[i];
			String patten = mapper + exportPat;
			String value = exporeMatchedFirstByRegular(context, patten);
			mapper += value;
			results.add(value);
		}
		return results;
	}

	public static Map<String, String> exporeMatchedMap(String context, String matchContext) {
		List<String> paraNames = getParameters(matchContext);
		matchContext = formatMatchContext(matchContext, paraNames);
		List<String> results = exporeMatcheds(context, matchContext);
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < paraNames.size(); i++) {
			try {
				if (map.containsKey(paraNames.get(i))) {
					map.put(paraNames.get(i), map.get(paraNames.get(i)) + "," + results.get(i));
					continue;
				}
				map.put(paraNames.get(i), results.get(i));
			} catch (Exception e) {
			}
		}
		return map;
	}

	public static List<String> getParameters(String context) {
		String patten = "\\$\\{([A-Za-z0-9_]+)\\}";
		return exporeMatchedsByRegular(context, patten);
	}

	public static String exporeMatchedFirstByRegular(String context, String patten) {
		List<String> results = exporeMatchedsByRegular(context, patten);
		if (CommonUtil.isNullOrEmpty(results)) {
			return null;
		}
		return results.get(0);
	}

	public static List<String> exporeMatchedsByRegular(String context, String patten) {
		try {
			Integer index = 0;
			Pattern pattern = Pattern.compile(patten, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(context);
			List<String> results = new ArrayList<String>();
			while (matcher.find(index)) {
				String tmp = matcher.group(1);
				index = matcher.end();
				if (CommonUtil.isNullOrEmpty(tmp)) {
					continue;
				}
				results.add(tmp);
			}
			return results;
		} catch (Exception e) {
			return null;
		}
	}

}
