package net.byteknight.ormUtil.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.byteknight.ormUtil.annotation.Entity;

public final class DBUtils {
	private DBUtils() {
	}

	public static <T> int getRowCount(String url, String user, String password, Class<T> target) {
		try (Connection connection = DriverManager.getConnection(url, user, password)) {
			try (Statement statement = connection.createStatement()) {
				String sql = generateRowCountSQLStatement(target);
				try (ResultSet resultSet = statement.executeQuery(sql)) {
					while(resultSet.next()) {
						int result = resultSet.getInt(1);
						return result;
					}
					
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static <T> String getTableName(Class<T> target) {
		String tableName = target.getAnnotation(Entity.class).tableName();
		return tableName;
	}

	/**
	 * 通过类和域映射，生成sql语句</br>
	 * 这里需要通过sql语句对数据进行分片
	 * 
	 * @param target
	 * @param map
	 * @return
	 */
	public static <T> String generateRowCountSQLStatement(Class<T> target) {
		StringBuilder sql = new StringBuilder("select ");
		String tableName = getTableName(target);
		if (null == tableName || "".equals(tableName)) {
			throw new IllegalArgumentException("No @Entity annotation found");
		}
		sql.append("count(*) from ");
		sql.append(tableName);
		sql.append(';');
		return sql.toString();
	}
}
