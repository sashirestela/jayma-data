package uma.jayma.data.dao.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uma.jayma.data.annotation.Identifier;
import uma.jayma.data.annotation.Many_Many;
import uma.jayma.data.annotation.Many_One;
import uma.jayma.data.annotation.One_Many;
import uma.jayma.data.annotation.One_One;
import uma.jayma.data.dao.Dao;
import uma.jayma.data.util.Const;
import uma.jayma.data.util.Property;
import uma.jayma.data.util.Util;

public class DaoImpl<T> implements Dao<T> {

	protected Class<T> clazz = null;
	protected Connection conn = null;

	@SuppressWarnings("unchecked")
	public DaoImpl() {
		ParameterizedType pt = (ParameterizedType)getClass().getGenericSuperclass();
		this.clazz = (Class<T>)pt.getActualTypeArguments()[0];
	}

	@Override
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Long create(T obj) {
		Long result = createGeneric(this.clazz, obj);
		return result;
	}

	@Override
	public void update(T obj) {
		updateGeneric(this.clazz, obj);
	}

	@Override
	public void delete(Long id) {
		deleteGeneric(this.clazz, id);
	}

	@Override
	public T fetch(Long id) {
		T obj = selectGeneric(this.clazz, id);
		return obj;
	}

	@Override
	public List<T> fetchWhere(String where, Object... params) {
		List<T> list = selectGeneric(this.clazz, where, params);
		return list;
	}

	@Override
	public List<T> fetchAll() {
		List<T> list = selectAllGeneric(this.clazz);
		return list;
	}

	@Override
	public <P> void saveLink(T obj, String linkName, P otherObj) {
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Class<?> clazzOther = otherObj.getClass();

		if (field.isAnnotationPresent(One_Many.class)) {
			String nameOtherId = field.getAnnotation(One_Many.class).otherJoinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(clazzOther, idOther, nameOtherId, id);
		} else if (field.isAnnotationPresent(Many_One.class)) {
			String nameSelfId = field.getAnnotation(Many_One.class).selfJoinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(this.clazz, id, nameSelfId, idOther);
		} else if (field.isAnnotationPresent(One_One.class)) {
			String nameFieldId = field.getAnnotation(One_One.class).joinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(One_One.class).selfDriven()) {
				updateOtherId(this.clazz, id, nameFieldId, idOther);
			} else {
				updateOtherId(clazzOther, idOther, nameFieldId, id);
			}
		} else if (field.isAnnotationPresent(Many_Many.class)) {
			String nameEntity = field.getAnnotation(Many_Many.class).joinEntity();
			if (!field.getAnnotation(Many_Many.class).isClass()) {
				Long id = null;
				Long idOther = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
					gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
					idOther = (Long)gettingMettod.invoke(otherObj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<String> fieldNames = Arrays.asList(getIdFieldName(this.clazz)+this.clazz.getSimpleName(), getIdFieldName(clazzOther)+clazzOther.getSimpleName());
				List<Long> values = Arrays.asList(id, idOther);
				createManyMany(nameEntity, fieldNames, values);
			} else {
				// Is Class
			}
		}
	}

