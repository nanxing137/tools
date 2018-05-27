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
	 * 利用序列化实现深复制</br>
	 * 要求传入参数必须实现{@code Cloneable,Serializable}</br>
	 * 可能抛出错为{@code CloneNotSupportedException}</br>
	 * 注意:可能比Object中的深复制慢
	 * 
	 * @param t
	 *            实现了{@code Cloneable,Serializable}的任意对象
	 * @return 深复制结果
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
