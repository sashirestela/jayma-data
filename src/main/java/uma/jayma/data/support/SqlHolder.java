package uma.jayma.data.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uma.jayma.data.enums.SqlEnum;
import uma.jayma.data.util.Const;
import uma.jayma.data.util.Property;
import uma.jayma.data.util.Util;

public class SqlHolder {

	protected static Map<String, String> mapSql = new HashMap<>();
	
	public SqlHolder() {}
	
	public String getSqlInsert(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.INSERT_REGULAR;
		if (!mapSql.containsKey(key)) {
			String sql = "INSERT INTO {0} ({1}) VALUES ({2},{3})";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					generateFieldsOnly(holder.getAllFieldNames()),
					getValueToInsert(holder.getClassName()),
					generateValueMarks(holder.getNoIdFieldNames()));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlInsertManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+SqlEnum.INSERT_MANY_MANY;
		if (!mapSql.containsKey(key)) {
			String sql = "INSERT INTO {0} ({1}) VALUES ({2})";
			sql = MessageFormat.format(sql,
					nameTable,
					generateFieldsOnly(fieldNames),
					generateValueMarks(fieldNames));
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlDelete(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.DELETE_REGULAR;
		if (!mapSql.containsKey(key)) {
			String sql = "DELETE FROM {0} WHERE {1}=?";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlDeleteManyMany(String nameTable, List<String> fieldNames) {
		String key = nameTable+"-"+SqlEnum.DELETE_MANY_MANY;
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
	
	public String getSqlUpdate(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.UPDATE_REGULAR;
		if (!mapSql.containsKey(key)) {
			String sql = "UPDATE {0} SET {1} WHERE {2}=?";
			sql = MessageFormat.format(sql,
					holder.getClassName(),
					generateFieldsSetting(holder.getNoIdFieldNames()),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlUpdateOtherId(Class<?> clazz, String nameOtherId) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
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
	
	public String getSqlSelect(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_REGULAR;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE {2}=?";
			sql = MessageFormat.format(sql,
					generateFieldsOnly(holder.getAllFieldNames()),
					holder.getClassName(),
					holder.getIdName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlSelectOtherId(Class<?> clazz, String nameOtherId) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
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
	
	public String getSqlSelectWhere(Class<?> clazz, String where) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_WHERE+"-"+where;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1} WHERE {2}";
			sql = MessageFormat.format(sql,
					generateFieldsOnly(holder.getAllFieldNames()),
					holder.getClassName(),
					where);
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
	}
	
	public String getSqlSelectAll(Class<?> clazz) {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		String key = holder.getClassName()+"-"+SqlEnum.SELECT_ALL;
		if (!mapSql.containsKey(key)) {
			String sql = "SELECT {0} FROM {1}";
			sql = MessageFormat.format(sql,
					generateFieldsOnly(holder.getAllFieldNames()),
					holder.getClassName());
			mapSql.put(key, sql);
		}
		return mapSql.get(key);
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