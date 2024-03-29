package org.coody.framework.logged.function;

import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class LevelFunction extends LoggedFunction {

	@Override
	public String invoke(String parameter, LoggedEntity logged) {

		return logged.getLevel();
	}

	@Override
	public String getName() {
		return "LEVEL";
	}

}
