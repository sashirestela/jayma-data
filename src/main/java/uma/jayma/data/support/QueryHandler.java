package uma.jayma.data.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uma.jayma.data.enums.Query;
import uma.jayma.data.util.Const;
import uma.jayma.data.util.Property;
import uma.jayma.data.util.Util;

public class QueryHandler {

	protected static Map<String, String> mapQuery = new HashMap<>();
	
	public QueryHandler() {}
	
	public String getInsert(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.INSERT_REGULAR;
		if (!mapQuery.containsKey(key)) {
			String query = "INSERT INTO {0} ({1}) VALUES ({2},{3})";
			query = MessageFormat.format(query,
					holder.getName(),
					generateFieldsOnly(holder.getAllFieldNames()),
					getValueToInsert(holder.getName()),
					generateValueMarks(holder.getNoIdFieldNames()));
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getInsertManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+Query.INSERT_MANY_MANY;
		if (!mapQuery.containsKey(key)) {
			String query = "INSERT INTO {0} ({1}) VALUES ({2})";
			query = MessageFormat.format(query,
					nameTable,
					generateFieldsOnly(fieldNames),
					generateValueMarks(fieldNames));
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getDelete(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.DELETE_REGULAR;
		if (!mapQuery.containsKey(key)) {
			String query = "DELETE FROM {0} WHERE {1}=?";
			query = MessageFormat.format(query,
					holder.getName(),
					holder.getId().getName());
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getDeleteManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+Query.DELETE_MANY_MANY;
		if (!mapQuery.containsKey(key)) {
			String query = "DELETE FROM {0} WHERE {1}=? AND {2}=?";
			query = MessageFormat.format(query,
					nameTable,
					fieldNames.get(0),
					fieldNames.get(1));
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getUpdate(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.UPDATE_REGULAR;
		if (!mapQuery.containsKey(key)) {
			String query = "UPDATE {0} SET {1} WHERE {2}=?";
			query = MessageFormat.format(query,
					holder.getName(),
					generateFieldsSetting(holder.getNoIdFieldNames()),
					holder.getId().getName());
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getUpdateOtherId(Class<?> clazz, String nameOtherId) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.UPDATE_OTHERID+"-"+nameOtherId;
		if (!mapQuery.containsKey(key)) {
			String query = "UPDATE {0} SET {1}=? WHERE {2}=?";
			query = MessageFormat.format(query,
					holder.getName(),
					nameOtherId,
					holder.getId().getName());
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getSelect(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.SELECT_REGULAR;
		if (!mapQuery.containsKey(key)) {
			String query = "SELECT {0} FROM {1} WHERE {2}=?";
			query = MessageFormat.format(query,
					generateFieldsOnly(holder.getAllFieldNames()),
					holder.getName(),
					holder.getId().getName());
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	public String getSelectOtherId(Class<?> clazz, String nameOtherId) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getName()+"-"+Query.SELECT_OTHERID;
		if (!mapQuery.containsKey(key)) {
			String query = "SELECT {0} FROM {1} WHERE {2}=?";
			query = MessageFormat.format(query,
					nameOtherId,
					holder.getName(),
					holder.getId().getName());
			mapQuery.put(key, query);
		}
		return mapQuery.get(key);
	}
	
	protected String generateFieldsOnly(List<String> list) {
		String generatedString = Util.listToStringWithSeparator(list, ",");
		return generatedString;
	}

	protected String generateFieldsSetting(List<String> list) {
		String generatedString = Util.listToStringWithSeparator(list, "=?,") + "=?";
		return generatedString;
	}

	protected String generateValueMarks(List<String> list) {
		String generatedString = new String(new char[list.size()]).replace("\0", ",?");
		generatedString = generatedString.substring(1);
		return generatedString;
	}

	protected <P> String getValueToInsert(String className) {
		String text = Property.getSingleton().getSqlDbId(Const.DbId.VALUE_TO_INSERT, className);
		return text;
	}

	protected <P> String getSelectLastInsert(String className) {
		String text = Property.getSingleton().getSqlDbId(Const.DbId.SQL_LAST_INSERT, className);
		return text;
	}
}