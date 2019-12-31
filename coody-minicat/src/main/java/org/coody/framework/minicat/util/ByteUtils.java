package org.coody.framework.minicat.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.util.StringUtil;
import org.coody.framework.minicat.config.MiniCatConfig;

@SuppressWarnings("unchecked")
public class ByteUtils {

	public static byte[] readLine(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = buildToOutputStream(inputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			return null;
		} finally {
			outputStream.close();
		}
	}

	public static String readLineString(InputStream inputStream) {
		try {
			byte[] data = readLine(inputStream);
			if (StringUtil.isNullOrEmpty(data)) {
				return null;
			}
			return new String(data, MiniCatConfig.encode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getBytes(InputStream inputStream) {
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = buildToOutputStream(inputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {

			return null;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {

			}
		}
	}

	public static byte[] getBytes(SocketChannel channel, Integer length) {
		if (length < 1) {
			return null;
		}
		int company = 1024;
		if (length < company) {
			company = length;
		}
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			ByteBuffer buff = ByteBuffer.allocate(company);
			int totalReadLength = 0;
			while (totalReadLength < length) {
				try {
					int rcLength = channel.read(buff);
					if (rcLength == 0) {
						TimeUnit.MICROSECONDS.sleep(1);
					}
					totalReadLength += rcLength;
					buff.flip();
					byte[] data = new byte[buff.remaining()];
					buff.get(data);
					outputStream.write(data);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					buff.flip();
					buff.clear();
				}

			}
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static byte[] getBytes(InputStream inputStream, Integer length) {
		if (length < 1) {
			return null;
		}
		if (inputStream == null) {
			return null;
		}
		int company = MiniCatConfig.maxHeaderLength;
		if (length < company) {
			company = length;
		}
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[company];
			int totalReadLength = 0;
			while (totalReadLength < length) {
				int rcLength = inputStream.read(buff);
				if (rcLength == 0) {
					TimeUnit.MICROSECONDS.sleep(1);
					break;
				}
				totalReadLength += rcLength;
				outputStream.write(buff, 0, rcLength);
				if (rcLength < buff.length) {
					break;
				}
			}
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static <T extends OutputStream> T buildToOutputStream(InputStream inputStream) {
		OutputStream outputStream = new ByteArrayOutputStream();
		try {
			buildToOutputStream(inputStream, outputStream);
			return (T) outputStream;
		} catch (Exception e) {

			return null;
		}
	}

	public static <T extends OutputStream> T buildToOutputStream(InputStream inputStream, OutputStream outputStream) {
		if (outputStream == null) {
			outputStream = new ByteArrayOutputStream();
		}
		try {
			outputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
				outputStream.write(buff, 0, rc);
			}
			return (T) outputStream;
		} catch (Exception e) {

			return null;
		}
	}
}
