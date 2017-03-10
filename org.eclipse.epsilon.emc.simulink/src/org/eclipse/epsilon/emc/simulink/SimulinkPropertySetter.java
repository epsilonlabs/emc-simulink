package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertySetter;

public class SimulinkPropertySetter extends JavaPropertySetter {
	
	protected SimulinkEngine engine;
	
	public SimulinkPropertySetter(SimulinkEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public void invoke(Object value) throws EolRuntimeException {
		try {
			engine.eval("set_param ('" + ((SimulinkElement) object).getPath() + "', '" + property + "', '" + value + "')");
		}
		catch (Exception ex) {
			super.invoke(value);
		}
	}
	
}
