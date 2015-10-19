package uma.jayma.data.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class Util {

	public static String listToStringWithSeparator(List<String> list, String separator) {
		String[] array = list.toArray(new String[list.size()]);
		String result = Arrays.toString(array).replace(", ", separator).replaceAll("[\\[\\]]", "");
		return result;
	}

	public static String upperFirst(String text) {
		String upper = text.substring(0, 1).toUpperCase() + text.substring(1);
		return upper;
	}

	public static String toString(Object obj) {
		String text = "[";
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (isScalarType(field)) {
				try {
					text += field.getName() + "=" + obj.getClass().getMethod("get" + upperFirst(field.getName())).invoke(obj) + "; ";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		text = text.substring(0, text.length()-2) + "]";
		return text;
	}
	
	private static boolean isScalarType(Field field) {
		boolean isScalar =
				Modifier.isProtected(field.getModifiers()) &&
				(field.getType().isPrimitive() ||
						(field.getType().getName().substring(0, 4).equals("java") &&
								!field.getType().isInterface()
						)
				);
		return isScalar;
	}

	public static String getValueToInsert(String className) {
		String result = Property.getIt().getSqlDbId(Const.DbId.VALUE_TO_INSERT, className);
		return result;
	}

	public static String getSelectLastInsert(String className) {
		String result = Property.getIt().getSqlDbId(Const.DbId.SQL_LAST_INSERT, className);
		return result;
	}
}
