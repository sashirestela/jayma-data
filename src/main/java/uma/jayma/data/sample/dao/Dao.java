package uma.jayma.data.sample.dao;

import java.sql.Connection;
import java.util.List;

public interface Dao<T> {

	public void setConnection(Connection conn);


	public Long create(T obj);

	public void update(T obj);

	public void delete(Long id);

	public T select(Long id);

	public List<T> selectAll();

	public List<T> selectWhere(String where, Object... params);


	public <P> void createAssoc(T obj, String assocName, P otherObj);

	public <P> void deleteAssoc(T obj, String assocName, P otherObj);

	public <P> P selectAssocOne(T obj, String assocName, Class<P> clazzLink);

	public <P> List<P> selectAssocMany(T obj, String assocName, Class<P> clazzLink);

}
