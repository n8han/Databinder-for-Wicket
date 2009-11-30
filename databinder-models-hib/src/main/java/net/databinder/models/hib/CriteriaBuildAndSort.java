package net.databinder.models.hib;

/*---
 Copyright 2008 The Scripps Research Institute
 http://www.scripps.edu
 
* Databinder: a simple bridge from Wicket to Hibernate
*
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
 ---*/

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

/**
 * Abstract base class for building OrderedCriteriaBuilders. Uses an ISortStateLocator to configure
 * the sorting.  Subclasses should call super.buildUnordered() when overriding.
 * 
 * @author Mark Southern
 */
public abstract class CriteriaBuildAndSort extends BaseCriteriaBuildAndSort implements ISortStateLocator {
	private SingleSortState sortState = new SingleSortState();

	public CriteriaBuildAndSort(final String defaultSortProperty, final boolean sortAscending, final boolean sortCased) {
		super(defaultSortProperty, sortAscending, sortCased);
	}

	@Override
	public void buildOrdered(final Criteria criteria) {
		buildUnordered(criteria);

		SortParam sort = sortState.getSort();
		String property;
		if (sort != null && sort.getProperty() != null) {
			property = sort.getProperty();
			sortAscending = sort.isAscending();
		}
		else {
			property = defaultSortProperty;
		}

		if (property != null) {
			property = processProperty(criteria, property);
			Order order = sortAscending ? Order.asc(property) : Order.desc(property);
			order = sortCased ? order : order.ignoreCase();
			criteria.addOrder(order);
		}
	}

	public ISortState getSortState() {
		return sortState;
	}

	public void setSortState(final ISortState state) {
		sortState = (SingleSortState) state;
	}
}