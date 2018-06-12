package ORMUtil.net.bittreasury;

import net.byteknight.ormUtil.annotation.Domain;
import net.byteknight.ormUtil.annotation.Entity;

@Entity(tableName="fts_resource")
public class E2 {
	@Domain(columnName="id")
	private int id;
	@Domain(columnName="name")
	private String name;
	@Domain(columnName="description")
	private String description;
	@Domain(columnName="resource_url")
	private String resourceUrl;
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("id="+id+";");
		stringBuilder.append("name="+name+";");
		stringBuilder.append("description="+description+";");
		stringBuilder.append("resourceUrl="+resourceUrl);
		return stringBuilder.toString();
	}
}
