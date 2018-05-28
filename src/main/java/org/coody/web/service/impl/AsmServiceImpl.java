package org.coody.web.service.impl;

import java.util.List;

import org.coody.framework.annotation.InitBean;
import org.coody.web.domain.UserInfo;
import org.coody.web.service.UserService;
import org.objectweb.asm.TypeReference;

@InitBean
public class AsmServiceImpl implements UserService{

	@Override
	public void saveOrUpdateUser(UserInfo user) throws RuntimeException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<UserInfo> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserInfo getUserInfo(String userId) {
		// TODO Auto-generated method stub
		return null;
	}


}
