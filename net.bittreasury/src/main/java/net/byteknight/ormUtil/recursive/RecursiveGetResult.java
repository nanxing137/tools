package net.byteknight.ormUtil.recursive;

import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import net.byteknight.ormUtil.DBRecursiveGet;

public class RecursiveGetResult<T> extends RecursiveTask<Queue<T>> {
	private final String url;
	private final String user;
	private final String password;

	private final Class<T> target;

	// 拆分工作
	private final int start;
	private final int end;

	// 拆分粒度
	private static final int THREAD_HOLD = 10000;

	protected RecursiveGetResult(String url, String user, String password, int start, int end, Class<T> target) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.start = start;
		this.end = end;
		this.target = target;
	}

	@Override
	protected Queue<T> compute() {
		DBRecursiveGet dbGet = new DBRecursiveGet(url, user, password, start, end);
		if (end - start < THREAD_HOLD) {
			try {
				return dbGet.getRecursiveResultList(target, start, end);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		int mid = (start + end) / 2;
		RecursiveGetResult<T> left = new RecursiveGetResult<>(url, user, password, start, mid, target);
		RecursiveGetResult<T> right = new RecursiveGetResult<>(url, user, password, mid, end, target);
		ForkJoinTask<Queue<T>> leftTask = left.fork();
		ForkJoinTask<Queue<T>> rightTask = right.fork();
		Queue<T> leftResult = null;
		Queue<T> rightResult = null;
		try {
			leftResult = leftTask.get();

		} catch (InterruptedException | ExecutionException e) {
			// 即使出错也对结果初始化，防止造成之后的空指针异常
			leftResult = new ConcurrentLinkedQueue<>();
			e.printStackTrace();
		}
		try {
			rightResult = rightTask.get();
		} catch (InterruptedException | ExecutionException e) {
			rightResult = new ConcurrentLinkedQueue<>();
			e.printStackTrace();
		}
		leftResult.addAll(rightResult);
		return leftResult;
	}

}
