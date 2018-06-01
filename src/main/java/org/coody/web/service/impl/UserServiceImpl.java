package org.coody.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.coody.framework.cache.annotation.CacheWipe;
import org.coody.framework.cache.annotation.CacheWrite;
import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.annotation.LogHead;
import org.coody.web.comm.constant.CacheFinal;
import org.coody.web.dao.UserDao;
import org.coody.web.domain.UserInfo;
import org.coody.web.service.UserService;

@InitBean
public class UserServiceImpl implements UserService{

	@Resource
	UserDao userDao;
	
	
	/**
	 * 保存或更新用户信息
	 * @param user
	 */
	@Override
	@CacheWipe(key=CacheFinal.USER_INFO,fields="user.userId")
	@CacheWipe(key=CacheFinal.USER_LIST)
	public void saveOrUpdateUser(UserInfo user){
		userDao.saveOrUpdateUser(user);
	}
	
	/**
	 * 查询用户列表
	 */
	@Override
	@LogHead("获取用户信息")
	public List<UserInfo> getUsers(){
		return userDao.getUsers();
	}
	
	/**
	 * 删除用户
	 * @param userId
	 */
	@Override
	@CacheWipe(key=CacheFinal.USER_INFO,fields="user.userId")
	@CacheWipe(key=CacheFinal.USER_LIST)
	public void deleteUser(String userId){
		userDao.deleteUser(userId);
	}
	
	/**
	 * 查询用户信息
	 */
	@Override
	@CacheWrite(key=CacheFinal.USER_INFO,fields="userId")
	public UserInfo getUserInfo(String userId){
		return userDao.getUserInfo(userId);
	}
	
}
