package uma.jayma.data.dao;

import java.sql.Connection;
import java.util.List;

public interface Dao<T> {
	
	public void setConnection(Connection conn);
	

	public Long create(T obj);
	
	public void update(T obj);
	
	public void delete(Long id);
	
	public T fetch(Long id);
	
	public List<T> fetchAll();
	
	public List<T> fetchWhere(String where, Object... params);
	
	
	public <P> void saveLink(T obj, String linkName, P otherObj);
	
	public <P> void deleteLink(T obj, String linkName, P otherObj);
	
	public <P> P fetchLinkOne(T obj, String linkName);
	
	public <P> List<P> fetchLinkMany(T obj, String linkName);
	
}
