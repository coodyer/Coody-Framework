package org.coody.framework.core.util.random;

public class RandomUtil {

	/**
	 * 取指定范围随机数
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static Integer random(int start, int end) {
		return (int) (Math.random() * (end - start + 1)) + start;
	}

	public static float random(Float start, Float end) {
		String str = String.valueOf(start);
		String[] tabs = str.split("\\.");
		Integer startLength = 1;
		if (tabs.length == 2) {
			startLength = tabs[1].length();
		}
		str = String.valueOf(end);
		tabs = str.split("\\.");
		Integer endLength = 1;
		if (tabs.length == 2) {
			endLength = tabs[1].length();
		}
		if (endLength > startLength) {
			startLength = endLength;
		}
		start = (float) (start * Math.pow(10, startLength));
		end = (float) (end * Math.pow(10, startLength));
		return (float) (random(start.intValue(), end.intValue()) / Math.pow(10, startLength));
	}

	public static Integer randomByPr(Integer[] prs) {
		Integer total = prs[0];
		for (int i = 1; i < prs.length; i++) {
			total += prs[i];
		}
		Integer random = random(1, total);
		for (int i = 0; i < prs.length; i++) {
			if (random <= prs[i]) {
				return i;
			}
			random = random - prs[i];
		}
		return 0;
	}

}
