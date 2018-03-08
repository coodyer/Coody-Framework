package org.coody.web.service;

import java.util.List;

import org.coody.web.domain.UserInfo;

public interface UserService {

	/**
	 * 保存或更新用户信息
	 * @param user
	 */
	public void saveOrUpdateUser(UserInfo user);
	
	/**
	 * 查询用户列表
	 */
	public List<UserInfo> getUsers();
	
	/**
	 * 删除用户
	 * @param userId
	 */
	public void deleteUser(String userId);
	
	/**
	 * 查询用户信息
	 */
	public UserInfo getUserInfo(String userId);
}
