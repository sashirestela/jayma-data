package uma.jayma.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uma.jayma.data.classinfo.ClassInfoHolder;
import uma.jayma.data.jdbc.JdbcBase;
import uma.jayma.data.jdbc.JdbcInputOutputProcessor;
import uma.jayma.data.jdbc.JdbcInputProcessor;
import uma.jayma.data.sql.SqlBuilder;

public class ORMapper {

	@SuppressWarnings("unchecked")
	public <P> Long insertSingle(Connection conn, Class<P> clazz, P obj) {
		String sql = SqlBuilder.getIt().getInsertSingle(clazz);
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		Object[] params = {clazz, obj, holder.getNoIdFieldNames()};
		
		Long id = JdbcBase.getIt().executeInsert(conn, sql, params, new JdbcInputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Class<P> clazz = (Class<P>)params[0];
				P obj = (P)params[1];
				List<String> fieldNames = (List<String>)params[2];
				JdbcBase.getIt().setStatementFromObject(pstm, clazz, obj, fieldNames);
			}
		});
		return id;
	}
	
	@SuppressWarnings("unchecked")
	public void insertManyMany(Connection conn, String nameTable, List<String> fieldNames, List<Long> values) {
		String sql = SqlBuilder.getIt().getInsertManyMany(nameTable, fieldNames);
		Object[] params = {values};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
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
	
	@SuppressWarnings("unchecked")
	public void deleteManyMany(Connection conn, String nameTable, List<String> fieldNames, List<Long> values) {
		String sql = SqlBuilder.getIt().getDeleteManyMany(nameTable, fieldNames);
		Object[] params = {values};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				List<Long> values = (List<Long>)params[0];
				pstm.setObject(1, values.get(0));
				pstm.setObject(2, values.get(1));
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <P> void updateSingle(Connection conn, Class<P> clazz, P obj) {
		String sql = SqlBuilder.getIt().getInsertSingle(clazz);
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		List<String> fieldNames = holder.getNoIdFieldNames();
		fieldNames.add(holder.getIdName());
		Object[] params = {clazz, obj, fieldNames};
		
		JdbcBase.getIt().executeUpdate(conn, sql, params, new JdbcInputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] params) throws SQLException {
				Class<P> clazz = (Class<P>)params[0];
				P obj = (P)params[1];
				List<String> fieldNames = (List<String>)params[2];
				JdbcBase.getIt().setStatementFromObject(pstm, clazz, obj, fieldNames);
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
	
	@SuppressWarnings("unchecked")
	public <P> P selectSingle(Connection conn, Class<P> clazz, Long id) {
		String sql = SqlBuilder.getIt().getSelectSingle(clazz);
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		Object[] paramsIn = {id};
		Object[] paramsOut = {clazz, holder.getAllFieldNames()};
		
		P obj = (P)JdbcBase.getIt().executeSelect(conn, sql, paramsIn, paramsOut, new JdbcInputOutputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] paramsIn) throws SQLException {
				Long id = (Long)paramsIn[0];
				pstm.setObject(1, id);
			}
			
			public void extractOutput(ResultSet rset, Object obj, Object[] paramsOut) throws SQLException {
				Class<P> clazz = (Class<P>)paramsOut[0];
				List<String> fieldNames = (List<String>)paramsOut[1];
				try {
					obj = clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				JdbcBase.getIt().setObjectFromResultSet(clazz, (P)obj, fieldNames, rset);
			}
		});
		return obj;
	}

	public <P> Long selectOtherId(Connection conn, Class<P> clazz, String nameOtherId, Long id) {
		String sql = SqlBuilder.getIt().getSelectOtherId(clazz, nameOtherId);
		Object[] paramsIn = {id};
		Object[] paramsOut = {};
		
		Long otherId = (Long)JdbcBase.getIt().executeSelect(conn, sql, paramsIn, paramsOut, new JdbcInputOutputProcessor() {
			
			public void configInput(PreparedStatement pstm, Object[] paramsIn) throws SQLException {
				Long id = (Long)paramsIn[0];
				pstm.setObject(1, id);
			}
			
			public void extractOutput(ResultSet rset, Object obj, Object[] paramsOut) throws SQLException {
				obj = new Long(rset.getObject(1).toString());
			}
		});
		return otherId;
	}
}