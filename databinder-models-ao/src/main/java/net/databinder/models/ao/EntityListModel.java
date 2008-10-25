package net.databinder.models.ao;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import net.databinder.ao.Databinder;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.LoadableDetachableModel;

@SuppressWarnings("unchecked")
public class EntityListModel<T extends RawEntity> extends LoadableDetachableModel<List<T>> {

	private Class<T> entityType;
	private Query query;
	private Object managerKey;
	
	public EntityListModel(Class<T> entityType) {
		this (entityType, Query.select());
	}
	public EntityListModel(Class<T> entityType, Query query) {
		this.entityType = entityType;
		this.query = query;
	}
	
	@Override
	protected List<T> load() {
		try {
			return (List<T>) Arrays.asList(Databinder.getEntityManager(managerKey).find(entityType, query));
		} catch (SQLException e) {
			throw new WicketRuntimeException("Error loading list", e);
		}
	}
	public Object getManagerKey() {
		return managerKey;
	}
	public void setManagerKey(Object managerKey) {
		this.managerKey = managerKey;
	}
}
