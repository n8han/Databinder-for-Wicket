package net.databinder.auth.components.hib;

import java.util.List;

import net.databinder.auth.AuthSession;
import net.databinder.auth.components.DataUserStatusPanelBase;
import net.databinder.auth.components.UserAdminPageBase;
import net.databinder.auth.data.DataUser;
import net.databinder.components.hib.DataForm;
import net.databinder.hib.Databinder;
import net.databinder.models.hib.HibernateListModel;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * User administration page. Lists all users, allows editing usernames, passwords, and roles.
 * Must have Role.ADMIN to view. Replaceable String resources: <pre>
 * data.auth.user_admin
 * data.auth.user_add
 * data.auth.username
 * data.auth.password
 * data.auth.passwordConfirm
 * data.auth.roles
 * data.auth.save
 * data.auth.delete</pre>
 * @see AuthSession
 */
public class UserAdminPage<T extends DataUser> extends UserAdminPageBase<T> {
	private DataForm<T> form;
	
	@Override
	protected Form<T> adminForm(String id, Class<T> userClass) {
		return form = new DataForm<T>(id, userClass);
	}
	
	@Override
	protected Button deleteButton(String id) {
		return new Button("delete") {
			@Override
			public void onSubmit() {
				Databinder.getHibernateSession().delete(getUserForm().getModelObject());
				Databinder.getHibernateSession().getTransaction().commit();
				form.clearPersistentObject();
			}
			@Override
			public boolean isEnabled() {
				return !((AuthSession)getSession()).getUser().equals(getUserForm().getModelObject())
					&& getBindingModel().isBound();
			}
		}.setDefaultFormProcessing(false);	
	}
	
	
	@Override
	protected DataUserStatusPanelBase statusPanel(String id) {
		return new DataUserStatusPanel(id);
	}
	
	@Override
	protected IModel<List<T>> userList(Class<T> userClass) {
		return new HibernateListModel<T>(userClass);
	}

}
