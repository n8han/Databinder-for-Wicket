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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Version;

import net.databinder.jpa.Databinder;
import net.databinder.models.BindingModel;
import net.databinder.models.LoadableWritableModel;

import org.apache.wicket.WicketRuntimeException;

/**
 * Model loaded and persisted via JPA. This central Databinder class can be initialized with an
 * entity ID, different types of queries, or an existing persistent object. As a writable Wicket model,
 * the object it contains may be swapped at any time for a different persistent object, a Serializable
 * object, or null.
 *
 * @param <T> Type of the Model
 *
 * @author rhansen@kindleit.net
 */
public class JPAObjectModel<T> extends LoadableWritableModel<T> implements BindingModel<T> {
  private static final long serialVersionUID = -8469845951034582593L;

  private final Class<T> objectClass;
	private final Serializable objectId;

	/** May store unsaved objects between requests. */
	private T retainedObject;
	/** Enable retaining unsaved objects between requests. */
	private boolean retainUnsaved = true;

	private String factoryKey;

	/**
	 * Create a model bound to the given class and entity id. If nothing matches
	 * the id the model object will be null.
	 * @param objectClass class to be loaded and stored by Hibernate
	 * @param entityId id of the persistent object
	 */
	public JPAObjectModel(final Class<T> objectClass, final Serializable entityId) {
		this.objectClass = objectClass;
		this.objectId = entityId;
	}

	/** @return session factory key, or null for the default factory */
	public Object getFactoryKey() {
		return factoryKey;
	}

	/**
	 * Set a factory key other than the default (null).
	 * @param key session factory key
	 * @return this, for chaining
	 */
	public JPAObjectModel<T> setFactoryKey(final String key) {
		this.factoryKey = key;
		return this;
	}

	/**
	 * Change the persistent object contained in this model.
	 * Because this method establishes a persistent object ID, queries and binders
	 * are removed if present.
	 * @param object must be an entity contained in the current Hibernate session, or Serializable, or null
	 */
	public void setObject(final T object) {
		throw new UnsupportedOperationException("Cannot set object");
	}

	public Serializable getIdentifier() {
	  return objectId;
	}

	/**
	 * Load the object through Hibernate, contruct a new instance if it is not
	 * bound to an id, or use unsaved retained object. Returns null if no
	 * criteria needed to load or construct an object are available.
	 */
	@Override
	protected T load() {
		try {
			if (!isBound()) {
				if (retainUnsaved && retainedObject != null) {
          return retainedObject;
        } else if (retainUnsaved) {
          try {
          	return retainedObject = objectClass.newInstance();
          } catch (final ClassCastException e) {
          	throw new WicketRuntimeException("Unsaved entity must be Serializable or retainUnsaved set to false; see JPAObjectModel javadocs.");
          }
        } else {
          return objectClass.newInstance();
        }
			}
		} catch (final ClassCastException e) {
			throw new RuntimeException("Retaining unsaved model objects requires that they be Serializable.", e);
		} catch (final Throwable e) {
			throw new RuntimeException("Unable to instantiate object. Does it have a default constructor?", e);
		}
   	return Databinder.getEntityManager(factoryKey).find(objectClass, objectId);
	}

	/**
	 * Uses version annotation to find version for this Model's object.
	 * @return Persistent storage version number if available, null otherwise
	 */
	public Serializable getVersion() {
		final Object o = getObject();

		if (o != null) {
			try {
				for (final Method m : objectClass.getMethods()) {
          if (m.isAnnotationPresent(Version.class)
							&& m.getParameterTypes().length == 0) {
            return (Serializable) m.invoke(o, new Object[] {});
          }
        }
				for (final Field f : objectClass.getDeclaredFields()) {
          if (f.isAnnotationPresent(Version.class)) {
						f.setAccessible(true);
						return (Serializable) f.get(o);
					}
        }
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/** Compares contained objects if present, otherwise calls super-implementation.*/
  @SuppressWarnings("unchecked")
  @Override
	public boolean equals(final Object obj) {
		final Object target = getObject();
		if (target != null && obj instanceof JPAObjectModel) {
      return target.equals(((JPAObjectModel)obj).getObject());
    }
		return super.equals(obj);
	}

	/** @return hash of contained object if present, otherwise from super-implementation.*/
	@Override
	public int hashCode() {
		final Object target = getObject();
		if (target == null) {
      return super.hashCode();
    }
		return target.hashCode();
	}


	/**
	 * Disassociates this object from any persistent object, but retains the class
	 * for constructing a blank copy if requested.
	 * @see #isBound()
	 */
	public void unbind() {
		retainedObject = null;
		detach();
	}

	/**
	 * "bound" models are those that can be loaded from persistent storage by a known id or
	 * query. When bound, this model discards its temporary model object at the end of every
	 * request cycle and reloads it via Hiberanate when needed again. When unbound, its
	 * behavior is dictated by the value of retanUnsaved.
	 * @return true if information needed to load from Hibernate (identifier, query, or criteria) is present
	 */
	public boolean isBound() {
		return objectId != null;
	}

	/**
	 * When retainUnsaved is true (the default) and the model is not bound,
	 * the model object must be Serializable as it is retained in the Web session between
	 * requests. See isBound() for more information.
	 * @return true if unsaved objects should be retained between requests.
	 */
	public boolean getRetainUnsaved() {
		return retainUnsaved;
	}

	/**
	 * Unsaved Serializable objects can be retained between requests.
	 * @param retainUnsaved set to true to retain unsaved objects
	 */
	public void setRetainUnsaved(final boolean retainUnsaved) {
		this.retainUnsaved = retainUnsaved;
	}
}
