package net.databinder.jpa;


import javax.persistence.EntityManager;

/**
 * Databinder application interface. DataStaticService expects the current Wicket
 * application to conform to this interface and supply a session factory as needed.
 *
 * @author rhansen@kindleit.net
 */
public interface JPAApplication {

  /**
	 * Supply the entity manager for the given key. Applications needing only one
	 * session factory may return it without inspecting the key parameter.
	 * @param persistenceContext or null for the default context
	 * @return configured Hibernate session factory
	 */
	EntityManager getEntityManager(String persistenceContext);

}
