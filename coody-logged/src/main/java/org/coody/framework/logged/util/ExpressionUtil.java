package org.coody.framework.logged.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionUtil {

	public static List<String> getParameters(String context, String patten) {

		Pattern pattern = Pattern.compile(patten, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(context);

		List<String> list = new ArrayList<String>();

		Integer index = 0;
		while (matcher.find(index)) {
			try {
				String line = matcher.group(0);
				index = matcher.end();
				if (line == null || line.length() == 0) {
					continue;
				}
				list.add(line);
			} catch (Exception e) {
			}
		}
		return list;
	}

	public static String center(String context, String front, String behind) {

		String patten = String.format("%s(.*?)%s", front, behind);

		Pattern pattern = Pattern.compile(patten, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(context);

		if (!matcher.find()) {
			return null;
		}
		return matcher.group(1);
	}

}
