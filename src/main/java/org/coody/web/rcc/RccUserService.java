package org.coody.web.rcc;

import java.util.List;

import org.coody.framework.cache.annotation.CacheWipe;
import org.coody.framework.cache.annotation.CacheWrite;
import org.coody.framework.rcc.annotation.RccService;
import org.coody.web.comm.constant.CacheFinal;
import org.coody.web.domain.UserInfo;

/**
 * 用户Remote Call Command
 * 
 * @author Coody
 */

@RccService(value = "192.167.100.172:64435", annoExtends = true)
public interface RccUserService {

	/**
	 * 获取用户列表
	 * 
	 * @return
	 */
	@RccService
	public List<UserInfo> getUsers(Integer num);

	/**
	 * 保存用户信息
	 * 
	 * @return
	 */
	@RccService
	@CacheWipe(key = CacheFinal.USER_INFO, fields = "userId")
	public Integer saveUserInfo(UserInfo user);

	/**
	 * 查询用户信息
	 */
	@RccService
	@CacheWrite(key = CacheFinal.USER_INFO, fields = "userId")
	public Integer getUserInfo(Integer userId);
}
