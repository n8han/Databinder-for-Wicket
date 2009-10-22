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
package net.databinder.auth.components;

import net.databinder.auth.AuthSession;
import net.databinder.auth.components.DataSignInPageBase.ReturnPage;
import net.databinder.auth.data.DataUser;
import net.databinder.components.NullPlug;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Displays username and password fields, along with optional "remember me" checkbox.
 * Queries the IAuthSession upon a login attempt. Replaceable String resources: <pre>
 * data.auth.username
 * data.auth.password
 * data.auth.remember
 * data.auth.sign_in</pre>
 * @see AuthSession
 */
public class DataSignInPanel<T extends DataUser> extends Panel {
	private ReturnPage returnPage;
	public DataSignInPanel(String id, ReturnPage returnPage) {
		super(id);
		this.returnPage = returnPage;
		add(newSignInForm("signInForm"));
	}
	
	/** Override to instantiate a SignInForm subclass. */
	protected SignInForm newSignInForm(String id) {
		return new SignInForm(id);
	}
	
	/** @return default value for remember me checkbox */
	protected boolean getRememberMeDefault() {
		return true;
	}

	protected class SignInForm extends Form {
		private CheckBox rememberMe;
		private RequiredTextField<String> username;
		private RSAPasswordTextField password;
		
		protected RequiredTextField getUsername() { return username; }
		protected RSAPasswordTextField getPassword() { return password; }
		protected CheckBox getRememberMe() { return rememberMe; }
		
		protected SignInForm(String id) {
			super(id);
			add(highFormSocket("highFormSocket"));
			add(feedbackBorder("username-border")
				.add(username = new RequiredTextField<String>("username", new Model<String>())));
			username.setLabel(new ResourceModel("data.auth.username", "Username"));
			add(new SimpleFormComponentLabel("username-label", username));
			add(feedbackBorder("password-border")
					.add(password = new RSAPasswordTextField("password", new Model<String>(), this)));
			password.setRequired(true);
			password.setLabel(new ResourceModel("data.auth.password", "Password"));
			add(new SimpleFormComponentLabel("password-label", password));
			add(rememberMe = new CheckBox("rememberMe", new Model<Boolean>(getRememberMeDefault())));
			add(new Label("rememberMe-text", getString("data.auth.remember", null, "Always sign in automatically")));
			
			add(lowFormSocket("lowFormSocket"));
		}
		@Override
		protected void onSubmit() {
			if (getAuthSession().signIn((String)username.getModelObject(), (String)password.getModelObject(), 
					(Boolean)rememberMe.getModelObject()))
			{
				if (returnPage == null) {
					if (!continueToOriginalDestination())
						setResponsePage(getApplication().getHomePage());
				} else
					setResponsePage(returnPage.get());
			} else
				error(getLocalizer().getString("signInFailed", this, "Sorry, these credentials are not recognized."));
		}
	}
	
	/** @return border to be added to each form component, base returns FormComponentFeedbackBorder */
	protected Border feedbackBorder(String id) {
		return new FormComponentFeedbackBorder(id);
	}

	/** @return content to appear above form, base return FeedbackPanel */
	protected Component highFormSocket(String id) {
		return new FeedbackPanel(id)
			.add(new AttributeModifier("class", true, new Model<String>("feedback")));
	}

	/** @return content to appear below form, base return blank */
	protected Component lowFormSocket(String id) {
		return new NullPlug(id);
	}
	/** @return casted session */
	protected AuthSession<T> getAuthSession() {
		return (AuthSession<T>) Session.get();
	}
}
