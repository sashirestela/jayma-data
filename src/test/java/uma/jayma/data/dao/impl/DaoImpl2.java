package uma.jayma.data.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.List;

import uma.jayma.data.dao.Dao;
import uma.jayma.data.orm.OrmAssoc;
import uma.jayma.data.orm.OrmCrud;

public class DaoImpl2<T> implements Dao<T> {
	
	protected Class<T> clazz = null;
	protected Connection conn = null;
	protected OrmCrud ormCrud = null;
	protected OrmAssoc ormAssoc = null;
	
	@SuppressWarnings("unchecked")
	public DaoImpl2() {
		ParameterizedType pt = (ParameterizedType)getClass().getGenericSuperclass();
		this.clazz = (Class<T>)pt.getActualTypeArguments()[0];
		ormCrud = new OrmCrud();
		ormAssoc = new OrmAssoc(ormCrud);
	}

	@Override
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Long create(T obj) {
		Long result = ormCrud.insertSingle(conn, clazz, obj);
		return result;
	}

	@Override
	public void update(T obj) {
		ormCrud.updateSingle(conn, clazz, obj);
	}

	@Override
	public void delete(Long id) {
		ormCrud.deleteSingle(conn, clazz, id);
	}

	@Override
	public T fetch(Long id) {
		T obj = ormCrud.selectSingle(conn, clazz, id);
		return obj;
	}

	@Override
	public List<T> fetchAll() {
		List<T> lst = ormCrud.selectAll(conn, clazz);
		return lst;
	}

	@Override
	public List<T> fetchWhere(String where, Object... params) {
		List<T> lst = ormCrud.selectWhere(conn, clazz, where, params);
		return lst;
	}

	@Override
	public <P> void saveLink(T obj, String linkName, P otherObj) {
		ormAssoc.insertAssoc(conn, clazz, obj, linkName, otherObj);
		
	}

	@Override
	public <P> void deleteLink(T obj, String linkName, P otherObj) {
		ormAssoc.deleteAssoc(conn, clazz, obj, linkName, otherObj);
	}

	@Override
	public <P> P fetchLinkOne(T obj, String linkName, Class<P> clazzLink) {
		P objLink = ormAssoc.selectAssocOne(conn, clazz, obj, linkName, clazzLink);
		return objLink;
	}

	@Override
	public <P> List<P> fetchLinkMany(T obj, String linkName, Class<P> clazzLink) {
		List<P> lstLink = ormAssoc.selectAssocMany(conn, clazz, obj, linkName, clazzLink);
		return lstLink;
	}

}
