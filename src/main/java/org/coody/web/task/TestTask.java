package org.coody.web.task;

import org.coody.framework.annotation.CronTask;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.util.DateUtils;

@InitBean
public class TestTask	 {

	//@CronTask("0/5 * * * * ? ")
	public void test() {
		System.out.println("定时任务执行中:" + DateUtils.getDateTimeString());
	}

}
