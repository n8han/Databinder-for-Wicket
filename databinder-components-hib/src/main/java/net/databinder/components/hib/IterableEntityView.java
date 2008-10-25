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
package net.databinder.components.hib;

import java.util.Iterator;

import net.databinder.models.hib.HibernateObjectModel;

import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * Similar to a PropertyListView, but works with any Iterable collection composed of
 * persisted entities. This is particularly useful for the common Hibernate set mapping, whose 
 * contents would otherwise need to be placed in a new List after loading. Contained items
 * must be Hibernate entities; a Hibernate exeception will be thrown otherwise. 
 * @author Nathan Hamblen
 */
public abstract class IterableEntityView<T> extends RefreshingView<T> {
	
	/**
	 * Contruct with externally bound model whose object must be Iterable.
	 * @param id
	 */
	public IterableEntityView(String id) {
		super(id);
	}
	
	/**
	 * @param id Wicket id
	 * @param model Wrapped object must be Iterable.
	 */
	public IterableEntityView(String id, IModel<T> model) {
		super(id, model);
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected final Iterator<IModel<T>> getItemModels() {
		return new ModelIterator((Iterable<T>) getDefaultModelObject());
	}
	
	/**
	 * Wraps o in a HibernateObjectModel inside a BoundCompoundPropertyModel. Override
	 * if the compound property model is not desired.
	 * @param o object to be wrapped
	 * @return detachable model wrapping object
	 */
	protected IModel<T> model(T o) {
		return new CompoundPropertyModel<T>(new HibernateObjectModel<T>(o));
	}
	
	private class ModelIterator implements Iterator<IModel<T>>
	{
		private Iterator<T> iterator;

		public ModelIterator(Iterable<T> items)
		{
			if (items != null)
				this.iterator = items.iterator();
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		public boolean hasNext()
		{
			return iterator != null && iterator.hasNext();
		}

		public IModel<T> next()
		{
			return model(iterator.next());
		}
	}
}
