package net.databinder.components.cay;

import net.databinder.models.cay.DataObjectModel;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IChainingModel;

/** Form to be used with a single object, wraps in a compound property model. */
public class DataForm<T extends DataObject> extends CommittingDataForm<T> {
	public DataForm(String id, Class<T> cl) {
		super(id, new CompoundPropertyModel<T>(new DataObjectModel<T>(cl)));
	}
	public DataForm(String id, T object) {
		super(id, new CompoundPropertyModel<T>(new DataObjectModel<T>(object)));
	}
	public DataForm(String id, ObjectId objectId) {
		super(id, new CompoundPropertyModel<T>(new DataObjectModel<T>(objectId)));
	}
	public DataObjectModel<T> getPersistentObjectModel() {
		return (DataObjectModel<T>) ((IChainingModel<T>)getModel()).getChainedModel();
	}
}
