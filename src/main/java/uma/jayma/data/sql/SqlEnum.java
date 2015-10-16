package uma.jayma.data.sql;

public enum SqlEnum {
	INSERT_SINGLE,
	INSERT_MANY_MANY,
	DELETE_SINGLE,
	DELETE_MANY_MANY,
	UPDATE_SINGLE,
	UPDATE_OTHERID,
	SELECT_SINGLE,
	SELECT_OTHERID,
	SELECT_MANY_MANY,
	SELECT_WHERE,
	SELECT_ALL;
	
	private final String text;
	
	private SqlEnum() {
		this.text = this.name();
	}
	
	public String toString() {
		return text;
	}
}