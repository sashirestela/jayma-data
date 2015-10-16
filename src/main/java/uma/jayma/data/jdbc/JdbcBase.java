package uma.jayma.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import uma.jayma.data.sql.SqlBuilder;

public class JdbcBase {

	protected void executeUpdate(Connection conn, String sql, List<Object> params, StatementConfigurator command) {
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sql);
			command.executeConfig(pstm, params);
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
	
	public void updateRegular(Connection conn, Class<?> clazz, List<Object> params) {
		String sql = SqlBuilder.getIt().getUpdateSingle(clazz);
		executeUpdate(conn, sql, params, new StatementConfigurator() {
			public void executeConfig(PreparedStatement pstm, List<Object> params) {
				// TODO Auto-generated method stub
			}
		});
	}
}
