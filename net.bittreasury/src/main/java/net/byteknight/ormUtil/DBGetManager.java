package net.byteknight.ormUtil;

import java.sql.SQLException;
import java.util.List;

public interface DBGetManager {
	<T> List<T> getResultList(Class<T> target) throws SQLException;
}
