package org.coody.framework.minicat.container;

import java.util.Date;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.encrypt.EncryptUtil;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.constant.ServerInfo;
import org.coody.framework.minicat.http.MinicatSessionImpl;

/**
 * session容器
 * 
 * @author Coody
 *
 */
public class SessionContainer {

	private static final Map<String, MinicatSessionImpl> SYSTEM_SESSION_CONTAINER = new ConcurrentHashMap<String, MinicatSessionImpl>();

	public static final ScheduledThreadPoolExecutor TASK_POOL = new ScheduledThreadPoolExecutor(30,
			new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r);
				}
			});
	public static boolean containsSession(String sessionId) {
		return SYSTEM_SESSION_CONTAINER.containsKey(sessionId);
	}

	public static MinicatSessionImpl getSession(String sessionId) {
		if (CommonUtil.isNullOrEmpty(sessionId)) {
			return null;
		}
		MinicatSessionImpl session = SYSTEM_SESSION_CONTAINER.get(sessionId);
		if (session == null) {
			return session;
		}
		session.setActiveTime(new Date());
		TASK_POOL.schedule(new TimeoutTimerTask(sessionId), MiniCatConfig.sessionTimeout, TimeUnit.MILLISECONDS);
		return session;
	}

	public static MinicatSessionImpl setSession(String sessionId, MinicatSessionImpl session) {
		return SYSTEM_SESSION_CONTAINER.put(sessionId, session);
	}

	public static MinicatSessionImpl initSession(String sessionId) {
		if (SYSTEM_SESSION_CONTAINER.containsKey(sessionId)) {
			return SYSTEM_SESSION_CONTAINER.get(sessionId);
		}
		MinicatSessionImpl session = new MinicatSessionImpl();
		SYSTEM_SESSION_CONTAINER.put(sessionId, session);
		TASK_POOL.schedule(new TimeoutTimerTask(sessionId), MiniCatConfig.sessionTimeout, TimeUnit.MILLISECONDS);
		return session;
	}

	private static int sessionIndex = 0;

	public static String createSessionId() {
		Integer currentSessionIndex = 0;
		synchronized (SessionContainer.class) {
			sessionIndex++;
			currentSessionIndex = sessionIndex;
		}
		String key = ServerInfo.startTime + currentSessionIndex.toString();
		return "SESS"+EncryptUtil.md5(key).toUpperCase();
	}
	
	static class TimeoutTimerTask extends TimerTask {
		
		private String key;

		public TimeoutTimerTask(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			MinicatSessionImpl session = SYSTEM_SESSION_CONTAINER.get(key);
			if(System.currentTimeMillis()- session.getActiveTime().getTime()<MiniCatConfig.sessionTimeout) {
				return;
			}
			SYSTEM_SESSION_CONTAINER.remove(key);
		}
	}
}
