package uma.jayma.data.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uma.jayma.data.annotation.Identifier;
import uma.jayma.data.annotation.Many_Many;
import uma.jayma.data.annotation.Many_One;
import uma.jayma.data.annotation.One_Many;
import uma.jayma.data.annotation.One_One;
import static uma.jayma.data.util.Util.*;

public class InfoHolder {

	private static Map<Class<?>, Info> mapClassInfo = new HashMap<>();
	
	private Class<?> clazz;
	
	public InfoHolder(Class<?> clazz) {
		this.clazz = clazz;
		if (!mapClassInfo.containsKey(clazz)) {
			Info classInfo = loadClassInfoByReflection();
			mapClassInfo.put(clazz, classInfo);
		}
	}
	
	public String getClassName() {
		String name = mapClassInfo.get(clazz).getClassName();
		return name;
	}
	
	public String getIdName() {
		String name = mapClassInfo.get(clazz).getIdName();
		return name;
	}
	
	public Field getIdField() {
		Field field = mapClassInfo.get(clazz).getIdField();
		return field;
	}
	
	public List<Field> getNoIdFields() {
		List<Field> listField = (List<Field>)mapClassInfo.get(clazz).getNoIdField().values();
		return listField;
	}
	
	public List<String> getNoIdFieldNames() {
		List<String> listName = new ArrayList<String>(mapClassInfo.get(clazz).getNoIdField().keySet());
		return listName;
	}
	
	public List<Field> getAllFields() {
		List<Field> listField = (List<Field>)mapClassInfo.get(clazz).getAllField().values();
		return listField;
	}
	
	public List<String> getAllFieldNames() {
		List<String> listName = new ArrayList<String>(mapClassInfo.get(clazz).getAllField().keySet());
		return listName;
	}
	
	public Field getField(String fieldName) {
		Field field = mapClassInfo.get(clazz).getAllField().get(fieldName);
		return field;
	}
	
	public Field getAssocField(String fieldName) {
		Field field = mapClassInfo.get(clazz).getAssocField().get(fieldName);
		return field;
	}
	
	public Method getMethod(AccessEnum access, String fieldName) {
		Method method = null;
		if (access == AccessEnum.GET) {
			method = mapClassInfo.get(clazz).getGetMethod().get(fieldName);
		} else if (access == AccessEnum.SET) {
			method = mapClassInfo.get(clazz).getSetMethod().get(fieldName);
		}
		return method;
	}
	
	protected Info loadClassInfoByReflection() {
		Info info = new Info();
		Field idField = null;
		Map<String, Field> noIdField = new LinkedHashMap<>();
		Map<String, Field> assocField = new LinkedHashMap<>();
		Map<String, Field> allField = new LinkedHashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			String fieldName = field.getName();
			if (field.isAnnotationPresent(Identifier.class)) {
				idField = field;
				allField.put(fieldName, field);
			} else {
				if (isAssociation(field)) {
					assocField.put(fieldName, field);
				} else {
					noIdField.put(fieldName, field);
					allField.put(fieldName, field);
				}
			}
		}
		Map<String, Method> getMethod = new LinkedHashMap<>();
		Map<String, Method> setMethod = new LinkedHashMap<>();
		for (Field field : allField.values()) {
			String fieldName = field.getName();
			try {
				getMethod.put(fieldName, clazz.getMethod(AccessEnum.GET + upperFirst(fieldName)));
				setMethod.put(fieldName, clazz.getMethod(AccessEnum.SET + upperFirst(fieldName), field.getType()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		info.setClassName(clazz.getSimpleName());
		info.setIdName(idField.getName());
		info.setIdField(idField);
		info.setNoIdField(noIdField);
		info.setAllField(allField);
		info.setAssocField(assocField);
		info.setGetMethod(getMethod);
		info.setSetMethod(setMethod);
		return info;
	}

	@SuppressWarnings("unchecked")
	protected boolean isAssociation(Field field) {
		Class<?>[] assocAnnotations = {
			One_Many.class,
			Many_One.class,
			One_One.class,
			Many_Many.class
		};
		boolean is = false;
		for (Class<?> annotation : assocAnnotations) {
			is = is || field.isAnnotationPresent((Class<? extends Annotation>)annotation);
		}
		return is;
	}
}