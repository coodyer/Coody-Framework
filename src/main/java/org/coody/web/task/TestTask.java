package org.coody.web.task;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.util.DateUtils;
import org.coody.framework.task.annotation.CronTask;

@InitBean
public class TestTask	 {

	@CronTask("0/5 * * * * ? ")
	public void test() {
		System.out.println("定时任务执行中:" + DateUtils.getDateTimeString());
	}

}
