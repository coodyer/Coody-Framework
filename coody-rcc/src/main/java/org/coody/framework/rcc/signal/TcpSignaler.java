package org.coody.framework.rcc.signal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.rcc.entity.RccSignalerEntity;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.pool.RccThreadPool;
import org.coody.framework.rcc.signal.iface.RccSignaler;

public class TcpSignaler implements RccSignaler {

	@SuppressWarnings({ "resource", "unused" })
	@Override
	public void doService(int port) {
		try {
			ServerSocket server = new ServerSocket(port);
			LogUtil.log.info("启动RCC服务 >>" + port);
			while (true) {
				Socket socket = server.accept();
				RccThreadPool.SERVER_POOL.execute(new Runnable() {
					@Override
					public void run() {
						/**
						 * TODO 解析报文 根据报文调度目标进行方法调用 响应报文
						 */
					}
				});
			}
		} catch (Exception e) {
			throw new RccException("服务启动失败", e);
		}
	}

	@Override
	public byte[] doConsume(RccSignalerEntity signaler) {
		Socket socket = null;
		try {
			socket = new Socket(signaler.getRcc().getHost(), signaler.getRcc().getPort());
			socket.setSoTimeout(signaler.getExpireTime());
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
