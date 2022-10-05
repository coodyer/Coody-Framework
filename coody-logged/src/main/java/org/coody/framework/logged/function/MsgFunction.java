package org.coody.framework.logged.function;

import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class MsgFunction extends LoggedFunction {

	@Override
	public String invoke(String parameter, LoggedEntity logged) {

		return logged.getMsg();
	}

}
