package uma.jayma.data.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcSupport {

	public void executeUpdate(JdbcCommand command, Connection conn, String sql, Object... params) {
		PreparedStatement pstm = null;
		try {
			pstm = conn.prepareStatement(sql);
			command.configStatement(pstm, params);
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
}
