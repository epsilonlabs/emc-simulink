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
	
	public void attach(boolean makeNameUnique) {
		try {
			String makeNameUniqueFlag = makeNameUnique ? "on" : "off";
			Double handle = (Double) engine.evalWithResult("add_block('" + type + "', '" + path + "', 'MakeNameUnique', '" + makeNameUniqueFlag + "')");
			path = (String) engine.evalWithResult("getfullname(" + handle + ")");
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
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
						 "add_line('" + model.getSimulinkModelName() + "',OutPortHandles.Outport(" + outPort + "),InPortHandles.Inport(" + inPort + "))";
		try {
			engine.eval(command);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
