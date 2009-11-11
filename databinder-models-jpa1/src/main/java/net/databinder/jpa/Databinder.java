/*
 * Databinder: a simple bridge from Wicket to Hibernate
 * Copyright (C) 2006  Nathan Hamblen nathan@technically.us
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
 */

package net.databinder.jpa;

import javax.persistence.EntityManager;

import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;




/**
 * Provides access to application-bound Hibernate session factories and current sessions.
 * This class will work with a
 * <a href="http://www.hibernate.org/hib_docs/v3/api/org/hibernate/context/ManagedSessionContext.html">ManagedSessionContext</a>
 * and DataRequestCycle listener when present, but neither is required so long as a
 * "current" session is available from the session factory supplied by the application.
 * @see JPAApplication
 * @author rhansen@kindleit.net
 */
public class Databinder {

  /**
   * @return default session factory, as returned by the application
   * @throws WicketRuntimeException if session factory can not be found
   * @see JPAApplication
   */
  public static EntityManager getEntityManager() {
    return getEntityManager(null);
  }
  /**
   * @param context object, or null for the default factory
   * @return session factory, as returned by the application
   * @throws WicketRuntimeException if session factory can not be found
   * @see JPAApplication
   */
  public static EntityManager getEntityManager(final String context) {
    final Application app = Application.get();
    if (app instanceof JPAApplication) {
      return ((JPAApplication)app).getEntityManager(context);
    }
    throw new WicketRuntimeException("Please implement JPAApplication in your Application subclass.");
  }

}