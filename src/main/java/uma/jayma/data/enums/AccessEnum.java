package uma.jayma.data.enums;

public enum AccessEnum {
	GET("get"),
	SET("set");
	
	private final String text;
	
	private AccessEnum(final String text) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
}