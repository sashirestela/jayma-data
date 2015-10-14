package uma.jayma.data.enums;

public enum Query {
	INSERT_REGULAR,
	INSERT_MANY_MANY,
	DELETE_REGULAR,
	DELETE_MANY_MANY,
	UPDATE_REGULAR,
	UPDATE_OTHERID,
	SELECT_REGULAR,
	SELECT_OTHERID,
	SELECT_WHERE,
	SELECT_ALL;
	
	private final String text;
	
	private Query() {
		this.text = this.name();
	}
	
	public String toString() {
		return text;
	}
}
