package org.coody.framework.elock.process;

import org.coody.framework.elock.LockHandle;

/**
 * 
 * @author Coody
 * @date 2018年10月31日
 */
public abstract class AbstractLockAble {

	private String lockName;
	
	/**
	 * 超时时间 ，默认15（秒）
	 */
	private Integer waitTime;
	
	
	public AbstractLockAble(String lockName){
		this.lockName=lockName;
		this.waitTime=15;
	}
	
	
	
	public AbstractLockAble(String lockName, Integer waitTime) {
		super();
		this.lockName = lockName;
		this.waitTime = waitTime;
	}



	/**
	 * 需要加锁执行的代码块
	 * @return
	 */
	public abstract Object doService();
	
	@SuppressWarnings({ "unchecked" })
	public <T> T invoke() throws InterruptedException{
		try {
			LockHandle.lock(lockName,waitTime);
			return (T) doService();
		} finally {
			LockHandle.unLock(lockName);
		}
	}
}
