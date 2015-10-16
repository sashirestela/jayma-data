package uma.jayma.data.jdbc;

import static uma.jayma.data.util.Util.getSelectLastInsert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import uma.jayma.data.classinfo.AccessEnum;
import uma.jayma.data.classinfo.ClassInfoHolder;

public class JdbcBase {
	
	private static final JdbcBase singleton = new JdbcBase();
	
	private JdbcBase() {}
	
	public static JdbcBase getIt() {
		return singleton;
	}

	public void executeUpdate(Connection conn, String sql, Object[] params, JdbcInputProcessor jdbcProcessor) {
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sql);
			jdbcProcessor.configInput(pstm, params);
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

	public Long executeInsert(Connection conn, String sql, Object[] params, JdbcInputProcessor jdbcProcessor) {
		executeUpdate(conn, sql, params, jdbcProcessor);
		
		Statement stm = null;
		ResultSet rset = null;
		String sqlId = null;
		Long lastId = null;
		try {
			sqlId = getSelectLastInsert(((Class<?>)params[0]).getSimpleName());
			stm = conn.createStatement();
			rset = stm.executeQuery(sqlId);
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastId;
	}
	
	public Object executeSelect(Connection conn, String sql, Object[] params, JdbcInputOutputProcessor jdbcProcessor) {
		PreparedStatement pstm = null;
		ResultSet rset = null;
		Object obj = null;
		try {
			pstm = conn.prepareStatement(sql);
			jdbcProcessor.configInput(pstm, params);
			rset = pstm.executeQuery();
			while (rset.next()) {
				jdbcProcessor.extractOutput(rset, obj);
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
	
	public <P> void configPreparedStatement(PreparedStatement pstm, Class<P> clazz, P obj, List<String> listNames) throws SQLException {
		ClassInfoHolder holder = new ClassInfoHolder(clazz);
		int i = 1;
		for (String fieldName : listNames) {
			Object fieldValue = null;
			try {
				fieldValue = holder.getMethod(AccessEnum.GET, fieldName).invoke(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fieldValue != null) {
				pstm.setObject(i, fieldValue);
			} else {
				pstm.setNull(i, Types.NULL);
			}
			i++;		}
	}
}
