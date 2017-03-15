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
		manageLink(other, outPort, inPort, true);
	}
	
	public void unlink(SimulinkElement other) {
		unlink(other, 1, 1);
	}
	
	public void unlinkTo(SimulinkElement other, int inPort) {
		unlink(other, 1, inPort);
	}
	
	public void unlinkFrom(SimulinkElement other, int outPort) {
		unlink(other, outPort, 1);
	}
	
	public void unlink(SimulinkElement other, int outPort, int inPort) {
		manageLink(other, outPort, inPort, false);
	}
	
	public void manageLink(SimulinkElement other, int outPort, int inPort, boolean create) {
		String command = "sourceHandle = ?\n" +
						 "targetHandle = ?\n" +
						 "OutPortHandles = get_param(sourceHandle,'PortHandles')\n" +
						 "InPortHandles = get_param(targetHandle,'PortHandles')\n" + 
						 "?_line('?',OutPortHandles.Outport(?),InPortHandles.Inport(?))";
		try {
			engine.eval(command, getHandle(), other.getHandle(), create ? "add" : "delete", getParentPath(), outPort, inPort);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	protected String getParentPath() {
		SimulinkElement parent = getParent();
		return parent == null ? model.getSimulinkModelName() : parent.getPath();
	}
	
	public void setParent(SimulinkElement parent) {
		try {
			String name = (String) new SimulinkPropertyGetter(engine).invoke(this, "name");
			String parentPath = parent == null ? model.getSimulinkModelName() : parent.getPath();
			Double newHandle = (Double) engine.evalWithResult("add_block('?', '?', 'MakeNameUnique', 'on')", getPath(), parentPath + "/" + name);
			engine.eval("handle = ? \n delete_block(handle)", handle);
			handle = newHandle;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Returns null for top-level elements and a 
	 * SimulinkElement for nested elements
	 */
	public SimulinkElement getParent() {
		
		String path = getPath();
		int lastPathSeparator = path.lastIndexOf("/");
		
		if (lastPathSeparator > -1) {
			String parentPath = path.substring(0, lastPathSeparator);
			
			if (parentPath.indexOf("/") < 0) return null;
			
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
