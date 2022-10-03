package org.coody.framework.minicat.socket.iface;

import java.io.IOException;

public interface MiniCatService {

	public void openPort(Integer port) throws IOException;

	public void doService() throws IOException;

}
