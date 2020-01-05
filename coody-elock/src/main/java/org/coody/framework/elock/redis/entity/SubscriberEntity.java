package org.coody.framework.elock.redis.entity;

import org.coody.framework.core.util.LogUtil;
import org.coody.framework.elock.pointer.ELockerPointer;

import redis.clients.jedis.JedisPubSub;

public class SubscriberEntity extends JedisPubSub {

	public SubscriberEntity() {
	}

	@Override
	public void onMessage(String channel, String message) { // 收到消息会调用
		LogUtil.log.debug(String.format("收到消息, channel %s, message %s", channel, message));
		ELockerPointer.next(message);
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) { // 订阅了频道会调用
		LogUtil.log.debug(String.format("订阅频道, channel %s, subscribedChannels %d", channel, subscribedChannels));
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) { // 取消订阅 会调用
		LogUtil.log.debug(String.format("取消订阅, channel %s, subscribedChannels %d", channel, subscribedChannels));

	}
}