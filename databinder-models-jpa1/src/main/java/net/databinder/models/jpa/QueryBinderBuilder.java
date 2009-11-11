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

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Converts a string and QueryBinder(s) into a builder so only builders need to
 * be dealt with.
 */
public class QueryBinderBuilder implements QueryBuilder {

  private static final long serialVersionUID = 8195146425011425221L;

  private final String query;
	private final QueryBinder[] binders;
	public QueryBinderBuilder(final String query, final QueryBinder... binders) {
		this.query = query;
		this.binders = binders;
	}
	public Query build(final EntityManager em) {
		final Query q = em.createQuery(query);
		for (final QueryBinder b: binders) {
      b.bind(q);
    }
		return q;
	}
}
