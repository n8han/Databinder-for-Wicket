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

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * DataTable property filter column that works with joined entities instead of string properties.
 * @author Mark Southern
 */
public class ObjectFilteredPropertyColumn<T, Y> extends ChoiceFilteredPropertyColumn<T, Y> {
	private ChoiceRenderer<Y> choiceRenderer;
	private String displayProperty;

	public ObjectFilteredPropertyColumn(IModel<String> displayModel, String sortProperty, String displayProperty, String propertyExpression, String filterLabelProperty, IModel<List<? extends Y>> filterChoices) {
		super(displayModel,sortProperty,propertyExpression,filterChoices);
		choiceRenderer = new ChoiceRenderer<Y>(filterLabelProperty);
		this.displayProperty = displayProperty;
	}

	protected IChoiceRenderer<Y> getChoiceRenderer() {
		return choiceRenderer;
	}

	protected IModel createLabelModel(IModel embeddedModel) {
		return new PropertyModel(embeddedModel, displayProperty);
	}

	public Component getFilter(String componentId, FilterForm form) {
		ChoiceFilter cf = (ChoiceFilter) super.getFilter(componentId, form);
		cf.getChoice().setNullValid(true);
		return cf;
	}
}
