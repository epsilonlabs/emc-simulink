package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IModelElement;

public class SimulinkElement implements IModelElement {
	
	protected SimulinkModel model = null;
	protected String path = null;
	protected String type;
	protected SimulinkEngine engine;
	
	public SimulinkElement(SimulinkModel model, String path, String type, SimulinkEngine engine) {
		this.model = model;
		this.path = path;
		this.type = type;
		this.engine = engine;
	}
	
	@Override
	public IModel getOwningModel() {
		return model;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		if (this.path == null && path != null) {
			try {
				engine.eval("add_block('" + type + "', '" + path + "')");
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		this.path = path;
	}
	
	public String getType() {
		if (type == null) {
			try {
				type = (String) engine.evalWithResult("get_param ('" + getPath() + "', 'BlockType')");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return type;
	}
}
