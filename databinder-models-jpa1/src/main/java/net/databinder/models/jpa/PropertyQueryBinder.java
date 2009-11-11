package net.databinder.models.jpa;

import javax.persistence.Query;


/**
 * Binds a query's parameters to the properties of an object, as needed.
 *
 * @author Jonathan
 */
public class PropertyQueryBinder extends AbstractPropertyQueryBinder {

	private static final long serialVersionUID = -5670443203499179555L;

	private final Object object;
	private final String[] properties;

	/**
	 * @param object The object to bind properties of
	 * @param properties The properties list to associate
	 */
	public PropertyQueryBinder(final Object object, final String[] properties) {
		this.object = object;
		this.properties = properties;
	}

	/**
	 * @param query The query to bind
	 */
	public void bind(final Query query) {
		bind(query, object, properties);
	}
}