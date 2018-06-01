package org.coody.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.Transacted;
import org.coody.web.dao.IcopDao;
import org.coody.web.domain.IcopTest;
import org.coody.web.service.IcopService;

@InitBean
public class IcopServiceImpl implements IcopService{

	@Resource
	IcopDao icopDao;
	@Override
	public IcopTest getIcop(Integer id){
		return icopDao.getIcop(id);
	}
	
	@Override
	public List<IcopTest> getIcops(){
		return icopDao.getIcops()
				;
	}
	@Override
	@Transacted
	public Long delIcop(Integer id){
		Long code= icopDao.delIcop(id);
		Integer i=50/0;
		System.out.println(i);
		return code;
	}
	
}
