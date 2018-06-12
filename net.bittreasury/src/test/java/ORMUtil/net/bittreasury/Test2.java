package ORMUtil.net.bittreasury;

import java.lang.reflect.Method;

public class Test2 {

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Class class1 = E1.class;
		Method method = class1.getMethod("getString", String.class);
		System.out.println(method);
	}

}
