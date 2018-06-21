package org.coody.framework.example.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.coody.framework.core.util.StringUtil;
import org.coody.framework.example.comm.entity.MsgEntity;
import org.coody.framework.example.domain.IcopTest;
import org.coody.framework.example.service.IcopService;
import org.coody.framework.web.annotation.JsonOut;
import org.coody.framework.web.annotation.PathBinding;

@PathBinding("/icop")
public class IcopController {

	
	@Resource
	IcopService icopService;
	
	@PathBinding("loadIcops.do")
	@JsonOut
	public Object loadIcops(){
		List<IcopTest> icops=icopService.getIcops();
		return icops;
	}
	/**
	 * 删除数据
	 * @param request
	 * @return
	 */
	@PathBinding("delIcop.do")
	@JsonOut
	public Object delIcop(HttpServletRequest request){
		Integer id=StringUtil.toInteger(request.getParameter("id"));
		Long code=icopService.delIcop(id);
		if(code>0){
			return new MsgEntity(0,"操作成功");
		}
		return new MsgEntity(-1,"系统出错");
	}
}
