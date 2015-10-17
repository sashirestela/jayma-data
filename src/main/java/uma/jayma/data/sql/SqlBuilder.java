package uma.jayma.data.sql;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uma.jayma.data.info.InfoHolder;

import static uma.jayma.data.util.Util.*;

public class SqlBuilder {
	
	private final static SqlBuilder singleton = new SqlBuilder();

	private static Map<String, String> mapSql = new HashMap<>();
	
	private SqlBuilder() {}
	
	public static SqlBuilder getIt() {
		return singleton;
	}
	
	public String getInsertSingle(Class<?> clazz) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.INSERT_SINGLE;
		if (!mapSql.containsKey(key)) {
			String sql = "INSERT INTO {0} ({1}) VALUES ({2},{3})";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					stringFieldsToEnumerate(holder.getAllFieldNames()),
					getValueToInsert(holder.getClassName()),
					stringMarksToInsert(holder.getNoIdFieldNames()));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getInsertManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+SqlEnum.INSERT_MANY_MANY+"-"+fieldNames.get(0)+"-"+fieldNames.get(1);
		if (!mapSql.containsKey(key)) {
			String sql = "INSERT INTO {0} ({1}) VALUES ({2})";
			sql = MessageFormat.format(sql,
					nameTable,
					stringFieldsToEnumerate(fieldNames),
					stringMarksToInsert(fieldNames));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getDeleteSingle(Class<?> clazz) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.DELETE_SINGLE;
		if (!mapSql.containsKey(key)) {
			String sql = "DELETE FROM {0} WHERE {1}=?";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getDeleteManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+SqlEnum.DELETE_MANY_MANY+"-"+fieldNames.get(0)+"-"+fieldNames.get(1);
		if (!mapSql.containsKey(key)) {
			String sql = "DELETE FROM {0} WHERE {1}=? AND {2}=?";
			sql = MessageFormat.format(sql,
					nameTable,
					fieldNames.get(0),
					fieldNames.get(1));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getUpdateSingle(Class<?> clazz) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.UPDATE_SINGLE;
		if (!mapSql.containsKey(key)) {
			String sql = "UPDATE {0} SET {1} WHERE {2}=?";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					stringFieldsToUpdate(holder.getNoIdFieldNames()),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getUpdateOtherId(Class<?> clazz, String nameOtherId) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.UPDATE_OTHERID+"-"+nameOtherId;
		if (!mapSql.containsKey(key)) {
			String sql = "UPDATE {0} SET {1}=? WHERE {2}=?";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					nameOtherId,
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSelectSingle(Class<?> clazz) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_SINGLE;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE {2}=?";
			sql = MessageFormat.format(sql,
					stringFieldsToEnumerate(holder.getAllFieldNames()),
					holder.getClassName(),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSelectAll(Class<?> clazz) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_ALL;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1}";
			sql = MessageFormat.format(sql,
					stringFieldsToEnumerate(holder.getAllFieldNames()),
					holder.getClassName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSelectWhere(Class<?> clazz, String where) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_WHERE+"-"+where;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE {2}";
			sql = MessageFormat.format(sql,
					stringFieldsToEnumerate(holder.getAllFieldNames()),
					holder.getClassName(),
					where);
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSelectManyMany(Class<?> clazz, String nameTable, List<String> fieldNames) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_MANY_MANY+"-"+nameTable;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE EXISTS (SELECT 1 FROM {2} WHERE {3}={4} AND {5}=?)";
			sql = MessageFormat.format(sql,
					stringFieldsToEnumerate(holder.getAllFieldNames()),
					holder.getClassName(),
					nameTable,
					fieldNames.get(0),
					fieldNames.get(1),
					fieldNames.get(2));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSelectOtherId(Class<?> clazz, String nameOtherId) {
		InfoHolder holder = new InfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_OTHERID+"-"+nameOtherId;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE {2}=?";
			sql = MessageFormat.format(sql,
					nameOtherId,
					holder.getClassName(),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	protected String stringFieldsToEnumerate(List<String> list) {
		String result = listToStringWithSeparator(list, ",");
		return result;
	}

	protected String stringFieldsToUpdate(List<String> list) {
		String result = listToStringWithSeparator(list, "=?,") + "=?";
		return result;
	}

	protected String stringMarksToInsert(List<String> list) {
		String result = new String(new char[list.size()]).replace("\0", ",?");
		result = result.substring(1);
		return result;
	}
}