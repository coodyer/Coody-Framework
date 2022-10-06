package org.coody.framework.core.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.util.log.LogUtil;

/**
 * 阻塞模式线程池
 * 
 * @author Coody
 *
 */
public class ThreadBlockPool {
	
	ExecutorService exePool;
	
	private List<Runnable> runnables = new ArrayList<Runnable>();
	private boolean isActivity = true;

	private Integer maxThread = 100;

	private Integer timeOutSeconds = 60;

	public Integer getMaxThread() {
		return maxThread;
	}

	public void setMaxThread(Integer maxThread) {
		this.maxThread = maxThread;
	}

	public List<Runnable> getRunnables() {
		return runnables;
	}

	public void setRunnables(List<Runnable> runnables) {
		this.runnables = runnables;
	}

	public ThreadBlockPool() {

	}

	public ThreadBlockPool(Integer maxThread, Integer timeOutSeconds) {
		this.maxThread = maxThread;
		this.timeOutSeconds = timeOutSeconds;
	}

	public ThreadBlockPool(List<Runnable> runnables) {
		this.runnables.addAll(runnables);
	}

	public void execute(List<Runnable> runnables) {
		pushTask(runnables);
		execute();
	}

	public void execute() {
		if (!isActivity) {
			LogUtil.log.error("ThreadBlockPool >>线程池已销毁");
			return;
		}
		isActivity = false;
		if (runnables == null || runnables.isEmpty()) {
			return;
		}
		Integer currThread = runnables.size();
		if (currThread > maxThread) {
			currThread = maxThread;
		}
		exePool = Executors.newFixedThreadPool(maxThread);
		LogUtil.log.debug("ThreadBlockPool >>[" + maxThread + "]执行中");
		for (Runnable runnable : runnables) {
			exePool.execute(runnable);
		}
		exePool.shutdown();
		try {
			exePool.awaitTermination(timeOutSeconds * 1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new CoodyException("任务执行出错");
		}
		LogUtil.log.debug("ThreadBlockPool:[" + maxThread + "]执行完毕");
	}

	public boolean pushTask(List<Runnable> runnables) {
		if (!isActivity) {
			LogUtil.log.error("ThreadBlockPool >>线程池已销毁");
			return false;
		}
		this.runnables.addAll(runnables);
		return isActivity;
	}

	public boolean pushTask(Runnable runnable) {
		if (!isActivity) {
			LogUtil.log.error("ThreadBlockPool >>线程池已销毁");
			return false;
		}
		runnables.add(runnable);
		return isActivity;
	}

}
