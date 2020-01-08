package org.coody.framework.elock.wrapper;

/**
 * 
 * @author Coody
 *
 */
public class ThreadWrapper {

	private Thread thread;
	
	private Long timeMillis;
	
	private Long expire;

	public ThreadWrapper(Thread thread,Long expire) {

		this.thread = thread;
		this.expire = expire;
		this.timeMillis=System.currentTimeMillis();
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}


	public Long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(Long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}
	
	public Boolean isExpire(){
		if (System.currentTimeMillis() - this.getTimeMillis() > this.getExpire()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "LockThreadWrapper [thread=" + thread + ", timeMillis=" + timeMillis + ", expire=" + expire + "]";
	}


}
