package org.coody.web.dao;

import java.util.List;

import org.coody.web.domain.IcopTest;

public interface IcopDao {

	
	public IcopTest getIcop(Integer id);
	
	
	public List<IcopTest> getIcops();
	
	public Long delIcop(Integer id);
}
