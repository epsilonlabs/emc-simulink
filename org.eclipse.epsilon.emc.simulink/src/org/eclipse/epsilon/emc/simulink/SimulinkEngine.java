package org.eclipse.epsilon.emc.simulink;

import com.mathworks.engine.MatlabEngine;

public class SimulinkEngine {
	
	protected MatlabEngine engine;
	
	public SimulinkEngine() {
		try {
			this.engine = MatlabEngine.connectMatlab();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object evalWithResult(String cmd) throws Exception {
		engine.eval("result = " + cmd);
		return engine.getVariable("result");
	}
	
	public void eval(String cmd) throws Exception {
		engine.eval(cmd);
	}
	
}
