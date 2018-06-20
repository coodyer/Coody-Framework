package org.coody.framework.example.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.util.JUUIDUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.example.dao.UserDao;
import org.coody.framework.example.domain.UserInfo;

@InitBean
public class UserDaoImpl implements UserDao,InitBeanFace{

	private static final Map<String, UserInfo> DATA_MAP=new ConcurrentHashMap<String, UserInfo>();
	
	/**
	 * 保存或更新用户信息
	 * @param user
	 */
	@Override
	public void saveOrUpdateUser(UserInfo user){
		if(StringUtil.isNullOrEmpty(user.getUserId())){
			user.setUserId(JUUIDUtil.createUuid());
			DATA_MAP.put(user.getUserId(), user);
			return;
		}
		DATA_MAP.put(user.getUserId(), user);
	}
	
	/**
	 * 查询用户列表
	 */
	@Override
	public List<UserInfo> getUsers(){
		Collection<UserInfo> users=DATA_MAP.values();
		return new ArrayList<UserInfo>(users);
	}
	/**
	 * 查询用户信息
	 */
	@Override
	public UserInfo getUserInfo(String userId){
		return DATA_MAP.get(userId);
	}
	/**
	 * 删除用户
	 * @param userId
	 */
	@Override
	public void deleteUser(String userId){
		DATA_MAP.remove(userId);
	}

	/**
	 * bean加载时执行
	 */
	@Override
	public void init() {
		UserInfo user=new UserInfo();
		user.setAge(18);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("张三");
		DATA_MAP.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(19);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("李四");
		DATA_MAP.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(20);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("王五");
		DATA_MAP.put(user.getUserId(), user);
		user=new UserInfo();
		user.setAge(21);
		user.setUserId(JUUIDUtil.createUuid());
		user.setUserName("马六");
		DATA_MAP.put(user.getUserId(), user);
	}
}
