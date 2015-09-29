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

import uma.jayma.data.dao.Dao;
import uma.jayma.data.dao.annotation.Identifier;
import uma.jayma.data.dao.annotation.ManyToMany;
import uma.jayma.data.dao.annotation.ManyToOne;
import uma.jayma.data.dao.annotation.OneToMany;
import uma.jayma.data.dao.annotation.OneToOne;
import uma.jayma.data.util.DataConst;
import uma.jayma.data.util.DataProperty;
import uma.jayma.data.util.DataUtil;

public class DaoImpl<T> implements Dao<T> {

	private static Class<?>[] assocAnnotations = {OneToMany.class, ManyToOne.class, OneToOne.class, ManyToMany.class};

	private Class<T> clazz = null;
	private Connection conn = null;

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

		if (field.isAnnotationPresent(OneToMany.class)) {
			String nameOtherId = field.getAnnotation(OneToMany.class).otherJoinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(clazzOther, idOther, nameOtherId, id);
		} else if (field.isAnnotationPresent(ManyToOne.class)) {
			String nameSelfId = field.getAnnotation(ManyToOne.class).selfJoinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(this.clazz, id, nameSelfId, idOther);
		} else if (field.isAnnotationPresent(OneToOne.class)) {
			String nameFieldId = field.getAnnotation(OneToOne.class).joinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(OneToOne.class).selfDriven()) {
				updateOtherId(this.clazz, id, nameFieldId, idOther);
			} else {
				updateOtherId(clazzOther, idOther, nameFieldId, id);
			}
		} else if (field.isAnnotationPresent(ManyToMany.class)) {
			String nameEntity = field.getAnnotation(ManyToMany.class).joinEntity();
			if (!field.getAnnotation(ManyToMany.class).isClass()) {
				Long id = null;
				Long idOther = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
					gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
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
		if (field.isAnnotationPresent(OneToMany.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameOtherId = field.getAnnotation(OneToMany.class).otherJoinColumn();
			Long idOther = null;
			try {
				gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(clazzOther, idOther, nameOtherId, null);
		} else if (field.isAnnotationPresent(ManyToOne.class)) {
			String nameSelfId = field.getAnnotation(ManyToOne.class).selfJoinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateOtherId(this.clazz, id, nameSelfId, null);
		} else if (field.isAnnotationPresent(OneToOne.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameFieldId = field.getAnnotation(OneToOne.class).joinColumn();
			Long id = null;
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
				gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
				idOther = (Long)gettingMettod.invoke(otherObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(OneToOne.class).selfDriven()) {
				updateOtherId(this.clazz, id, nameFieldId, null);
			} else {
				updateOtherId(clazzOther, idOther, nameFieldId, null);
			}
		} else if (field.isAnnotationPresent(ManyToMany.class)) {
			Class<?> clazzOther = otherObj.getClass();
			String nameEntity = field.getAnnotation(ManyToMany.class).joinEntity();
			if (!field.getAnnotation(ManyToMany.class).isClass()) {
				Long id = null;
				Long idOther = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
					gettingMettod = clazzOther.getMethod("get"+DataUtil.upperFirst(getIdFieldName(clazzOther)));
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

	@SuppressWarnings("unchecked")
	@Override
	public <P> P fetchLinkOne(T obj, String linkName) {
		P objLink = null;
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Class<?> clazzField = field.getType();

		if (field.isAnnotationPresent(ManyToOne.class)) {
			String nameSelfId = field.getAnnotation(ManyToOne.class).selfJoinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Long idOther = selectOtherId(this.clazz, nameSelfId, id);
			objLink = (P)selectGeneric(clazzField, idOther);
		} else if (field.isAnnotationPresent(OneToOne.class)) {
			String nameFieldId = field.getAnnotation(OneToOne.class).joinColumn();
			Long id = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				id = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (field.getAnnotation(OneToOne.class).selfDriven()) {
				Long idOther = selectOtherId(this.clazz, nameFieldId, id);
				objLink = (P)selectGeneric(clazzField, idOther);
			} else {
				objLink = (P)(selectGeneric(clazzField, nameFieldId+"=?", id).get(0));
			}
		}
		return objLink;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> List<P> fetchLinkMany(T obj, String linkName) {
		List<P> lstLink = null;
		Field field = null;
		Method gettingMettod = null;
		try {
			field = this.clazz.getDeclaredField(linkName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParameterizedType pt = (ParameterizedType)field.getGenericType();
		Class<?> clazzField = (Class<?>)pt.getActualTypeArguments()[0];

		if (field.isAnnotationPresent(OneToMany.class)) {
			String nameOtherId = field.getAnnotation(OneToMany.class).otherJoinColumn();
			Long idOther = null;
			try {
				gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
				idOther = (Long)gettingMettod.invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			lstLink = (List<P>)selectGeneric(clazzField, nameOtherId+"=?", idOther);
		} else if (field.isAnnotationPresent(ManyToMany.class)) {
			String nameEntity = field.getAnnotation(ManyToMany.class).joinEntity();
			if (!field.getAnnotation(ManyToMany.class).isClass()) {
				Long id = null;
				try {
					gettingMettod = this.clazz.getMethod("get"+DataUtil.upperFirst(getIdFieldName(this.clazz)));
					id = (Long)gettingMettod.invoke(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String where = "EXISTS (SELECT 1 FROM {0} WHERE {1}={2} AND {3}=?)";
				where = MessageFormat.format(where, nameEntity,
						clazzField.getSimpleName()+"."+getIdFieldName(clazzField),
						nameEntity+"."+getIdFieldName(clazzField)+clazzField.getSimpleName(),
						nameEntity+"."+getIdFieldName(this.clazz)+this.clazz.getSimpleName());
				lstLink = (List<P>)selectGeneric(clazzField, where, id);
			} else {
				// Is Association-Class
			}
		}
		return lstLink;
	}

	private <P> Long createGeneric(Class<P> clazz, P obj) {
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

	private void createManyMany(String nameTable, List<String> fieldNames, List<Long> values) {
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

	private <P> void updateGeneric(Class<P> clazz, P obj) {
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

	private <P> boolean updateOtherId(Class<P> clazz, Long id, String nameOtherId, Long otherId) {
		PreparedStatement pstm = null;
		boolean result = false;
		try {
			String query = "UPDATE {0} SET {1}=? WHERE {2}=?";
			query = MessageFormat.format(query, clazz.getSimpleName(), nameOtherId, getIdFieldName(clazz));
			pstm = conn.prepareStatement(query);
			pstm.setObject(1, otherId);
			pstm.setObject(2, id);
			pstm.executeUpdate();
			result = true;
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
		return result;
	}

	private <P> void deleteGeneric(Class<P> clazz, Long id) {
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

	private void deleteManyMany(String nameTable, List<String> fieldNames, List<Long> values) {
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

	private <P> P selectGeneric(Class<P> clazz, Long id) {
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

	private <P> List<P> selectGeneric(Class<P> clazz, String where, Object... params) {
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

	private <P> List<P> selectAllGeneric(Class<P> clazz) {
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

	private <P> Long selectOtherId(Class<P> clazz, String nameOtherId, Long id) {
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

	private <P> String getValueToInsert(Class<P> clazz) {
		String text = DataProperty.getSingleton().getSqlDbId(DataConst.DbId.VALUE_TO_INSERT, clazz.getSimpleName());
		return text;
	}

	private <P> String getSelectLastInsert(Class<P> clazz) {
		String text = DataProperty.getSingleton().getSqlDbId(DataConst.DbId.SQL_LAST_INSERT, clazz.getSimpleName());
		return text;
	}

	private <P> String getIdFieldName(Class<P> clazz) {
		String idFieldName = null;
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Identifier.class)) {
				idFieldName = field.getName();
				break;
			}
		}
		return idFieldName;
	}

	private <P> List<String> getNotIdFieldNames(Class<P> clazz) {
		List<String> listNotId = new ArrayList<String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Identifier.class) && !isAssociationField(field)) {
				listNotId.add(field.getName());
			}
		}
		return listNotId;
	}

	private <P> List<String> getAllFieldNames(Class<P> clazz) {
		List<String> list = new ArrayList<String>();
		for (Field field : clazz.getDeclaredFields()) {
			if (!isAssociationField(field)) {
				list.add(field.getName());
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private boolean isAssociationField(Field field) {
		boolean is = false;
		for (Class<?> cls : assocAnnotations) {
			is = is || field.isAnnotationPresent((Class<? extends Annotation>)cls);
		}
		return is;
	}

	private String generateFieldsOnly(List<String> list) {
		String generatedString = DataUtil.delimitListWithSeparator(list, ",");
		return generatedString;
	}

	private String generateFieldsSetting(List<String> list) {
		String generatedString = DataUtil.delimitListWithSeparator(list, "=?,") + "=?";
		return generatedString;
	}

	private String generateValueMarks(List<String> list) {
		String generatedString = new String(new char[list.size()]).replace("\0", ",?");
		generatedString = generatedString.substring(1);
		return generatedString;
	}

	private <P> void configPreparedStatement(PreparedStatement pstm, Class<P> clazz, P obj, List<String> listNames) throws SQLException {
		int i = 1;
		for (String fieldName : listNames) {
			Object fieldValue = null;
			try {
				fieldValue = clazz.getMethod("get"+DataUtil.upperFirst(fieldName)).invoke(obj);
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

	private <P> void configResultSet(ResultSet rset, Class<P> clazz, P obj, List<String> listNames) throws SQLException {
		int i = 1;
		for (String fieldName : listNames) {
			if (rset.getObject(i) != null) {
				try {
					Object type = clazz.getDeclaredField(fieldName).getType();
					if (type instanceof Date || type instanceof Timestamp) {
						clazz.getMethod("set"+DataUtil.upperFirst(fieldName), (Class<?>)type).invoke(obj, rset.getObject(i));
					} else {
						Constructor<?> constructor = ((Class<?>)type).getConstructor(new Class[] {String.class});
						String value = rset.getObject(i).toString();
						if (type instanceof Boolean) {
							clazz.getMethod("set"+DataUtil.upperFirst(fieldName), (Class<?>)type).invoke(obj, constructor.newInstance(value.equals("1")||value.toLowerCase().equals("true")));
						} else {
							clazz.getMethod("set"+DataUtil.upperFirst(fieldName), (Class<?>)type).invoke(obj, constructor.newInstance(value));
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
