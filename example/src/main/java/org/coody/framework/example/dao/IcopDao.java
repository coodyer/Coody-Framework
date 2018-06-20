package org.coody.framework.example.dao;

import java.util.List;

import org.coody.framework.example.domain.IcopTest;

public interface IcopDao {

	
	public IcopTest getIcop(Integer id);
	
	
	public List<IcopTest> getIcops();
	
	public Long delIcop(Integer id);
}
