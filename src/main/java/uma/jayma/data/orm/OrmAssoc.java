package uma.jayma.data.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import uma.jayma.data.annot.ManyMany;
import uma.jayma.data.annot.ManyOne;
import uma.jayma.data.annot.OneMany;
import uma.jayma.data.annot.OneOne;
import uma.jayma.data.info.AccessEnum;
import uma.jayma.data.info.InfoHolder;

public class OrmAssoc {
	
	protected OrmCrud crud;
	
	public OrmAssoc(OrmCrud crud) {
		this.crud = crud;
	}
	
	public <P,Q> void insertAssoc(Connection conn, Class<P> clazz, P obj, String assocName, Q otherObj) {
		updateAssoc(conn, clazz, obj, assocName, otherObj, true);
	}
	
	public <P,Q> void deleteAssoc(Connection conn, Class<P> clazz, P obj, String assocName, Q otherObj) {
		updateAssoc(conn, clazz, obj, assocName, otherObj, false);
	}
	
	public <P,Q> Q selectAssocOne(Connection conn, Class<P> clazz, P obj, String assocName, Class<Q> clazzAssoc) {
		Q objAssoc = null;
		InfoHolder holder = new InfoHolder(clazz);
		Field field = holder.getAssocField(assocName);
		Annotation annotation = field.getAnnotations()[0];
		Long id = null;
		try {
			id = (Long)holder.getMethod(AccessEnum.GET, holder.getIdName()).invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ManyOne.class.equals(annotation.annotationType())) {
			String nameSelfId = ((ManyOne)annotation).selfJoinColumn();
			Long idOther = crud.selectOtherId(conn, clazz, nameSelfId, id);
			objAssoc = crud.selectSingle(conn, clazzAssoc, idOther);
		} else if (OneOne.class.equals(annotation.annotationType())) {
			String nameFieldId = ((OneOne)annotation).joinColumn();
			if (((OneOne)annotation).selfDriven()) {
				Long idOther = crud.selectOtherId(conn, clazz, nameFieldId, id);
				objAssoc = crud.selectSingle(conn, clazzAssoc, idOther);
			} else {
				objAssoc = (crud.selectWhere(conn, clazzAssoc, nameFieldId+"=?", id)).get(0);
			}
		}
		return objAssoc;
	}
	
	public <P,Q> List<Q> selectAssocMany(Connection conn, Class<P> clazz, P obj, String assocName, Class<Q> clazzAssoc) {
		List<Q> lstAssoc = null;
		InfoHolder holder = new InfoHolder(clazz);
		Field field = holder.getAssocField(assocName);
		Annotation annotation = field.getAnnotations()[0];
		Long id = null;
		try {
			id = (Long)holder.getMethod(AccessEnum.GET, holder.getIdName()).invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (OneMany.class.equals(annotation.annotationType())) {
			String nameOtherId = ((OneMany)annotation).otherJoinColumn();
			lstAssoc = crud.selectWhere(conn, clazzAssoc, nameOtherId+"=?", id);
		} else if (ManyMany.class.equals(annotation.annotationType())) {
			String nameEntity = ((ManyMany)annotation).joinEntity();
			if (!((ManyMany)annotation).isClass()) {
				InfoHolder holderAssoc = new InfoHolder(clazzAssoc);
				List<String> fieldNames = Arrays.asList(
						holderAssoc.getClassName()+"."+holderAssoc.getIdName(),
						nameEntity+"."+holderAssoc.getIdName()+holderAssoc.getClassName(),
						nameEntity+"."+holder.getIdName()+holder.getClassName());
				lstAssoc = crud.selectManyMany(conn, clazzAssoc, nameEntity, fieldNames, id);
			} else {
				// Association-Class
			}
		}
		return lstAssoc;
	}
	
	protected <P,Q> void updateAssoc(Connection conn, Class<P> clazz, P obj, String assocName, Q otherObj, boolean isInsert) {
		InfoHolder holder = new InfoHolder(clazz);
		Class<?> clazzOther = otherObj.getClass();
		InfoHolder holderOther = new InfoHolder(clazzOther);
		Field field = holder.getAssocField(assocName);
		Annotation annotation = field.getAnnotations()[0];
		Long id = null;
		Long idOther = null;
		try {
			id = (Long)holder.getMethod(AccessEnum.GET, holder.getIdName()).invoke(obj);
			idOther = (Long)holderOther.getMethod(AccessEnum.GET, holderOther.getIdName()).invoke(otherObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (OneMany.class.equals(annotation.annotationType())) {
			String nameOtherId = ((OneMany)annotation).otherJoinColumn();
			crud.updateOtherId(conn, clazzOther, idOther, nameOtherId, (isInsert?id:null));
		} else if (ManyOne.class.equals(annotation.annotationType())) {
			String nameSelfId = ((ManyOne)annotation).selfJoinColumn();
			crud.updateOtherId(conn, clazz, id, nameSelfId, (isInsert?idOther:null));
		} else if (OneOne.class.equals(annotation.annotationType())) {
			String nameFieldId = ((OneOne)annotation).joinColumn();
			if (((OneOne)annotation).selfDriven()) {
				crud.updateOtherId(conn, clazz, id, nameFieldId, (isInsert?idOther:null));
			} else {
				crud.updateOtherId(conn, clazzOther, idOther, nameFieldId, (isInsert?id:null));
			}
		} else if (ManyMany.class.equals(annotation.annotationType())) {
			String nameEntity = ((ManyMany)annotation).joinEntity();
			if (!((ManyMany)annotation).isClass()) {
				List<String> fieldNames = Arrays.asList(holder.getIdName()+holder.getClassName(), holderOther.getIdName()+holderOther.getClassName());
				List<Long> values = Arrays.asList(id, idOther);
				if (isInsert) {
					crud.insertManyMany(conn, nameEntity, fieldNames, values);
				} else {
					crud.deleteManyMany(conn, nameEntity, fieldNames, values);
				}
			} else {
				// Association-Class
			}
		}
	}
}
