package net.byteknight.ormUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.byteknight.ormUtil.annotation.Domain;
import net.byteknight.ormUtil.annotation.Entity;

public class DBGet implements DBGetManager{

	protected final String url;
	protected final String user;
	protected final String password;

	public DBGet(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	/**
	 * 输入目标类</br>
	 * 返回目标类列表
	 * 
	 * @param target
	 * @return
	 * @throws SQLException
	 */
	public <T> List<T> getResultList(Class<T> target) throws SQLException {
		Map<String, Field> objectMap = getObjectMap(target);
		List<T> list = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			// connection = DriverManager.getConnection(url, user, password);
			try (Statement statement = connection.createStatement()) {
				String sql = "";
				sql = generateSQLStatement(target, objectMap);
				try (ResultSet result = statement.executeQuery(sql)) {
					list = getList(target, result, objectMap);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return list;
	}

	/**
	 * 通过类和域映射，生成sql语句
	 * 
	 * @param target
	 * @param map
	 * @return
	 */
	protected <T> String generateSQLStatement(Class<T> target, Map<String, Field> map) {
		StringBuilder sql = new StringBuilder("select ");
		String tableName = target.getAnnotation(Entity.class).tableName();
		if (null == tableName || "".equals(tableName)) {
			throw new IllegalArgumentException("No @Entity annotation found");
		}
		map.forEach((k, v) -> {
			sql.append("," + k);
		});
		sql.append(" from " + tableName + ";");
		return sql.toString().replaceFirst(",", "");
	}

	/**
	 * 获取target中被{@Entity}标记的域</br>
	 * 并返回由<列名,域>组成的{@Map}
	 * 
	 * @param target
	 * @return
	 */
	protected <T> Map<String, Field> getObjectMap(Class<T> target) {
		Map<String, Field> orm = new HashMap<>();
		Entity entity = target.getAnnotation(Entity.class);
		if (null == entity) {
			throw new IllegalArgumentException("No @Entity annotation found");
		}
		Field[] declaredFields = target.getDeclaredFields();
		for (Field field : declaredFields) {
			Domain domain = field.getAnnotation(Domain.class);
			String columnName;
			if (null != domain) {
				columnName = domain.columnName();
				orm.put(columnName, field);
			}
		}
		if (orm.size() == 0) {
			throw new IllegalArgumentException("No @Domain annotation found");
		}
		return orm;
	}

	/**
	 * 通过目标类，结果集，对象域列表</br>
	 * 返回目标列表
	 * 
	 * @param target
	 * @param resultSet
	 * @param objectMap
	 * @return
	 */
	protected <T> List<T> getList(Class<T> target, ResultSet resultSet, Map<String, Field> objectMap) {
		List<T> result = new ArrayList<>();
		try {
			T newInstance;
			while (resultSet.next()) {
				newInstance = target.newInstance();
				String key;
				Field field;
				String simpleName;
				Class<?> fieldType;
				Method get;
				Object invoke;
				Set<Entry<String, Field>> entrySet = objectMap.entrySet();
				for (Entry<String, Field> entry : entrySet) {
					key = entry.getKey();
					field = entry.getValue();
					fieldType = field.getType();
					simpleName = initialCapital(fieldType.getSimpleName());
					get = resultSet.getClass().getMethod("get" + simpleName, String.class);
					invoke = get.invoke(resultSet, key);
					field.setAccessible(true);
					field.set(newInstance, invoke);
				}
				result.add(newInstance);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			// 为了让程序不因为一个值的错误死掉，自己内部处理错误
			// throw e;
		}
		return result;
	}

	protected String initialCapital(String originalName) {
		// originalName = originalName.substring(0, 1).toUpperCase() +
		// originalName.substring(1);
		// return originalName;
		char[] cs = originalName.toCharArray();
		if (cs[0] <= 122 && cs[0] >= 97) {
			cs[0] -= 32;
		}

		return String.valueOf(cs);
	}
}
