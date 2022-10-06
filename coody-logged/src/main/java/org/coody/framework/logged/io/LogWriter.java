package org.coody.framework.logged.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.coody.framework.logged.config.LoggedConfig;
import org.coody.framework.logged.constant.LevelConstant;
import org.coody.framework.logged.entity.LoggedWriteEntity;
import org.coody.framework.logged.pool.LoggedThreadPool;

public class LogWriter {

	static Map<String, FileOutputStream> outputMap = new ConcurrentHashMap<String, FileOutputStream>();

	static LinkedBlockingQueue<LoggedWriteEntity> queue = new LinkedBlockingQueue<LoggedWriteEntity>();

	static {
		LoggedThreadPool.THREAD_POOL.execute(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						LoggedWriteEntity line = queue.take();
						write(line);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void offer(LoggedWriteEntity line) {
		queue.offer(line);
	}

	private static void write(LoggedWriteEntity line) throws IOException {
		FileOutputStream output = outputMap.get(line.getFile());
		if (output == null) {
			output = new FileOutputStream(line.getFile(), true);
			outputMap.put(line.getFile(), output);
		}
		FileChannel channel = output.getChannel();

		byte[] data = line.getMsg().getBytes(LoggedConfig.encode);
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();
		channel.write(buffer);
		output.flush();

		if (LoggedConfig.sysout) {
			if (line.getLevel().equals(LevelConstant.ERROR)) {
				System.err.print(line.getMsg());
				return;
			}
			System.out.print(line.getMsg());
		}
	}
}
