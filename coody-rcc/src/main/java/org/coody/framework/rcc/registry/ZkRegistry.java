package org.coody.framework.rcc.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.random.RandomUtil;
import org.coody.framework.rcc.entity.RccInstance;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.registry.iface.RccRegistry;

/**
 * Zookeeper注册中心
 * 
 * @author Coody
 *
 */
public class ZkRegistry implements RccRegistry {

	private ZooKeeper zookeeper;

	public ZkRegistry setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
		return this;
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

}
