package uma.jayma.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import uma.jayma.data.classinfo.ClassInfoHolder;
import uma.jayma.data.jdbc.JdbcBase;
import uma.jayma.data.jdbc.JdbcInputProcessor;
import uma.jayma.data.sql.SqlBuilder;

public class ORMapper {

	public <P> Long insertSingle(Connection conn, Class<P> clazz, P obj) {
		String sql = SqlBuilder.getIt().getInsertSingle(clazz);
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		Object[] params = {clazz, obj, holder.getNoIdFieldNames()};
		
		Long id = JdbcBase.getIt().executeInsert(conn, sql, params, new JdbcInputProcessor() {
			
			@SuppressWarnings("unchecked")
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Class<P> clazz = (Class<P>)params[0];
				P obj = (P)params[1];
				List<String> fieldNames = (List<String>)params[2];
				JdbcBase.getIt().configPreparedStatement(pstm, clazz, obj, fieldNames);
			}
		});
		return id;
	}
	
	public void insertManyMany(Connection conn, String nameTable, List<String> fieldNames, List<Long> values) {
		String sql = SqlBuilder.getIt().getInsertManyMany(nameTable, fieldNames);
		Object[] params = {values};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			@SuppressWarnings("unchecked")
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				List<Long> values = (List<Long>)params[0];
				pstm.setObject(1, values.get(0));
				pstm.setObject(2, values.get(1));
			}
		});
	}
	
	public <P> void deleteSingle(Connection conn, Class<P> clazz, Long id) {
		String sql = SqlBuilder.getIt().getDeleteSingle(clazz);
		Object[] params = {id};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Long id = (Long)params[0];
				pstm.setObject(1, id);
			}
		});
	}
	
	public void deleteManyMany(Connection conn, String nameTable, List<String> fieldNames, List<Long> values) {
		String sql = SqlBuilder.getIt().getDeleteManyMany(nameTable, fieldNames);
		Object[] params = {values};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			@SuppressWarnings("unchecked")
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				List<Long> values = (List<Long>)params[0];
				pstm.setObject(1, values.get(0));
				pstm.setObject(2, values.get(1));
			}
		});
	}

	public <P> void updateSingle(Connection conn, Class<P> clazz, P obj) {
		String sql = SqlBuilder.getIt().getInsertSingle(clazz);
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		List<String> fieldNames = holder.getNoIdFieldNames();
		fieldNames.add(holder.getIdName());
		Object[] params = {clazz, obj, fieldNames};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			@SuppressWarnings("unchecked")
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Class<P> clazz = (Class<P>)params[0];
				P obj = (P)params[1];
				List<String> fieldNames = (List<String>)params[2];
				JdbcBase.getIt().configPreparedStatement(pstm, clazz, obj, fieldNames);
			}
		});
	}
	
	public <P> void updateOtherId(Connection conn, Class<P> clazz, Long id, String nameOtherId, Long otherId) {
		String sql = SqlBuilder.getIt().getUpdateOtherId(clazz, nameOtherId);
		Object[] params = {otherId, id};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Long otherId = (Long)params[0];
				Long id = (Long)params[1];
				pstm.setObject(1, otherId);
				pstm.setObject(2, id);
			}
		});
	}
}