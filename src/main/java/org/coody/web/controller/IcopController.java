package org.coody.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.coody.framework.core.util.StringUtil;
import org.coody.framework.mvc.annotation.JsonSerialize;
import org.coody.framework.mvc.annotation.PathBinding;
import org.coody.web.comm.entity.MsgEntity;
import org.coody.web.domain.IcopTest;
import org.coody.web.service.IcopService;

@PathBinding("/icop")
public class IcopController {

	
	@Resource
	IcopService icopService;
	
	@PathBinding("loadIcops.do")
	@JsonSerialize
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
	@JsonSerialize
	public Object delIcop(HttpServletRequest request){
		Integer id=StringUtil.toInteger(request.getParameter("id"));
		Long code=icopService.delIcop(id);
		if(code>0){
			return new MsgEntity(0,"操作成功");
		}
		return new MsgEntity(-1,"系统出错");
	}
}
