package uma.jayma.data.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class Property {

	private final static Property singleton = new Property();

	private Properties properties = null;

	private Property() {
		loadProperties();
	}

	public static Property getIt() {
		return singleton;
	}

	protected void loadProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(Const.Config.URL_DAO_PROPERTIES));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getSqlType(String type) {
		String sqlType = properties.getProperty(properties.getProperty(Const.Config.DATABASE_LANGUAGE) + "."
				+ Const.Config.PROPERTY_GROUP_TYPE + "." + type);
		return sqlType;
	}

	public String getSqlDbId(String dbId, String className) {
		String sqlDbId = properties.getProperty(properties.getProperty(Const.Config.DATABASE_LANGUAGE) + "."
				+ Const.Config.PROPERTY_GROUP_DBID + "." + dbId);
		sqlDbId = MessageFormat.format(sqlDbId, className);
		return sqlDbId;
	}
}
