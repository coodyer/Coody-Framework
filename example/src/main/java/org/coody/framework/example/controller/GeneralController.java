package org.coody.framework.example.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.example.comm.entity.MsgEntity;
import org.coody.framework.example.domain.UserInfo;
import org.coody.framework.example.service.UserService;
import org.coody.framework.web.adapt.FormNomalAdapt;
import org.coody.framework.web.annotation.JsonSerialize;
import org.coody.framework.web.annotation.ParamsAdapt;
import org.coody.framework.web.annotation.PathBinding;
import org.nico.noson.Noson;
/**
 * 测试Controller
 * @author admin
 *
 */
@PathBinding("/")
public class GeneralController {

	@Resource
	UserService userService;
	@Resource
	HttpServletRequest request;
	@Resource
	HttpServletResponse response;
	
	@PathBinding("/index.do")	
	@JsonSerialize
	@ParamsAdapt(FormNomalAdapt.class)
	public Object index(){
		List<UserInfo> users=userService.getUsers();
		userService.deleteUser("bee723d8cb034040b81887709b12b662");
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
		System.out.println(Noson.reversal(request.getParameterMap()));
		return new MsgEntity(0,"操作成功","这是test的内容");
	}
	public static void main(String[] args) {
		Method[] methods=GeneralController.class.getDeclaredMethods();
		for(Method method:methods){
			System.out.println(Noson.reversal(PropertUtil.getMethodParaNames(method)));
		}
	}
	
}
