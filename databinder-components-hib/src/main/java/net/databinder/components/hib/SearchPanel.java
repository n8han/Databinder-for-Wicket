package net.databinder.components.hib;

import net.databinder.components.AjaxCell;
import net.databinder.components.AjaxOnKeyPausedUpdater;
import net.databinder.models.hib.CriteriaBuilder;
import net.databinder.models.hib.PropertyQueryBinder;
import net.databinder.models.hib.QueryBinder;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

/**
 * Panel for a "live" search field with a clear button. Instances of this class must
 * implement the onUpdate method to register external components for updating.
 * It is possible to override the search button text with the key "searchbutton.text"
 * The SearchPanel model maps to the text of the search.
 * @author Nathan Hamblen
 */
public abstract class SearchPanel extends Panel {

	private TextField search;

	/**
	 * @param id Wicket id
	 */
	public SearchPanel(final String id) {
		super(id, new Model());
		add(new SearchForm("searchForm"));
	}

	/** Use the given model (must not be read-only ) for the search string */
	public SearchPanel(final String id, final IModel searchModel) {
		super(id, searchModel);
		add(new SearchForm("searchForm"));
	}

	@Override
	/** Sets model to search component. */
	public MarkupContainer setDefaultModel(final IModel model) {
		return search.setDefaultModel(model);
	}

	/**
	 * Override to add components to be updated (or JavaScript to be executed)
	 * when the search string changes. Remember that added components must
	 * have a markup id; use component.setMarkupId(true) to assign one
	 * programmatically.
	 * @param target Ajax target to register components for update
	 */
	public abstract void onUpdate(AjaxRequestTarget target);

	/**
	 * Binds the search model to a "search" parameter in a query. The value in the
	 * search field will be bracketed by percent signs (%) for a find-anywhere match.
	 * In the query itself, "search" must be the name of the  one and only parameter
	 * If your needs differ, bind the model passed in to the SearchPanel constructor
	 * to your own IQueryBinder instance; this is a convenience method.
	 * @return binder for a "search" parameter
	 */
	public QueryBinder getQueryBinder() {
		return new PropertyQueryBinder(this);
	}

	/**
	 * Adds a criterion that will match the current search string anywhere within
	 * any of the given properties. If the search is empty, no criterion is added.
	 * @param searchProperty one or more properties to be searched
	 * @return builder to be used with list model or data provider
	 */
	public CriteriaBuilder getCriteriaBuilder(final String... searchProperty) {
		return getCriteriaBuilder(MatchMode.ANYWHERE, searchProperty);
	}

	/**
	 * Adds a criterion that will match the current search string within (depending on the MatchMode)
	 * any of the given properties. If the search is empty, no criterion is added.
	 * @param matchMode used against all properties
	 * @param searchProperty one or more properties to be searched
	 * @return builder to be used with list model or data provider
	 */
	public CriteriaBuilder getCriteriaBuilder(final MatchMode matchMode, final String... searchProperty) {
		return new CriteriaBuilder() {
			public void build(final Criteria criteria) {
				final String search = (String) getDefaultModelObject();
				if (search != null) {
					final Disjunction d = Restrictions.disjunction();
					for (final String prop : searchProperty) {
            d.add(Property.forName(prop).like(search, matchMode));
          }
					criteria.add(d);
				}
			}
		};
	}

	/** @return search string bracketed by the % wildcard */
	public String getSearch() {
		return getDefaultModelObject() == null ? null : "%" + getDefaultModelObject() + "%";
	}

	/** Resets the search model, The default behavior is setting it to null,
	 * but it is possible to override with your own custom defaults. */
  public void resetSearchModelObject() {
    setDefaultModelObject(null);
  }

  /** Form with AJAX components and their AjaxCells. */
	public class SearchForm extends Form {
		@SuppressWarnings("unchecked")
		public SearchForm(final String id) {
			super(id);

			final AjaxCell searchWrap = new AjaxCell("searchWrap");
			add(searchWrap);
			search = new TextField("searchInput", SearchPanel.this.getDefaultModel());
			search.setOutputMarkupId(true);
			searchWrap.add(search);

			final AjaxCell clearWrap = new AjaxCell("clearWrap");
			add(clearWrap);
			final AjaxLink clearLink = new AjaxLink("clearLink") {
				/** Clear field and register updates. */
				@Override
        public void onClick(final AjaxRequestTarget target) {
					resetSearchModelObject();
					target.addComponent(searchWrap);
					target.addComponent(clearWrap);
					onUpdate(target);
				}
				/** Hide when search is blank. */
				@Override
        public boolean isVisible() {
					return SearchPanel.this.getDefaultModelObject() != null;
				}
			};
			clearLink.setOutputMarkupId(true);
			clearLink.add( new Image("clear",
					new ResourceReference(this.getClass(), "clear.png")));
			clearWrap.add(clearLink);

			// triggered when user pauses or tabs out
			search.add(new AjaxOnKeyPausedUpdater() {
				@Override
        protected void onUpdate(final AjaxRequestTarget target) {
					target.addComponent(clearWrap);
					SearchPanel.this.onUpdate(target);
				}
			});
		}
	}
}
