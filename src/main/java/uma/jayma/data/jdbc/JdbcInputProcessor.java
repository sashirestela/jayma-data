package uma.jayma.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JdbcInputProcessor {
	
	public void configInput(PreparedStatement pstm, Object[] params) throws SQLException;
}
