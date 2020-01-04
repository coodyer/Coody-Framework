package org.coody.framework.serializer.adapter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.coody.framework.serializer.adapter.iface.AbstractAdapter;

public class ThrowableAdapter extends AbstractAdapter<Throwable> {

	@Override
	public String adapt(Throwable target) {
		if (target == null) {
			return null;
		}
		return "\"" + getErrorStack(target).replace("\"", "\\\"") + "\"";
	}

	private static String getErrorStack(Throwable e) {
		String error = null;
		if (e != null) {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(byteArrayOutputStream);
				e.printStackTrace(printStream);
				error = byteArrayOutputStream.toString();
				byteArrayOutputStream.close();
				printStream.close();
			} catch (Exception e1) {
				error = e.toString();
			}
		}
		return error;
	}
}
