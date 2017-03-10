package org.eclipse.epsilon.emc.simulink;

import java.util.LinkedHashSet;
import java.util.Set;

import com.mathworks.engine.MatlabEngine;

public class SimulinkEnginePool {
	
	protected static SimulinkEnginePool instance;
	protected Set<SimulinkEngine> pool = new LinkedHashSet<SimulinkEngine>();
	
	public static SimulinkEnginePool getInstance() {
		if (instance == null) {
			instance = new SimulinkEnginePool();
		}
		return instance;
	}
	
	public SimulinkEnginePool() {
		
	}
	
	public SimulinkEngine getSimulinkEngine() throws Exception {
		if (pool.isEmpty()) {
			return new SimulinkEngine();
		}
		else {
			return pool.iterator().next();
		}
	}
	
	public void release(SimulinkEngine engine) {
		pool.add(engine);
	}
	
	
}
