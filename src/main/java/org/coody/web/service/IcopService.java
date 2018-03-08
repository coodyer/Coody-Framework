package org.coody.web.service;

import java.util.List;

import org.coody.web.domain.IcopTest;

public interface IcopService {

	
	public IcopTest getIcop(Integer id);
	
	
	public List<IcopTest> getIcops();

	public Long delIcop(Integer id);
	
}
