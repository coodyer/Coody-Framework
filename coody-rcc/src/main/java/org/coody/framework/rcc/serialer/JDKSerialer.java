package org.coody.framework.rcc.serialer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.rcc.exception.SerialerErrorException;
import org.coody.framework.rcc.serialer.iface.RccSerialer;

@AutoBuild
public class JDKSerialer implements RccSerialer {

	@Override
	public byte[] serialize(Object object) {
		ObjectOutputStream objectOutputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			throw new SerialerErrorException("序列化失败", e);
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unSerialize(byte[] data) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return (T) objectInputStream.readObject();
		} catch (Exception e) {
			throw new SerialerErrorException("反序列化失败", e);
		} finally {
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
