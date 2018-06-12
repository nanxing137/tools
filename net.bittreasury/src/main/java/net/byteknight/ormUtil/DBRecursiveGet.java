package net.byteknight.ormUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.byteknight.ormUtil.annotation.Entity;

public final class DBRecursiveGet extends DBGet {
	private final int start;
	private final int end;

	public DBRecursiveGet(String url, String user, String password, int start, int end) {
		super(url, user, password);
		this.start = start;
		this.end = end;
	}

	/**
	 * 用于fork-join框架下</br>
	 * 拆分任务分批读取数据使用
	 * 
	 * @param target
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public <T> Queue<T> getRecursiveResultList(Class<T> target, int start, int end) throws SQLException {
		Map<String, Field> objectMap = getObjectMap(target);
		// List<T> list = new ArrayList<>();

		Queue<T> queue = new ConcurrentLinkedQueue<T>();

		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			// connection = DriverManager.getConnection(url, user, password);
			try (Statement statement = connection.createStatement()) {
				String sql = "";
				sql = generateSQLStatement(target, objectMap);
				try (ResultSet result = statement.executeQuery(sql)) {
					queue = getQueue(target, result, objectMap);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return queue;
	}

	/**
	 * 通过类和域映射，生成sql语句</br>
	 * 这里需要通过sql语句对数据进行分页
	 * @param target
	 * @param map
	 * @return
	 */
	@Override
	protected <T> String generateSQLStatement(Class<T> target, Map<String, Field> map) {
		StringBuilder sql = new StringBuilder("select ");
		String tableName = target.getAnnotation(Entity.class).tableName();
		if (null == tableName || "".equals(tableName)) {
			throw new IllegalArgumentException("No @Entity annotation found");
		}
		map.forEach((k, v) -> {
			sql.append("," + k);
		});
		sql.append(" from " + tableName);
		sql.append(" limit "+start+","+end+";");
		return sql.toString().replaceFirst(",", "");
	}

	/**
	 * 通过目标类，结果集，对象域列表</br>
	 * 功能近似super.getList</br>
	 * 改变返回值为Queue类型 返回目标列表
	 * 
	 * @param target
	 * @param resultSet
	 * @param objectMap
	 * @return
	 */
	protected <T> Queue<T> getQueue(Class<T> target, ResultSet resultSet, Map<String, Field> objectMap) {
		// List<T> result = new ArrayList<>();
		Queue<T> result = new ConcurrentLinkedQueue<>();
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
}
