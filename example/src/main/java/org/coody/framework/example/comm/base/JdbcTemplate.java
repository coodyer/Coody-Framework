package org.coody.framework.example.comm.base;

import java.beans.PropertyVetoException;
import java.io.IOException;

import org.coody.framework.core.annotation.InitBean;
import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.jdbc.JdbcHandle;

@InitBean
public class JdbcTemplate extends JdbcHandle implements InitBeanFace{


	@Override
	public void init() {
		try {
			initConfig("config/c3p0.properties");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

}
