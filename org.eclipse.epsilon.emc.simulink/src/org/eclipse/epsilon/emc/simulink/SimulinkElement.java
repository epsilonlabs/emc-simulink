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
		// The first time the path of the block is set
		// the block is added to the model
		// TODO: Once the element is created only keep
		// the last fragment of the type
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
	
	public void link(SimulinkElement other) {
		link(other, 1, 1);
	}
	
	public void linkTo(SimulinkElement other, int inPort) {
		link(other, 1, inPort);
	}
	
	public void linkFrom(SimulinkElement other, int outPort) {
		link(other, outPort, 1);
	}
	
	public void link(SimulinkElement other, int outPort, int inPort) {
		String command = "OutPortHandles = get_param('" + getPath() + "','PortHandles')\n" +
						 "InPortHandles = get_param('" + other.getPath() + "','PortHandles')\n" + 
						 "add_line('sample2',OutPortHandles.Outport(" + outPort + "),InPortHandles.Inport(" + inPort + "))";
		try {
			engine.eval(command);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
