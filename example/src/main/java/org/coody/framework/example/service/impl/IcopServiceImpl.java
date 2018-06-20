package org.coody.framework.example.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.example.dao.IcopDao;
import org.coody.framework.example.domain.IcopTest;
import org.coody.framework.example.service.IcopService;
import org.coody.framework.jdbc.annotation.Transacted;

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
