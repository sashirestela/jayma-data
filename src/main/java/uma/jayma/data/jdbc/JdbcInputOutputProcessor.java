package uma.jayma.data.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JdbcInputOutputProcessor extends JdbcInputProcessor {
	
	public Object extractOutput(ResultSet rset, Object[] paramsOut) throws SQLException;
}
