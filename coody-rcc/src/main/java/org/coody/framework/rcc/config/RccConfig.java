package org.coody.framework.rcc.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.coody.framework.core.model.BaseConfig;
import org.coody.framework.rcc.serialer.JDKSerialer;
import org.coody.framework.rcc.serialer.iface.RccSerialer;
import org.coody.framework.rcc.signal.TcpSignaler;
import org.coody.framework.rcc.signal.iface.RccSignaler;

public class RccConfig extends BaseConfig {

	public static String PREFIX = "coody.rcc";

	/**
	 * 服务者线程数
	 */
	public static int serverThread = 100;
	/**
	 * 消费者线程数
	 */
	public static int consumeThread = 100;

	
	/**
	 * 项目名
	 */
	public static String name;
	
	/**
	 * 本地host
	 */
	public static String host;
	/**
	 * 本服务端口
	 */
	public static Integer port;
	/**
	 * 本机权重
	 */
	public static Integer pr;
	/**
	 * 调用其他服务的超时时间
	 */
	public static Integer expire = 6000;

	/**
	 * 同步注册中心数据时间
	 */
	public static Integer keepTime = 10000;

	/**
	 * 注册中心KEY
	 */
	public static String registerKey = "coody:framework:rcc";
	/**
	 * 调用其他服务的重试次数
	 */
	public static Integer retry;
	/**
	 * 调用其他服务使用的序列化工具
	 */
	public static Class<? extends RccSerialer> serialer = JDKSerialer.class;

	/**
	 * 通信工具
	 */
	public static Class<? extends RccSignaler> signaler = TcpSignaler.class;

	static {
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
