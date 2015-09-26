package uma.jayma.data.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class DataProperty {
	
	private final static DataProperty singleton = new DataProperty();
	
	private Properties properties = null;
	
	private DataProperty() {
		loadProperties();
	}
	
	public static DataProperty getSingleton() {
		return singleton;
	}
	
	private void loadProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(DataConst.Config.URL_DAO_PROPERTIES));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getSqlType(String type) {
		String sqlType = properties.getProperty(properties.getProperty(DataConst.Config.DATABASE_LANGUAGE) + "."
				+ DataConst.Config.PROPERTY_GROUP_TYPE + "." + type);
		return sqlType;
	}
	
	public String getSqlDbId(String dbId, String className) {
		String sqlDbId = properties.getProperty(properties.getProperty(DataConst.Config.DATABASE_LANGUAGE) + "."
				+ DataConst.Config.PROPERTY_GROUP_DBID + "." + dbId);
		sqlDbId = MessageFormat.format(sqlDbId, className);
		return sqlDbId;
	}
}
