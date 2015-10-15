package uma.jayma.data.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ClassInfo {

	protected String className;
	protected String idName;
	protected Field idField;
	protected Map<String, Field> noIdField;
	protected Map<String, Field> allField;
	protected Map<String, Field> assocField;
	protected Map<String, Method> getMethod;
	protected Map<String, Method> setMethod;
	
	public ClassInfo() {}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public Field getIdField() {
		return idField;
	}

	public void setIdField(Field idField) {
		this.idField = idField;
	}

	public Map<String, Field> getNoIdField() {
		return noIdField;
	}

	public void setNoIdField(Map<String, Field> noIdField) {
		this.noIdField = noIdField;
	}

	public Map<String, Field> getAllField() {
		return allField;
	}

	public void setAllField(Map<String, Field> allField) {
		this.allField = allField;
	}

	public Map<String, Field> getAssocField() {
		return assocField;
	}

	public void setAssocField(Map<String, Field> assocField) {
		this.assocField = assocField;
	}

	public Map<String, Method> getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(Map<String, Method> getMethod) {
		this.getMethod = getMethod;
	}

	public Map<String, Method> getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(Map<String, Method> setMethod) {
		this.setMethod = setMethod;
	}
}
