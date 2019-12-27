package org.coody.framework.minicat.http.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.coody.framework.minicat.config.MiniCatConfig;

public class MiniCatOutputStream extends ByteArrayOutputStream{

	public void print(String s) throws IOException {
		write(s.getBytes(MiniCatConfig.encode));
	}

	public void write(String s) throws IOException {
		print(s);
	}

	public void print(boolean b) throws IOException {
		String msg;
		if (b)
			msg = "true";
		else {
			msg = "false";
		}
		print(msg);
	}

	public void print(char c) throws IOException {
		print(String.valueOf(c));
	}

	public void print(int i) throws IOException {
		print(String.valueOf(i));
	}

	public void print(long l) throws IOException {
		print(String.valueOf(l));
	}

	public void print(float f) throws IOException {
		print(String.valueOf(f));
	}

	public void print(double d) throws IOException {
		print(String.valueOf(d));
	}

	public void println() throws IOException {
		print("\r\n");
	}

	public void println(String s) throws IOException {
		print(s);
		println();
	}

	public void println(boolean b) throws IOException {
		print(b);
		println();
	}

	public void println(char c) throws IOException {
		print(c);
		println();
	}

	public void println(int i) throws IOException {
		print(i);
		println();
	}

	public void println(long l) throws IOException {
		print(l);
		println();
	}

	public void println(float f) throws IOException {
		print(f);
		println();
	}

	public void println(double d) throws IOException {
		print(d);
		println();
	}

}