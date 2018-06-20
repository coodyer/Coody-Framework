package org.coody.framework.example.service;

import java.util.List;

import org.coody.framework.example.domain.IcopTest;

public interface IcopService {

	
	public IcopTest getIcop(Integer id);
	
	
	public List<IcopTest> getIcops();

	public Long delIcop(Integer id);
	
}
