/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us

 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.databinder.models.jpa;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.databinder.jpa.Databinder;
import net.databinder.models.PropertyDataProvider;

/**
 * Provides query results to DataView and related components. Like the Hibernate model classes,
 * the results of this provider can be altered by query binders and criteria builders. By default
 * this provider wraps items in a compound property model in addition to a Hibernate model.
 * This is convenient for mapping DataView subcomponents as bean properties (as with
 * PropertyListView). However, <b>DataTable will not work with a compound property model.</b>
 * Call setWrapWithPropertyModel(false) when using with DataTable, DataGridView, or any
 * other time you do not want a compound property model.
 *
 * @author rhansen@kindleit.net
 *
 * @param <T> Type of the Model
 */
public abstract class JPAProvider<T> extends PropertyDataProvider<T> {

  private static final long serialVersionUID = 3420653591929473081L;

  private QueryBuilder queryBuilder, countQueryBuilder;

	private String factoryKey;

	/**
	 * Provides entities matching the given queries.
	 * @param query Query the provider will work with.
	 * @param countQuery Query that returns the element count in this provider
	 */
	public JPAProvider(final String query, final String countQuery) {
		this(new QueryBinderBuilder(query), new QueryBinderBuilder(countQuery));
	}

	/**
	 * Provides entities matching the given queries with bound parameters.
	 * @param query query to return entities
	 * @param queryBinder binder for the standard query
	 * @param countQuery query to return count of entities
	 * @param countQueryBinder binder for the count query (may be same as queryBinder)
	 */
	public JPAProvider(final String query, final QueryBinder queryBinder, final String countQuery, final QueryBinder countQueryBinder) {
		this(new QueryBinderBuilder(query, queryBinder), new QueryBinderBuilder(countQuery, countQueryBinder));
	}

	public JPAProvider(final QueryBuilder queryBuilder, final QueryBuilder countQueryBuilder) {
		this.queryBuilder = queryBuilder;
		this.countQueryBuilder = countQueryBuilder;
	}

	/** @return session factory key, or null for the default factory */
	public String getFactoryKey() {
		return factoryKey;
	}

	/**
	 * Set a factory key other than the default (null).
	 * @param key session factory key
	 * @return this, for chaining
	 */
	public JPAProvider<T> setFactoryKey(final String key) {
		this.factoryKey = key;
		return this;
	}

	/**
	 * It should not normally be necessary to override (or call) this default implementation.
	 * @param first First element to retrieve in the list
	 * @param count Number of elements to retrieve
	 * @return An iterator for the returned elements
	 */
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator(final int first, final int count) {
		final EntityManager em =  Databinder.getEntityManager(factoryKey);
		final Query q = queryBuilder.build(em);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList().iterator();
	}

	/**
	 * Only override this method if a single count query or
	 * criteria projection is not possible.
	 * @return The element count.
	 */
	public int size() {
		final EntityManager sess =  Databinder.getEntityManager(factoryKey);
		final Query q = countQueryBuilder.build(sess);
		final Object obj = q.getSingleResult();
		return ((Number) obj).intValue();
	}

	/** does nothing */
	@Override
  public void detach() {
	}
}
