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
		eval("result = " + cmd);
		return engine.getVariable("result");
	}
	
	public Object evalWithSetupAndResult(String setup, String cmd, Object... parameters) throws Exception {
		eval(setup + "\n" + "result = " + cmd, parameters);
		return engine.getVariable("result");
	}
	
	public Object evalWithResult(String cmd, Object... parameters) throws Exception {
		eval("result = " + cmd, parameters);
		return engine.getVariable("result");
	}
	
	public void eval(String cmd) throws Exception {
		engine.eval(cmd);
	}
	
	public void eval(String cmd, Object... parameters) throws Exception {
		cmd = " " + cmd + " ";
		String[] parts = cmd.split("[?]");
		if (parts.length != parameters.length + 1) 
			throw new Exception(parts.length - 1 + " parameters were expected but " + parameters.length + " were provided");
		
		cmd = parts[0];
		for (int i=0; i<parameters.length; i++) {
			cmd += String.valueOf(parameters[i]).replace("'", "''") + parts[i+1];
		}
		cmd = cmd.substring(1, cmd.length()-1);
		engine.eval(cmd);
	}
	
}
