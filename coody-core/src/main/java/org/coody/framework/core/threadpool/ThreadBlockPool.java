package org.coody.framework.core.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.util.LogUtil;

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
			e.printStackTrace();
		}
		LogUtil.log.debug("ThreadBlockPool:[" + maxThread + "]执行完毕");
	}

	public boolean pushTask(List<Runnable> runnables) {
		if (!isActivity) {
			LogUtil.log.error("ThreadBlockPool >>线程池已销毁");
		}
		this.runnables.addAll(runnables);
		return isActivity;
	}

	public boolean pushTask(Runnable runnable) {
		if (!isActivity) {
			LogUtil.log.error("ThreadBlockPool >>线程池已销毁");
		}
		runnables.add(runnable);
		return isActivity;
	}

	public static void main(String[] args) {
	}
}
