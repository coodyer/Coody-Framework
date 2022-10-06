package org.coody.framework.logged.function;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.coody.framework.logged.entity.LoggedEntity;
import org.coody.framework.logged.function.iface.LoggedFunction;

public class TimeFunction extends LoggedFunction {

	@Override
	public String invoke(String parameter, LoggedEntity logged) {
		return toString(new Date(), parameter);
	}

	public static String toString(Date date, String format) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sfDate = new SimpleDateFormat(format);
		return sfDate.format(date);
	}

	@Override
	public String getName() {
		return "TIME";
	}

}
