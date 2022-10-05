package org.coody.framework.logged.function;

import java.util.Date;

import org.coody.framework.core.util.date.DateUtils;
import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class TimeFunction extends LoggedFunction {


	@Override
	public String invoke(String parameter, LoggedEntity logged) {
		return DateUtils.toString(new Date(), parameter);
	}

}
