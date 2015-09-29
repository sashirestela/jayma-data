package uma.jayma.data.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class DataUtil {

	public static String delimitListWithSeparator(List<String> list, String separator) {
		String[] array = list.toArray(new String[list.size()]);
		String listWithSeparator = Arrays.toString(array).replace(", ", separator).replaceAll("[\\[\\]]", "");
		return listWithSeparator;
	}

	public static String upperFirst(String text) {
		String upper = text.substring(0, 1).toUpperCase() + text.substring(1);
		return upper;
	}

	public static String toString(Object obj) {
		String text = "[";
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (Modifier.isProtected(field.getModifiers()) && !field.getType().equals(List.class)) {
				String fieldName = field.getName();
				String fieldNameUpperFirst = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					text += field.getName() + "=" + obj.getClass().getMethod("get" + fieldNameUpperFirst).invoke(obj) + "; ";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		text = text.substring(0, text.length()-2) + "]";
		return text;
	}
}
