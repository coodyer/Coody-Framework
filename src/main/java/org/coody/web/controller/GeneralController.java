package org.coody.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.box.annotation.JsonSerialize;
import org.coody.framework.box.annotation.OutBean;
import org.coody.framework.box.annotation.PathBinding;
import org.coody.web.comm.entity.MsgEntity;
import org.coody.web.domain.UserInfo;
import org.coody.web.service.UserService;

import com.alibaba.fastjson.JSON;
/**
 * 测试Controller
 * @author admin
 *
 */
@PathBinding("/")
public class GeneralController {

	@OutBean
	UserService userService;
	
	@PathBinding("/index.do")
	@JsonSerialize
	public Object index(HttpServletRequest request){
		List<UserInfo> users=userService.getUsers();
		return users;
	}
	
	@PathBinding("/test.do")
	@JsonSerialize
	public Object test(HttpServletRequest request,HttpServletResponse response){
		System.out.println(JSON.toJSONString(request.getParameterMap()));
		return new MsgEntity(0,"操作成功","这是test的内容");
	}
}
