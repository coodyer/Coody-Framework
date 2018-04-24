package org.coody.framework.aspect;

import java.sql.Connection;
import java.util.List;

import org.coody.framework.annotation.Around;
import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.Transacted;
import org.coody.framework.container.TransactedThreadContainer;
import org.coody.framework.point.AspectPoint;
import org.coody.framework.util.StringUtil;

@InitBean
public class TransactedAspect {

	/**
	 * 事物控制
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass=Transacted.class)
	public Object transacted(AspectPoint wrapper) throws Throwable{
		if(TransactedThreadContainer.hasTransacted()){
			return wrapper.invoke();
		}
		try{
			TransactedThreadContainer.writeHasTransacted();
			Object result= wrapper.invoke();
			//提交事物
			List<Connection> connections=TransactedThreadContainer.getConnections();
			if(!StringUtil.isNullOrEmpty(connections)){
				for(Connection conn:connections){
					try{
						conn.commit();
					}catch (Exception e) {
					}
				}
			}
			return result;
		}finally {
			//关闭连接
			List<Connection> connections=TransactedThreadContainer.getConnections();
			TransactedThreadContainer.clear();
			if(!StringUtil.isNullOrEmpty(connections)){
				for(Connection conn:connections){
					try{
						conn.close();
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}
	}
}
