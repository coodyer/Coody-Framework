package org.coody.web.task;

import org.coody.framework.box.annotation.CronTask;
import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.iface.InitFace;
import org.coody.framework.util.DateUtils;

@InitBean
public class TestTask implements InitFace {

	@CronTask("0/5 * * * * ? ")
	public void test() {
		System.out.println("定时任务执行中:" + DateUtils.getDateTimeString());
	}

	@Override
	public void init() {

		System.out.println("初始化方法执行中");
	}
}
