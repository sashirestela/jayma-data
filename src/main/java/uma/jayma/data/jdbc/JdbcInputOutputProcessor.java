package uma.jayma.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JdbcInputOutputProcessor extends JdbcInputProcessor {
	
	public void extractOutput(ResultSet rset, Object obj, Object[] paramsOut) throws SQLException;
}
