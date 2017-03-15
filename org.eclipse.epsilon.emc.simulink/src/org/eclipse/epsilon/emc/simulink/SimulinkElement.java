package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IModelElement;

public class SimulinkElement implements IModelElement {
	
	protected SimulinkModel model = null;
	protected Double handle = null;
	protected String type;
	protected SimulinkEngine engine;
	
	public SimulinkElement(SimulinkModel model, String path, String type, SimulinkEngine engine) {
		this.model = model;
		try {
			handle = (Double) engine.evalWithResult("add_block('?', '?', 'MakeNameUnique', 'on')", type, path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.type = type;
		this.engine = engine;
	}
	
	public SimulinkElement(SimulinkModel model, Double handle, String type, SimulinkEngine engine) {
		this.model = model;
		this.handle = handle;
		this.type = type;
		this.engine = engine;
	}
	
	@Override
	public IModel getOwningModel() {
		return model;
	}
	
	public Double getHandle() {
		return handle;
	}
	
	public String getType() {
		if (type == null) {
			try {
				type = (String) engine.evalWithSetupAndResult("handle = ?", "get_param (handle, 'BlockType')", getHandle());
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
		String command = "sourceHandle = ?\n" +
						 "targetHandle = ?\n" +
						 "OutPortHandles = get_param(sourceHandle,'PortHandles')\n" +
						 "InPortHandles = get_param(targetHandle,'PortHandles')\n" + 
						 "add_line('?',OutPortHandles.Outport(?),InPortHandles.Inport(?))";
		try {
			engine.eval(command, getHandle(), other.getHandle(), model.getSimulinkModelName(), outPort, inPort);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void setParent(SimulinkElement parent) {
		try {
			String name = (String) new SimulinkPropertyGetter(engine).invoke(this, "name");
			Double newHandle = (Double) engine.evalWithResult("add_block('?', '?', 'MakeNameUnique', 'on')", getPath(), parent.getPath() + "/" + name);
			engine.eval("handle = ? \n delete_block(handle)", handle);
			handle = newHandle;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public SimulinkElement getParent() {
		
		String path = getPath();
		int lastPathSeparator = path.lastIndexOf("/");
		
		if (lastPathSeparator > -1) {
			String parentPath = path.substring(0, lastPathSeparator);
			try {
				Double parentHandle = (Double) engine.evalWithResult("getSimulinkBlockHandle('?')", parentPath);
				return new SimulinkElement(model, parentHandle, null, engine);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}
	
	public String getPath() {
		try {
			return (String) engine.evalWithResult("getfullname(" + handle + ")");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return getPath();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof SimulinkElement && ((SimulinkElement) other).getHandle().equals(this.getHandle());
	}
	
}
