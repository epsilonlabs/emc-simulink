package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;

public class SimulinkPropertySetter extends JavaPropertySetter {
	
	protected SimulinkEngine engine;
	
	public SimulinkPropertySetter(SimulinkEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public void invoke(Object value) throws EolRuntimeException {
		
		SimulinkElement element = (SimulinkElement) object;
		
		if ("parent".equalsIgnoreCase(property)) {
			element.setParent((SimulinkElement) value); return;
		}
		
		try {
			engine.eval("handle = ? \n set_param (handle, '?', '?')", element.getHandle(), property, value);
		}
		catch (Exception ex) {
			throw new EolInternalException(ex);
		}
	}
	
}
