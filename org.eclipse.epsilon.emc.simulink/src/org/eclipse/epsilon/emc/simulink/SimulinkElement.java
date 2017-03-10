package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IModelElement;

public class SimulinkElement implements IModelElement {
	
	protected SimulinkModel model = null;
	protected String path = null;
	
	public SimulinkElement(SimulinkModel model, String path) {
		this.model = model;
		this.path = path;
	}
	
	@Override
	public IModel getOwningModel() {
		return model;
	}
	
	public String getPath() {
		return path;
	}
	
}