	@Override
	public <P> void deleteLink(T obj, String linkName, P otherObj) {
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (field.isAnnotationPresent(One_Many.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameOtherId = field.getAnnotation(One_Many.class).otherJoinColumn();
			Long idOther = null;
			try {
				gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(clazzOther, idOther, nameOtherId, null);
		} else if (field.isAnnotationPresent(Many_One.class)) {
			String nameSelfId = field.getAnnotation(Many_One.class).selfJoinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(this.clazz, id, nameSelfId, null);
		} else if (field.isAnnotationPresent(One_One.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameFieldId = field.getAnnotation(One_One.class).joinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(One_One.class).selfDriven()) {
				updateOtherId(this.clazz, id, nameFieldId, null);
			} else {
				updateOtherId(clazzOther, idOther, nameFieldId, null);
			}
		} else if (field.isAnnotationPresent(Many_Many.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameEntity = field.getAnnotation(Many_Many.class).joinEntity();
			if (!field.getAnnotation(Many_Many.class).isClass()) {
				Long id = null;
				Long idOther = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
					gettingMettod = clazzOther.getMethod("get"+Util.upperFirst(getIdFieldName(clazzOther)));
					idOther = (Long)gettingMettod.invoke(otherObj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				List<String> fieldNames = Arrays.asList(getIdFieldName(this.clazz)+this.clazz.getSimpleName(), getIdFieldName(clazzOther)+clazzOther.getSimpleName());
				List<Long> values = Arrays.asList(id, idOther);
				deleteManyMany(nameEntity, fieldNames, values);
			} else {
				// Is Association-Class
			}
		}
	}

	@Override
	public <P> P fetchLinkOne(T obj, String linkName, Class<P> clazzLink) {
		P objLink = null;
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Class<?> clazzLink = field.getType();

		if (field.isAnnotationPresent(Many_One.class)) {
			String nameSelfId = field.getAnnotation(Many_One.class).selfJoinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Long idOther = selectOtherId(this.clazz, nameSelfId, id);
			objLink = (P)selectGeneric(clazzLink, idOther);
		} else if (field.isAnnotationPresent(One_One.class)) {
			String nameFieldId = field.getAnnotation(One_One.class).joinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(One_One.class).selfDriven()) {
				Long idOther = selectOtherId(this.clazz, nameFieldId, id);
				objLink = (P)selectGeneric(clazzLink, idOther);
			} else {
				objLink = (P)(selectGeneric(clazzLink, nameFieldId+"=?", id).get(0));
			}
		}
		return objLink;
	}

	@Override
	public <P> List<P> fetchLinkMany(T obj, String linkName, Class<P> clazzLink) {
		List<P> lstLink = null;
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ParameterizedType pt = (ParameterizedType)field.getGenericType();
		//Class<?> clazzLink = (Class<?>)pt.getActualTypeArguments()[0];

		if (field.isAnnotationPresent(One_Many.class)) {
			String nameOtherId = field.getAnnotation(One_Many.class).otherJoinColumn();
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
				idOther = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lstLink = (List<P>)selectGeneric(clazzLink, nameOtherId+"=?", idOther);
		} else if (field.isAnnotationPresent(Many_Many.class)) {
			String nameEntity = field.getAnnotation(Many_Many.class).joinEntity();
			if (!field.getAnnotation(Many_Many.class).isClass()) {
				Long id = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+Util.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String where = "EXISTS (SELECT 1 FROM {0} WHERE {1}={2} AND {3}=?)";
				where = MessageFormat.format(where, nameEntity,
						clazzLink.getSimpleName()+"."+getIdFieldName(clazzLink),
						nameEntity+"."+getIdFieldName(clazzLink)+clazzLink.getSimpleName(),
						nameEntity+"."+getIdFieldName(this.clazz)+this.clazz.getSimpleName());
				lstLink = (List<P>)selectGeneric(clazzLink, where, id);
			} else {
				// Is Association-Class
			}
		}
		return lstLink;
	}

	protected <P> Long createGeneric(Class<P> clazz, P obj) {
		PreparedStatement pstm = null;
		Statement stm = null;
		ResultSet rset = null;
		Long lastId = null;
		try {
			String query = "INSERT INTO {0} ({1}) VALUES ({2},{3})";
			query = MessageFormat.format(query, clazz.getSimpleName(), generateFieldsOnly(getAllFieldNames(clazz)), getValueToInsert(clazz), generateValueMarks(getNotIdFieldNames(clazz)));
			pstm = conn.prepareStatement(query);
			configPreparedStatement(pstm, clazz, obj, getNotIdFieldNames(clazz));
			pstm.executeUpdate();

			query = getSelectLastInsert(clazz);
			stm = conn.createStatement();
			rset = stm.executeQuery(query);
			if (rset.next()) {
				lastId = new Long(rset.getObject(1).toString());

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
					rset = null;
				}
				if (stm != null) {
					stm.close();
					stm = null;
				}
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastId;
	}

	protected void createManyMany(String nameTable, List<String> fieldNames, List<Long> values) {
		PreparedStatement pstm = null;
		try {
			String query = "INSERT INTO {0} ({1}) VALUES ({2})";
			query = MessageFormat.format(query, nameTable, generateFieldsOnly(fieldNames), generateValueMarks(fieldNames));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, values.get(0));
			pstm.setObject(2, values.get(1));
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected <P> void updateGeneric(Class<P> clazz, P obj) {
		PreparedStatement pstm = null;
		try {
			String query = "UPDATE {0} SET {1} WHERE {2}=?";
			query = MessageFormat.format(query, clazz.getSimpleName(), generateFieldsSetting(getNotIdFieldNames(clazz)), getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			List<String> listFieldsForUpdate = getNotIdFieldNames(clazz);
			listFieldsForUpdate.add(getIdFieldName(clazz));
			configPreparedStatement(pstm, clazz, obj, listFieldsForUpdate);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected <P> void updateOtherId(Class<P> clazz, Long id, String nameOtherId, Long otherId) {
		PreparedStatement pstm = null;
		try {
			String query = "UPDATE {0} SET {1}=? WHERE {2}=?";
			query = MessageFormat.format(query, clazz.getSimpleName(), nameOtherId, getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, otherId);
			pstm.setObject(2, id);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected <P> void deleteGeneric(Class<P> clazz, Long id) {
		PreparedStatement pstm = null;
		try {
			String query = "DELETE FROM {0} WHERE {1}=?";
			query = MessageFormat.format(query, clazz.getSimpleName(), getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, id);
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected void deleteManyMany(String nameTable, List<String> fieldNames, List<Long> values) {
		PreparedStatement pstm = null;
		try {
			String query = "DELETE FROM {0} WHERE {1}=? AND {2}=?";
			query = MessageFormat.format(query, nameTable, fieldNames.get(0), fieldNames.get(1));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, values.get(0));
			pstm.setObject(2, values.get(1));
			pstm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	protected <P> P selectGeneric(Class<P> clazz, Long id) {
		PreparedStatement pstm = null;
		ResultSet rset = null;
		P obj = null;
		try {
			String query = "SELECT {0} FROM {1} WHERE {2}=?";
			query = MessageFormat.format(query, generateFieldsOnly(getAllFieldNames(clazz)), clazz.getSimpleName(), getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, id);
			rset = pstm.executeQuery();
			while (rset.next()) {
				try {
					obj = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				configResultSet(rset, clazz, obj, getAllFieldNames(clazz));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
					rset = null;
				}
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	protected <P> List<P> selectGeneric(Class<P> clazz, String where, Object... params) {
		PreparedStatement pstm = null;
		ResultSet rset = null;
		List<P> list = new ArrayList<P>();
		try {
			String query = "SELECT {0} FROM {1} WHERE {2}";
			query = MessageFormat.format(query, generateFieldsOnly(getAllFieldNames(clazz)), clazz.getSimpleName(), where);
			pstm = conn.prepareStatement(query);
			int i = 0;
			for (Object param : params) {
				pstm.setObject(++i, param);
			}
			rset = pstm.executeQuery();
			P obj = null;
			while (rset.next()) {
				try {
					obj = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				configResultSet(rset, clazz, obj, getAllFieldNames(clazz));
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
					rset = null;
				}
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (list.size() == 0) {
			list = null;
		}
		return list;
	}

	protected <P> List<P> selectAllGeneric(Class<P> clazz) {
		PreparedStatement pstm = null;
		ResultSet rset = null;
		List<P> list = new ArrayList<P>();
		try {
			String query = "SELECT {0} FROM {1}";
			query = MessageFormat.format(query, generateFieldsOnly(getAllFieldNames(clazz)), clazz.getSimpleName());
			pstm = conn.prepareStatement(query);
			rset = pstm.executeQuery();
			P obj = null;
			while (rset.next()) {
				try {
					obj = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				configResultSet(rset, clazz, obj, getAllFieldNames(clazz));
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
					rset = null;
				}
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (list.size() == 0) {
			list = null;
		}
		return list;
	}

	protected <P> Long selectOtherId(Class<P> clazz, String nameOtherId, Long id) {
		PreparedStatement pstm = null;
		ResultSet rset = null;
		Long otherId = null;
		try {
			String query = "SELECT {0} FROM {1} WHERE {2}=?";
			query = MessageFormat.format(query, nameOtherId, clazz.getSimpleName(), getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, id);
			rset = pstm.executeQuery();
			while (rset.next()) {
				otherId = new Long(rset.getObject(1).toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
					rset = null;
				}
				if (pstm != null) {
					pstm.close();
					pstm = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return otherId;
	}

	protected <P> String getValueToInsert(Class<P> clazz) {
		String text = Property.getIt().getSqlDbId(Const.DbId.VALUE_TO_INSERT, clazz.getSimpleName());
		return text;
	}

	protected <P> String getSelectLastInsert(Class<P> clazz) {
		String text = Property.getIt().getSqlDbId(Const.DbId.SQL_LAST_INSERT, clazz.getSimpleName());
		return text;
	}

	protected <P> String getIdFieldName(Class<P> clazz) {
		String idFieldName = null;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Identifier.class)) {
				idFieldName = field.getName();
				break;
			}
		}
		return idFieldName;
	}

	protected <P> List<String> getNotIdFieldNames(Class<P> clazz) {
		List<String> listNotId = new ArrayList<String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Identifier.class) && !isAssociation(field)) {
				listNotId.add(field.getName());
			}
		}
		return listNotId;
	}

	protected <P> List<String> getAllFieldNames(Class<P> clazz) {
		List<String> list = new ArrayList<String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!isAssociation(field)) {
				list.add(field.getName());
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	protected boolean isAssociation(Field field) {
		Class<?>[] assocAnnotations = {
			One_Many.class,
			Many_One.class,
			One_One.class,
			Many_Many.class
		};
		boolean is = false;
		for (Class<?> annotation : assocAnnotations) {
			is = is || field.isAnnotationPresent((Class<? extends Annotation>)annotation);
		}
		return is;
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

	protected <P> void configPreparedStatement(PreparedStatement pstm, Class<P> clazz, P obj, List<String> listNames) throws SQLException {
		int i = 1;
		for (String fieldName : listNames) {
			Object fieldValue = null;
			try {
				fieldValue = clazz.getMethod("get"+Util.upperFirst(fieldName)).invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fieldValue != null) {
				pstm.setObject(i, fieldValue);
			} else {
				pstm.setNull(i, Types.NULL);
			}
			i++;
		}
	}

	protected <P> void configResultSet(ResultSet rset, Class<P> clazz, P obj, List<String> listNames) throws SQLException {
		int i = 1;
		for (String fieldName : listNames) {
			if (rset.getObject(i) != null) {
				try {
					Class<?> type = clazz.getDeclaredField(fieldName).getType();
					if (type.equals(Date.class) || type.equals(Timestamp.class)) {
						clazz.getMethod("set"+Util.upperFirst(fieldName), type).invoke(obj, rset.getObject(i));
					} else {
						Constructor<?> constructor = type.getConstructor(new Class[] {String.class});
						String value = rset.getObject(i).toString();
						if (type.equals(Boolean.class)) {
							clazz.getMethod("set"+Util.upperFirst(fieldName), type).invoke(obj, constructor.newInstance(value.equals("1")||value.toLowerCase().equals("true")));
						} else {
							clazz.getMethod("set"+Util.upperFirst(fieldName), type).invoke(obj, constructor.newInstance(value));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			i++;
		}
	}
}
