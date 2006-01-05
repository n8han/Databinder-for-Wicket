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

package net.databinder.components;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.PageParameters;
import wicket.markup.html.PackageResourceReference;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.IModel;
import wicket.model.Model;

/**
 *  Simple page with a default stylesheet, page title, and feedback component in its 
 *  HTML template. Will not likely serve as a base for complicated layouts.
 *  @author Nathan Hamblen
 */
public abstract class DataPage extends WebPage {

	/**
	 * If this constructor is public in a subclass, the page will be bookmarkable and a valid default page.
	 */
	protected DataPage() {
		super();
		init();
	}
	
	/**
	 * If this constructor is public in a subclass, the page will be bookmarkable and a valid default page.
	 * When this and a no argument constructor are both public, this one will be used by default.	 * 
	 */	
	protected DataPage(PageParameters params) {
		super(params);	// nothing is done with params in WebPage
		init();
	}
	
	/**
	 * Construct the page with an existing model.
	 * @param model presumedly created in another page or component 
	 */
	protected DataPage(IModel model) {
		super(model);
		init();
	}
	
	/**
	 * Adds title, stylesheet, and feedback components.
	 */
	private void init() {		
		add(new Label("pageTitle", new Model() {
			@Override
			public Object getObject(Component component) {
				return getName();
			}
		}).setRenderBodyOnly(true));
		
		String cssUrl = urlFor(new PackageResourceReference(DataPage.class, "DataPage.css").getPath());
		add(new WebMarkupContainer("stylesheetLink").add(new AttributeModifier("href", new Model(cssUrl))));
		
		add(new FeedbackPanel("feedback"));
	}
	
	/**
	 * Name to be used as a title in the HEAD.
	 * @return
	 */
	protected abstract String getName();
}
