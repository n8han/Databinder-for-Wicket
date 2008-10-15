package net.databinder.components;

import net.databinder.models.BindingModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.Link;

/**
 * Unbinds the target persistent model. This will generally revert the object
 * to a "blank" state, such as when creating a new object with a form instead of
 * updating one.
 * @see BindingModel
 */
public class UnbindLink<T> extends Link<T> {
	private Component target;
	
	/**
	 * @param id this component id
	 * @param target component to be notified when unbinding model
	 * @param model
	 */
	public UnbindLink(String id, Component target, BindingModel<T> model) {
		super(id, model);
		this.target = target;
	}
	/** unbinds model */
	@Override
	public void onClick() {
		target.modelChanging();
		((BindingModel<T>)getModel()).unbind();
		target.modelChanged();
	}
	/** @return true if model is bound */
	@Override
	public boolean isEnabled() {
		return ((BindingModel<T>)getModel()).isBound();
	}
}
