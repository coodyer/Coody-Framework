package org.coody.framework.core.util.random;

public class RandomUtil {

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
}
