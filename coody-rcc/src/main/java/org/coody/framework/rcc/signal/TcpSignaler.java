package org.coody.framework.rcc.signal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.entity.RccSignalerEntity;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.pool.RccThreadPool;
import org.coody.framework.rcc.signal.iface.RccSignaler;

public class TcpSignaler implements RccSignaler {

	@SuppressWarnings("resource")
	@Override
	public void doService(RccConfig config) {
		try {
			ServerSocket server = new ServerSocket(config.getPort());
			server.setSoTimeout(config.getExpire());
			while (true) {
				Socket socket = server.accept();
				RccThreadPool.SERVER_POOL.execute(new Runnable() {
					@Override
					public void run() {

					}
				});
			}
		} catch (Exception e) {
			throw new RccException("服务启动失败", e);
		}
	}

	@Override
	public byte[] doConsume(RccConfig config, RccSignalerEntity signaler) {
		Socket socket = null;
		try {
			socket = new Socket(signaler.getRcc().getHost(), signaler.getRcc().getPort());
			socket.setSoTimeout(config.getExpire());
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(signaler.builder());
			outputStream.flush();
			socket.shutdownOutput();
			InputStream inputStream = socket.getInputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len;
			while ((len = inputStream.read(bytes)) != -1) {
				byteArrayOutputStream.write(bytes, 0, len);
			}
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			throw new RccException("发送数据出错", e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
