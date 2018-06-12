package ORMUtil.net.bittreasury;

import java.sql.SQLException;
import java.util.List;

import net.byteknight.ormUtil.DBGet;
import net.byteknight.ormUtil.DBGetManager;
import net.byteknight.ormUtil.recursive.RecursiveExecutor;

public class Test1 {

	private static final String url = "jdbc:mysql://39.107.109.144:3306/fortyThieves?useSSL=false";
	private static final String user = "user";
	private static final String password = "123456";

	public static void main(String[] args) throws SQLException {
		DBGet dbGet = new DBGet(url, user, password);
//		List<E1> json = dbGet.getResultList(E1.class);
		DBGetManager dbGetManager = new RecursiveExecutor(url, user, password);
		List<E2> resultList = dbGetManager.getResultList(E2.class);
		System.out.println(resultList);
	}

}
