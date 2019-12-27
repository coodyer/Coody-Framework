package org.coody.framework.minicat.container;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.util.EncryptUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.minicat.config.MiniCatConfig;
import org.coody.framework.minicat.constant.ServerInfo;
import org.coody.framework.minicat.http.MinicatSessionImpl;
import org.coody.framework.minicat.threadpool.MiniCatThreadPool;

/**
 * session容器
 * 
 * @author Coody
 *
 */
public class SessionContainer {

	private static final Map<String, MinicatSessionImpl> SYSTEM_SESSION_CONTAINER = new ConcurrentHashMap<String, MinicatSessionImpl>();

	
	
	
	static{
		MiniCatThreadPool.MINICAT_POOL.execute(new Runnable() {
			public void run() {
				sessionGuard();
			}
		});
	}
	
	private static void sessionGuard(){
		while(true){
			try {
				if(StringUtil.isNullOrEmpty(SYSTEM_SESSION_CONTAINER)){
					return;
				}
				List<String> willCleanSessionIds=new ArrayList<String>();
				for(String key:SYSTEM_SESSION_CONTAINER.keySet()){
					MinicatSessionImpl session=SYSTEM_SESSION_CONTAINER.get(key);
					if(System.currentTimeMillis()-session.getActiveTime().getTime()>MiniCatConfig.sessionTimeout){
						willCleanSessionIds.add(key);
					}
				}
				for(String key:willCleanSessionIds){
					SYSTEM_SESSION_CONTAINER.remove(key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					TimeUnit.SECONDS.sleep(1l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean containsSession(String sessionId) {
		return SYSTEM_SESSION_CONTAINER.containsKey(sessionId);
	}

	public static MinicatSessionImpl getSession(String sessionId) {
		if(StringUtil.isNullOrEmpty(sessionId)){
			return null;
		}
		MinicatSessionImpl session=SYSTEM_SESSION_CONTAINER.get(sessionId);
		if(session==null){
			return session;
		}
		session.setActiveTime(new Date());
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
		return EncryptUtil.md5Code(key);
	}
}
