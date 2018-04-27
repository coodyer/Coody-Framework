package snippet;

public class Snippet {
	@Override
	    public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
	        long time = unit.toMillis(waitTime);
	        // 申请锁，返回还剩余的锁过期时间
	        Long ttl = tryAcquire(leaseTime, unit);
	        // 如果为空，表示申请锁成功
	        if (ttl == null) {
	            return true;
	        }
	        // 订阅监听redis消息，并且创建RedissonLockEntry，其中RedissonLockEntry中比较关键的是一个 Semaphore属性对象用来控制本地的锁请求的信号量同步，返回的是netty框架的Future实现。
	        Future future = subscribe();
	        // 阻塞等待subscribe的future的结果对象，如果subscribe方法调用超过了time，说明已经超过了客户端设置的最大wait time，则直接返回false，取消订阅，不再继续申请锁了。
	        if (!future.await(time, TimeUnit.MILLISECONDS)) {
	            future.addListener(new FutureListener() {
	                @Override
	                public void operationComplete(Future future) throws Exception {
	                    if (future.isSuccess()) {  
	                        unsubscribe(future);
	                    }
	                }
	            });
	            return false;
	        }
	 
	        try {
	            while (true) {
	            // 再次尝试一次申请锁
	                ttl = tryAcquire(leaseTime, unit);
	                // 获得锁，返回
	                if (ttl == null) {
	                    return true;
	                }
	                // 不等待申请锁，返回
	                if (time 0) {
	                    return false;
	                }
	 
	                // 阻塞等待锁
	                long current = System.currentTimeMillis();
	                RedissonLockEntry entry = getEntry();
	 
	                if (ttl >= 0 & ttl // 通过信号量(共享锁)阻塞,等待解锁消息.
	                // 如果剩余时间(ttl)小于wait time ,就在 ttl 时间内，从Entry的信号量获取一个许可(除非被中断或者一直没有可用的许可)。
	                // 否则就在wait time 时间范围内等待可以通过信号量
	                    entry.getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
	                } else {
	                    entry.getLatch().tryAcquire(time, TimeUnit.MILLISECONDS);
	                }
	    // 更新等待时间(最大等待时间-已经消耗的阻塞时间)
	                long elapsed = System.currentTimeMillis() - current;
	                time -= elapsed;
	            }
	        } finally {
	           // 无论是否获得锁,都要取消订阅解锁消息
	            unsubscribe(future);
	        }
	 
	    }
}

