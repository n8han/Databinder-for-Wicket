package net.databinder.models.ao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.util.string.Strings;

import net.databinder.ao.Databinder;
import net.databinder.models.BindingModel;
import net.databinder.models.LoadableWritableModel;
import net.java.ao.Common;
import net.java.ao.RawEntity;
import net.java.ao.schema.FieldNameConverter;

public class EntityModel<T extends RawEntity<K>, K extends Serializable> 
		extends LoadableWritableModel<T> implements BindingModel<T> {
	private K id;
	private Class<T> entityType;
	private Map<String, Object> propertyStore;
	private Object managerKey;
	
	public EntityModel(Class<T> entityType, K id) {
		this(entityType);
		this.id = id;
	}
	
	public EntityModel(Class<T> entityType) {
		this.entityType = entityType;
	}
	
	public EntityModel(T entity) {
		setObject(entity);
	}
	
	public boolean isBound() {
		return id != null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T load() {
		if (isBound())
			return Databinder.getEntityManager(managerKey).get(entityType, id);
		return (T) getPropertyStore(); // umm...
	}
	
	@SuppressWarnings("unchecked")
	public void setObject(T entity) {
		unbind();
		entityType = (Class<T>) entity.getEntityType();
		id = Common.getPrimaryKeyValue(entity);
		setTempModelObject(entity);
	}
	
	protected void putDefaultProperties(Map<String, Object> propertyStore) { }
	
	public void unbind() {
		id = null;
		propertyStore = null; 
		detach();
	}

	/**
	 * @return map of  properties to values for Wicket property models
	 */
	public Map<String, Object> getPropertyStore() {
		if (propertyStore == null) {
			propertyStore = new HashMap<String, Object>();
			putDefaultProperties(propertyStore);
		}
		return propertyStore;
	}

	/**
	 * @return map of database fields to their values for creating new entities
	 */
	public Map<String, Object> getFieldMap() {
		Map<String, Object> properties = getPropertyStore(), fields = new HashMap<String, Object>();
		FieldNameConverter conv = Databinder.getEntityManager(managerKey).getFieldNameConverter();
		for (Entry<String, Object> e : properties.entrySet()) {
			String field = e.getKey(), prop = Strings.capitalize(field);
			for (Method m : entityType.getMethods()) {
				// match getter or setter
				if (m.getName().substring(3).equals(prop)) {
					field = conv.getName(m);
					break;
				}
			}
			if (e.getValue() != null)
				fields.put(field, e.getValue());
		}
		return fields;
	}

	public Class<T> getEntityType() {
		return entityType;
	}

	public Object getManagerKey() {
		return managerKey;
	}

	public void setManagerKey(Object managerKey) {
		this.managerKey = managerKey;
	}
	
}
