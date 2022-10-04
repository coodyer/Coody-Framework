package org.coody.framework.rcc.signal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.coody.framework.core.util.ByteUtils;
import org.coody.framework.core.util.GZIPUtils;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.rcc.container.RccContainer;
import org.coody.framework.rcc.container.RccContainer.RccInvoker;
import org.coody.framework.rcc.entity.RccSignalerEntity;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.instance.RccKeepInstance;
import org.coody.framework.rcc.pool.RccThreadPool;
import org.coody.framework.rcc.signal.iface.RccSignaler;

public class TcpSignaler implements RccSignaler {

	@SuppressWarnings({ "resource" })
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
						doAccept(socket);
					}
				});
			}
		} catch (Exception e) {
			throw new RccException("服务启动失败", e);
		}
	}

	public void doAccept(Socket socket) {
		/**
		 * TODO 解析报文 根据报文调度目标进行方法调用 响应报文
		 */
		try {
			String line = ByteUtils.readLineString(socket.getInputStream(), "UTF-8");

			Integer length = Integer.valueOf(line.trim());

			byte[] data = ByteUtils.getBytes(socket.getInputStream(), length);

			RccSignalerEntity signaler = RccKeepInstance.serialer.unSerialize(GZIPUtils.uncompress(data));
			// 寻找调用目标
			RccInvoker rcc = RccContainer.SERVER_MAPPING.get(signaler.getRcc().getPath());

			try {
				Object result = rcc.invoke(RccKeepInstance.serialer.unSerialize(signaler.getData()));
				signaler.setData(RccKeepInstance.serialer.serialize(result));
			} catch (Exception e) {
				signaler.setException(e);
			}
			data = GZIPUtils.compress(RccKeepInstance.serialer.serialize(signaler));
			socket.getOutputStream().write((data.length + "\r\n").getBytes());
			socket.getOutputStream().write(data);
			socket.getOutputStream().flush();
			socket.shutdownOutput();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RccSignalerEntity doConsume(RccSignalerEntity signaler) {
		Socket socket = null;
		try {
			socket = new Socket(signaler.getRcc().getHost(), signaler.getRcc().getPort());
			socket.setSoTimeout(signaler.getExpireTime());
			OutputStream outputStream = socket.getOutputStream();

			byte[] data = GZIPUtils.compress(RccKeepInstance.serialer.serialize(signaler));
			// 写入传输长度
			outputStream.write((data.length + "\r\n").getBytes());
			// 写入业务内容
			outputStream.write(data);
			outputStream.flush();
			socket.shutdownOutput();

			String line = ByteUtils.readLineString(socket.getInputStream(), "UTF-8");
			Integer length = Integer.valueOf(line.trim());
			data = ByteUtils.getBytes(socket.getInputStream(), length);

			signaler = RccKeepInstance.serialer.unSerialize(GZIPUtils.uncompress(data));
			return signaler;

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

	public static void main(String[] args) {
		byte a = '\n';
		System.out.println(a);
	}
}
