package org.coody.web.dao.impl;

import java.util.List;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.OutBean;
import org.coody.web.comm.base.JdbcTemplate;
import org.coody.web.dao.IcopDao;
import org.coody.web.domain.IcopTest;

@InitBean
public class IcopDaoImpl implements IcopDao{

	@OutBean
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
