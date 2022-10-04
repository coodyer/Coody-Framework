package org.coody.framework.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * @author Coody
 * 
 */
public class GZIPUtils {

	public static final String GZIP_ENCODE_UTF_8 = "UTF-8";

	/**
	 * 字符串压缩为GZIP字节数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] compress(String str) {
		try {
			return compress(str.getBytes(GZIP_ENCODE_UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 字符串压缩为GZIP字节数组
	 * 
	 * @param str
	 * @param encoding
	 * @return
	 */
	public static byte[] compress(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(data);
		} catch (IOException e) {
		} finally {
			try {
				gzip.close();
			} catch (Exception e2) {
			}
			try {
				out.close();
			} catch (Exception e2) {
			}
		}
		return out.toByteArray();
	}

	/**
	 * GZIP解压缩
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] uncompress(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		GZIPInputStream ungzip = null;
		try {
			ungzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = ungzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}

		} catch (IOException e) {
		} finally {
			try {
				in.close();
			} catch (Exception e2) {
			}
			try {
				out.close();
			} catch (Exception e2) {
			}
			try {
				ungzip.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return out.toByteArray();
	}
}