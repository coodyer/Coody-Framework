package org.coody.framework.example.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.example.comm.base.JdbcTemplate;
import org.coody.framework.example.dao.IcopDao;
import org.coody.framework.example.domain.IcopTest;

@InitBean
public class IcopDaoImpl implements IcopDao{

	@Resource
	JdbcTemplate jdbcTemplate;
	
	@Override
	public IcopTest getIcop(Integer id){
		return jdbcTemplate.findBeanFirst(IcopTest.class,"id",id);
	}
	
	@Override
	public List<IcopTest> getIcops(){
		return jdbcTemplate.findBean(IcopTest.class);
	}
	@Override
	public Long delIcop(Integer id){
		
		String sql="delete from icop_test where id=? limit 1";
		return jdbcTemplate.doUpdate(sql,id);
	}
}
