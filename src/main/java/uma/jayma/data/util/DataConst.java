package uma.jayma.data.util;

public class DataConst {
	
	public class Config {
		public static final String URL_DAO_PROPERTIES = "/dao.properties";
		
		public static final String DATABASE_LANGUAGE = "DataBase.Language";
		
		public static final String PROPERTY_GROUP_TYPE = "Type";
		public static final String PROPERTY_GROUP_DBID = "DbId";
	}

	public class Type {
		public static final String BOOLEAN = "BOOLEAN";
		public static final String BYTE = "BYTE";
		public static final String SHORT = "SHORT";
		public static final String INTEGER = "INTEGER";
		public static final String LONG = "LONG";
		public static final String FLOAT = "FLOAT";
		public static final String DOUBLE = "DOUBLE";
		public static final String BIGDECIMAL = "BIGDECIMAL";
		public static final String DATE = "DATE";
		public static final String DATETIME = "DATETIME";
		public static final String CHAR = "CHAR";
		public static final String STRING = "STRING";
		public static final String TEXT = "TEXT";
	}
	
	public class DbId {
		public static final String AUTO_INCREMENT = "AutoIncrement";
		public static final String SEQUENCE_DEF = "SequenceDef";
		public static final String VALUE_TO_INSERT = "ValueToInsert";
		public static final String SQL_LAST_INSERT = "SqlLastInsert";
	}
}
