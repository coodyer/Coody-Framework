package org.coody.framework.core.util.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	private static final String BLANK_STR_PATTEN = "\\s*|\t|\r|\n";

	public static String replaceBlank(String string) {
		String dest = "";
		if (string != null) {
			Pattern p = Pattern.compile(BLANK_STR_PATTEN);
			Matcher m = p.matcher(string);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static String stringCutCenter(String allTxt, String firstTxt, String lastTxt) {
		try {
			String tmp = "";
			int n1 = allTxt.indexOf(firstTxt);
			if (n1 == -1) {
				return "";
			}
			tmp = allTxt.substring(n1 + firstTxt.length(), allTxt.length());
			int n2 = tmp.indexOf(lastTxt);
			if (n2 == -1) {
				return "";
			}
			tmp = tmp.substring(0, n2);
			return tmp;
		} catch (Exception e) {
			return "";
		}
	}

	public static List<String> stringCutCenters(String allTxt, String firstTxt, String lastTxt) {
		try {
			List<String> results = new ArrayList<String>();
			while (allTxt.contains(firstTxt)) {
				int n = allTxt.indexOf(firstTxt);
				allTxt = allTxt.substring(n + firstTxt.length(), allTxt.length());
				n = allTxt.indexOf(lastTxt);
				if (n == -1) {
					return results;
				}
				String result = allTxt.substring(0, n);
				results.add(result);
				allTxt = allTxt.substring(n + firstTxt.length(), allTxt.length());
			}
			return results;
		} catch (Exception e) {
			return null;
		}
	}

}
