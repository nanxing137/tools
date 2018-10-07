package net.byteknight.objectCreator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * 使用反射创造对象</br>
 * 并且根据map中的键值对设置对象中的变量
 * @author Thornhill
 * @date 2018年10月7日
 */
public final class ObjectCreator {

	/**
	 * 通过反射构造类型为T的对象</br>
	 * 并调用与map中与键名相同的set方法设置其值
	 * 
	 * @param map 包含新创造的值的map
	 * @param clazz 新对象的Class
	 * @return
	 */
	public static <T> T getT(Map<String, ?> map, Class<T> clazz) {
		T newInstance = null;
		try {
			newInstance = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		Set<String> keySet = map.keySet();
		Method[] declaredMethods = clazz.getDeclaredMethods();
		Map<String, Method> methodMap = new HashMap<>();
		for (Method method : declaredMethods) {
			if (!method.getName().startsWith("set")) {
				continue;
			}
			String lowerCaseFirstOne = toLowerCaseFirstOne(method.getName().replace("set", ""));
			methodMap.put(lowerCaseFirstOne, method);
		}
		for (String string : keySet) {
			Method method = methodMap.get(string);
			if (method != null) {
				try {
					method.invoke(newInstance, map.get(string));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return newInstance;
	}

	private static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

}
