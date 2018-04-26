package org.coody.web.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.adapt.FormNomalAdapt;
import org.coody.framework.annotation.JsonSerialize;
import org.coody.framework.annotation.OutBean;
import org.coody.framework.annotation.ParamsAdapt;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.util.PropertUtil;
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
	@OutBean
	HttpServletRequest request;
	@OutBean
	HttpServletResponse response;
	
	@PathBinding("/index.do")
	@JsonSerialize
	@ParamsAdapt(FormNomalAdapt.class)
	public Object index(){
		List<UserInfo> users=userService.getUsers();
		userService.saveOrUpdateUser(users.get(0));
		return users;
	}
	
	
	@PathBinding("/hello.do")
	public void hello(){
		try {
			String name=request.getParameter("name");
			response.getWriter().write("hello:"+name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PathBinding("/test.do")
	@JsonSerialize
	public Object test(){
		System.out.println(JSON.toJSONString(request.getParameterMap()));
		return new MsgEntity(0,"操作成功","这是test的内容");
	}
	public static void main(String[] args) {
		Method[] methods=GeneralController.class.getDeclaredMethods();
		for(Method method:methods){
			System.out.println(JSON.toJSONString(PropertUtil.getMethodParaNames(method)));
		}
	}
	
}
