package net.databinder.models.jpa;

import javax.persistence.Query;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

/**
 * A query binder that sets query parameters to corresponding properties taken
 * from the given Wicket model object.
 *
 * @param <T> Object type for the model
 *
 * @author rhansen@kindleit.net
 */
public class ModelPropertyQueryBinder<T> extends AbstractPropertyQueryBinder
		implements IDetachable {

	private static final long serialVersionUID = -6544558086991812867L;

	protected final IModel<T> model;
	protected final String[] properties;

	public ModelPropertyQueryBinder(final IModel<T> model, final String[] properties) {
		this.model = model;
		this.properties = properties;
	}

	public void detach() {
		model.detach();
	}

	public void bind(final Query query) {
		bind(query, model.getObject(), properties);
	}
}