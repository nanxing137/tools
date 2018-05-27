package net.byteknight.deepClone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import proxy.T;

public class DeepCloneUtil {

	private DeepCloneUtil() {
	}

	/**
	 * �������л�ʵ�����</br>
	 * Ҫ�����������ʵ��{@code Cloneable,Serializable}</br>
	 * �����׳���Ϊ{@code CloneNotSupportedException}</br>
	 * ע��:���ܱ�Object�е������
	 * 
	 * @param t
	 *            ʵ����{@code Cloneable,Serializable}���������
	 * @return ��ƽ��
	 * @throws CloneNotSupportedException
	 */
	public static <T extends Cloneable & Serializable> T deepClone(T t) throws CloneNotSupportedException {
		try {

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try (ObjectOutputStream out = new ObjectOutputStream(bout)) {
				out.writeObject(t);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
			try (InputStream bin = new ByteArrayInputStream(bout.toByteArray())) {
				ObjectInputStream in = new ObjectInputStream(bin);
				@SuppressWarnings("unchecked")
				T result = (T) in.readObject();
				return result;
			}
		} catch (IOException | ClassNotFoundException e) {
			CloneNotSupportedException e2 = new CloneNotSupportedException();
			e2.initCause(e);
			throw e2;
		}
	}
}
