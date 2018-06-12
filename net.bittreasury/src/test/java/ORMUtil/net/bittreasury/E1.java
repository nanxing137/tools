package ORMUtil.net.bittreasury;

import java.sql.Date;

import net.byteknight.ormUtil.annotation.Domain;
import net.byteknight.ormUtil.annotation.Entity;

@Entity(tableName="demo")
public class E1 {
	
	@Domain(columnName="id")
	private int ID;
	
	@Domain(columnName="date")
	private Date date;
	
	@Domain(columnName="name")
	private String name;
	
	public String getString(String name) {
		return "dddd";
	}
	@Override
	public java.lang.String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID="+ID);
		stringBuilder.append("date="+date);
		stringBuilder.append("name="+name);
		return stringBuilder.toString();
	}
}
