package org.coody.framework.core.util.encrypt;

import java.net.URLEncoder;
import java.security.MessageDigest;

public final class EncryptUtil {

	public final static String CHARSET = "UTF-8";

	private EncryptUtil() {
		throw new Error("Utility classes should not instantiated!");
	}

	public static String custom(String str) {
		try {
			str = md5(str);
			str = textEncode(str, str);
			str = str.substring(1, str.length() - 1);
			str = URLEncoder.encode(str, "UTF-8").replace("%", "").toLowerCase();
			str = md5(str);
			return str;
		} catch (Exception e) {
			return "";
		}

	}

	public static String md5(String pwd) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes(CHARSET));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();

		} catch (Exception e) {

		}
		return "";
	}

	private static String textEncode(String s, String key) {
		String result = "";
		int chr;
		if (key.length() == 0) {
			return s;
		}
		if (s.equals(null)) {
			return result;
		}
		for (int i = 0, j = 0; i < s.length(); i++, j++) {
			if (j > key.length() - 1) {
				j = j % key.length();
			}
			chr = s.codePointAt(i) + key.codePointAt(j);
			if (chr > 65535) {
				chr = chr % 65535;// ch - 33 = (ch - 33) % 95 ;
			}
			result += (char) chr;
		}
		return result;

	}

	@SuppressWarnings("unused")
	private static String textDecode(String s, String key) {
		String result = "";
		int chr;
		if (key.length() == 0) {
			return s;
		}
		if (s.equals(key)) {
			return result;
		}
		for (int i = 0, j = 0; i < s.length(); i++, j++) {
			if (j > key.length() - 1) {
				j = j % key.length();
			}
			chr = (s.codePointAt(i) + 65535 - key.codePointAt(j));
			if (chr > 65535) {
				chr = chr % 65535;// ch - 33 = (ch - 33) % 95 ;
			}
			result += (char) chr;
		}
		return result;
	}
}
