package uma.jayma.data.support;

import java.sql.PreparedStatement;

public interface JdbcCommand {

	public int configStatement(PreparedStatement pstm, Object... params);
	
}
