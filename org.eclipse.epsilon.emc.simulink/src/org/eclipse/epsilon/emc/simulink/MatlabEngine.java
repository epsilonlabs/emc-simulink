package org.eclipse.epsilon.emc.simulink;

//import com.mathworks.engine.MatlabEngine;

public class MatlabEngine {
	
	protected ReflectiveMatlabEngine engine;
	
	public MatlabEngine(String libraryPath, String engineJarPath) {
		try {
			this.engine = ReflectiveMatlabEngine.connectMatlab(libraryPath, engineJarPath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object evalWithResult(String cmd) throws Exception {
		eval("result = " + cmd);
		return engine.getVariable("result");
	}
	
	public Object evalWithSetupAndResult(String setup, String cmd, Object... parameters) {
		eval(setup + "\n" + "result = " + cmd, parameters);
		try {
			return engine.getVariable("result");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object evalWithResult(String cmd, Object... parameters) {
		eval("result = " + cmd, parameters);
		try {
			return engine.getVariable("result");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void eval(String cmd) {
		try {
			engine.eval(cmd);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void eval(String cmd, Object... parameters) {
		cmd = " " + cmd + " ";
		String[] parts = cmd.split("[?]");
		if (parts.length != parameters.length + 1) 
			throw new RuntimeException(parts.length - 1 + " parameters were expected but " + parameters.length + " were provided");
		
		cmd = parts[0];
		for (int i=0; i<parameters.length; i++) {
			cmd += String.valueOf(parameters[i]).replace("'", "''") + parts[i+1];
		}
		cmd = cmd.substring(1, cmd.length()-1);
		try {
			engine.eval(cmd);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ReflectiveMatlabEngine getImpl() {
		return engine;
	}
	
}
