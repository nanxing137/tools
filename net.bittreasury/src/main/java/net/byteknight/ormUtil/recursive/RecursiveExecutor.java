package net.byteknight.ormUtil.recursive;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import net.byteknight.ormUtil.DBGetManager;
import net.byteknight.ormUtil.utils.DBUtils;

public class RecursiveExecutor implements DBGetManager {
	// 持有一个ForkJoinPool
	private ForkJoinPool pool;
	private String url;
	private String user;
	private String password;

	public <T> RecursiveExecutor(String url, String user, String password) {
		this.pool = new ForkJoinPool();
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public <T> List<T> getResultList(Class<T> target) throws SQLException {
		int start =0;
		int end = DBUtils.getRowCount(url, user, password, target);
		RecursiveGetResult<T> recursiveGetResult = new RecursiveGetResult<>(url, user, password, start, end, target);
		ForkJoinTask<Queue<T>> task = pool.submit(recursiveGetResult);
		Queue<T> result = null;
		try {
			result = task.get();
		} catch (InterruptedException | ExecutionException e) {
			// 增加鲁棒性
			result = new ConcurrentLinkedQueue<T>();
			e.printStackTrace();
		}
		return new ArrayList<T>(result);
	}

}
