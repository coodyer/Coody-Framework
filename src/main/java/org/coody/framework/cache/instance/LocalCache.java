package org.coody.framework.cache.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.cache.instance.iface.IcopCacheFace;
import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.util.AntUtil;
import org.coody.framework.core.util.StringUtil;


/**
 * 内存缓存工具类
 * @author Coody
 *
 */

@InitBean
@SuppressWarnings("unchecked")
public class LocalCache implements IcopCacheFace{

	private static final Timer timer;
	private static final ConcurrentHashMap<String, Object> map;
	static Object mutex = new Object();
	static {
		timer = new Timer();
		map = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param time
	 *            有效时间
	 */
	public  void setCache(String key, Object value,
			Integer time) {
		map.put(key, new CacheWrapper(time,value));
		timer.schedule(new TimeoutTimerTask(key,this), time * 1000);
	}
	/**
	 * 获取缓存KEY列表
	 * @return
	 */
	public Set<String> getCacheKeys() {
		return map.keySet();
	}
	/**
	 * 模糊获取缓存KEY
	 * @param patton
	 * @return
	 */
	public List<String> getKeysFuzz(String patton){
		List<String> list=new ArrayList<String>();
		for (String key : map.keySet()) {
			if (AntUtil.isAntMatch(key, patton)) {
				list.add(key);
			}
		}
		if(StringUtil.isNullOrEmpty(list)){
			return null;
		}
		return list;
	}
	/**
	 * 增加缓存对象
	 * 
	 * @param key
	 * @param ce
	 * @param time
	 *            有效时间
	 */
	public  void setCache(String key, Object value) {
			map.put(key, new CacheWrapper(value));
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	public <T> T getCache(String key) {
		CacheWrapper wrapper=(CacheWrapper) map.get(key);
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
	public Boolean contains(String key) {
		return map.containsKey(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public void delCache(String key) {
		map.remove(key);
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public void delCacheFuzz(String key) {
		for (String tmpKey : map.keySet()) {
			if (tmpKey.contains(key)) {
				map.remove(tmpKey);
			}
		}
	}

	/**
	 * 获取缓存大小
	 * 
	 * @param key
	 */
	public int getCacheSize() {
		return map.size();
	}

	/**
	 * 清除全部缓存
	 */
	public void clearCache() {
		map.clear();
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
		private LocalCache cacheHandle;

		public TimeoutTimerTask(String key,LocalCache cacheHandle) {
			this.ceKey = key;
		}
		@Override
		public void run() {
			CacheWrapper cacheWrapper=(CacheWrapper) map.get(ceKey);
			if(cacheWrapper==null||cacheWrapper.getDate()==null){
				return;
			}
			if(new Date().getTime()<cacheWrapper.getDate().getTime()){
				return;
			}
			cacheHandle.delCache(ceKey);
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
