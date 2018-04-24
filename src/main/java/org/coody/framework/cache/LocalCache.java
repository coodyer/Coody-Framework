package org.coody.framework.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.container.SysTimerContainer;
import org.coody.framework.util.StringUtil;



/**
 * @className：CacheHandler
 * @description：缓存操作类，对缓存进行管理,清除方式采用Timer定时的方式
 * @creater：Coody
 * @creatTime：2014年5月7日 上午9:18:54
 * @remark：
 * @version
 */
@SuppressWarnings("unchecked")
public class LocalCache {
	
	private static final ConcurrentHashMap<String, Object> CACHE_MAP;
	
	static Object mutex = new Object();
	static {
		CACHE_MAP = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            有效时间
	 */
	public static  void setCache(String key, Object ce,
			int validityTime) {
		CACHE_MAP.put(key, new CacheWrapper(validityTime,ce));
		SysTimerContainer.execute(new TimeoutTimerTask(key), validityTime * 1000);
	}
	//获取缓存KEY列表
	public static Set<String> getCacheKeys() {
		return CACHE_MAP.keySet();
	}
	
	public static List<String> getKeysFuzz(String patton){
		List<String> list=new ArrayList<String>();
		for (String tmpKey : CACHE_MAP.keySet()) {
			if (tmpKey.contains(patton)) {
				list.add(tmpKey);
			}
		}
		if(StringUtil.isNullOrEmpty(list)){
			return null;
		}
		return list;
	}
	public static Integer getKeySizeFuzz(String patton){
		Integer num=0;
		for (String tmpKey : CACHE_MAP.keySet()) {
			if (tmpKey.startsWith(patton)) {
				num++;
			}
		}
		return num;
	}
	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param validityTime
	 *            有效时间
	 */
	public static  void setCache(String key, Object ce) {
		CACHE_MAP.put(key, new CacheWrapper(ce));
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	public static <T> T getCache(String key) {
		CacheWrapper wrapper=(CacheWrapper) CACHE_MAP.get(key);
		if(wrapper==null){
			return null;
		}
		return (T) wrapper.getValue();
	}

	/**
	 * 检查是否含有制定key的缓冲
	 * 
	 * @param key
	 * @return
	 */
	public static boolean contains(String key) {
		return CACHE_MAP.containsKey(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static void delCache(String key) {
		CACHE_MAP.remove(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static void delCacheFuzz(String key) {
		for (String tmpKey : CACHE_MAP.keySet()) {
			if (tmpKey.contains(key)) {
				CACHE_MAP.remove(tmpKey);
			}
		}
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param key
	 */
	public static int getCacheSize() {
		return CACHE_MAP.size();
	}

	/**
	 * 清除全部缓存
	 */
	public static void clearCache() {
		CACHE_MAP.clear();
	}

	/**
	 * @projName：lottery
	 * @className：TimeoutTimerTask
	 * @description：清除超时缓存定时服务类
	 * @creater：Coody
	 * @creatTime：2014年5月7日上午9:34:39
	 * @alter：Coody
	 * @alterTime：2014年5月7日 上午9:34:39
	 * @remark：
	 * @version
	 */
	static class TimeoutTimerTask extends TimerTask {
		private String ceKey;

		public TimeoutTimerTask(String key) {
			this.ceKey = key;
		}
		@Override
		public void run() {
			CacheWrapper cacheWrapper=(CacheWrapper) CACHE_MAP.get(ceKey);
			if(cacheWrapper==null||cacheWrapper.getDate()==null){
				return;
			}
			if(System.currentTimeMillis()<cacheWrapper.getDate().getTime()){
				return;
			}
			LocalCache.delCache(ceKey);
		}
	}

	private static class CacheWrapper{
		private Date date;
		private Object value;
		public CacheWrapper(int time,Object value){
			this.date=new Date(System.currentTimeMillis()+time*1000);
			this.value=value;
		}
		public CacheWrapper(Object value){
			this.value=value;
		}
		public Date getDate() {
			return date;
		}
		public Object getValue() {
			return value;
		}
	}
}
