package uma.jayma.data.enums;

public enum Accessor {
	GET("get"),
	SET("set");
	
	private final String text;
	
	private Accessor(final String text) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
}