package uma.jayma.data.jdbc;

import java.sql.PreparedStatement;
import java.util.List;

public interface StatementConfigurator {

	public void executeConfig(PreparedStatement pstm, List<Object> params);
	
}
