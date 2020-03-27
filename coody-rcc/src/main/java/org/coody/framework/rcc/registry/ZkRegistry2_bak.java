package org.coody.framework.rcc.registry;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.random.RandomUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.cson.Cson;
import org.coody.framework.rcc.entity.RccInstance;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.registry.iface.RccRegistry;

/**
 * Zookeeper注册中心
 * 
 * @author Coody
 *
 */
public class ZkRegistry2_bak implements RccRegistry, Watcher {

	private ZooKeeper zookeeper;

	private static CountDownLatch countDownLatch = new CountDownLatch(1);

	public ZooKeeper getZookeeper() {
		return zookeeper;
	}

	public void setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
	}

	@Override
	public List<RccInstance> getRccInstances(String methodKey) {
		try {
			List<String> contents = zookeeper.getChildren("/" + methodKey, false);
			List<RccInstance> rccs = new ArrayList<RccInstance>();
			for (String line : contents) {
				String[] attrs = line.trim().split(":");
				RccInstance rcc = new RccInstance();
				rcc.setHost(attrs[0]);
				rcc.setPort(Integer.valueOf(attrs[1]));
				rcc.setPr(Integer.valueOf(attrs[2]));
				rccs.add(rcc);
			}
			return rccs;
		} catch (Exception e) {
			throw new RccException("获取节点数据列表失败", e);
		}
	}

	@Override
	public List<String> getMethods(String host, Integer port) {
		try {
			return zookeeper.getChildren("/" + host + ":" + port, false);
		} catch (Exception e) {
			throw new RccException("获取节点数据列表失败", e);
		}
	}

	@Override
	public RccInstance getRccInstance(String methodKey) {
		List<RccInstance> rccs = getRccInstances(methodKey);
		if (CommonUtil.isNullOrEmpty(rccs)) {
			return null;
		}
		if (rccs.size() == 1) {
			return rccs.get(0);
		}
		Integer[] prs = new Integer[rccs.size()];
		for (int i = 0; i < rccs.size(); i++) {
			prs[i] = rccs.get(i).getPr();
			if (prs[i] == null || prs[i] < 1) {
				prs[i] = 1;
			}
		}
		return rccs.get(RandomUtil.randomByPr(prs));
	}

	@Override
	public boolean register(String methodKey, String host, Integer port, Integer pr) {
		try {
			String hostPath = "/" + host + ":" + port;
			if (zookeeper.exists(hostPath, false) == null) {
				System.out.println(
						zookeeper.create(hostPath, methodKey.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
				;
			}
			hostPath = "/" + host + ":" + port + "/" + methodKey;
			if (zookeeper.exists(hostPath, false) == null) {
				System.out.println(
						zookeeper.create(hostPath, methodKey.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
			}
			String methodPath = "/" + methodKey;
			if (zookeeper.exists(methodPath, false) == null) {
				System.out.println(zookeeper.create(methodPath, (host + ":" + port).getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT));
			}
			methodPath = "/" + methodKey + "/" + host + ":" + port + ":" + pr;
			if (zookeeper.exists(methodPath, false) == null) {
				System.out.println(zookeeper.create(methodPath, (host + ":" + port).getBytes(), Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT));
			}
			return true;
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
		ZooKeeper zk = new ZooKeeper("icoody.cn:52019", 5000, new ZkRegistry2_bak());
		waitUntilConnected(zk, countDownLatch);

		ZkRegistry2_bak zkRegistry = new ZkRegistry2_bak();
		zkRegistry.setZookeeper(zk);

		String host = "127.0.0.1";
		Integer port = 8088;

		Method method = ZkRegistry2_bak.class.getDeclaredMethod("clean", String.class);
		String methodKey = MethodSignUtil.getMethodUnionKey(method);

		System.out.println(zkRegistry.register(methodKey, host, port, 1));
		// 获取127.0.0.1:8088注册的所有方法
		System.out.println(Cson.toJson(zkRegistry.getMethods("127.0.0.1", 8088)));
		System.out.println(zkRegistry.getRccInstances(methodKey));
		System.out.println("OK");
	}

	public static void waitUntilConnected(ZooKeeper zooKeeper, CountDownLatch connectedLatch) {
		if (States.CONNECTING == zooKeeper.getState()) {
			try {
				connectedLatch.await();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public void clean(String node) throws IOException, KeeperException, InterruptedException {
		// 打印当前节点路径
		if (zookeeper.getChildren(node, false).size() == 0) {
			try {
				// 删除节点
				zookeeper.delete(node, -1);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 递归查找非空子节点
		List<String> list = zookeeper.getChildren(node, true);
		for (String child : list) {
			clean((node == "/" ? "" : node) + "/" + child);
		}
	}

	@Override
	public void process(WatchedEvent event) {
		// 获取事件的状态
		KeeperState keeperState = event.getState();
		EventType eventType = event.getType();
		// 如果是建立连接
		if (KeeperState.SyncConnected == keeperState) {
			if (EventType.None == eventType) {
				// 如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
				System.out.println("zk 建立连接");
				countDownLatch.countDown();
			}
		}
	}

}
