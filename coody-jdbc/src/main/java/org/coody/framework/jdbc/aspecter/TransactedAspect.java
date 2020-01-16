package org.coody.framework.jdbc.aspecter;

import java.sql.Connection;
import java.util.List;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.abnormal.PrintException;
import org.coody.framework.jdbc.annotation.Transacted;
import org.coody.framework.jdbc.container.TransactedThreadContainer;

@AutoBuild
public class TransactedAspect {

	/**
	 * 事物控制
	 * 
	 * @param wrapper
	 * @return
	 * @throws Throwable
	 */
	@Around(annotationClass = Transacted.class)
	public Object transacted(AspectPoint point) throws Throwable {
		if (TransactedThreadContainer.hasTransacted()) {
			return point.invoke();
		}
		try {
			TransactedThreadContainer.writeHasTransacted();
			Object result = point.invoke();
			// 提交事物
			List<Connection> connections = TransactedThreadContainer.getConnections();
			if (CommonUtil.isNullOrEmpty(connections)) {
				return result;
			}
			for (Connection conn : connections) {
				if (conn.isClosed()) {
					continue;
				}
				conn.commit();
			}
			return result;
		} catch (Exception e) {
			// 回滚事物
			List<Connection> connections = TransactedThreadContainer.getConnections();
			if (CommonUtil.isNullOrEmpty(connections)) {
				throw e;
			}
			for (Connection conn : connections) {
				try {
					conn.rollback();
				} catch (Exception ex) {
					PrintException.printException(e);
				}
			}
			throw e;
		} finally {
			// 关闭连接
			List<Connection> connections = TransactedThreadContainer.getConnections();
			TransactedThreadContainer.clear();
			if (CommonUtil.isNullOrEmpty(connections)) {
				for (Connection conn : connections) {
					try {
						conn.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
