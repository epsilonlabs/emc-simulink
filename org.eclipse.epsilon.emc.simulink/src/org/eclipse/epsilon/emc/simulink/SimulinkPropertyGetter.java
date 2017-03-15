package org.eclipse.epsilon.emc.simulink;

import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;

public class SimulinkPropertyGetter extends JavaPropertyGetter {
	
	protected SimulinkEngine engine;
	
	public SimulinkPropertyGetter(SimulinkEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Object invoke(Object object, String property) throws EolRuntimeException {
		try {
			return engine.evalWithSetupAndResult("handle = ?", "get_param (handle, '?')", ((SimulinkElement) object).getHandle(), property);
		}
		catch (Exception ex) {
			return super.invoke(object, property);
		}
	}

}
