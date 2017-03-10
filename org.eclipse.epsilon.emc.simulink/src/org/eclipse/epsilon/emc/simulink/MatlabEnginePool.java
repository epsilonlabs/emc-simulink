package org.eclipse.epsilon.emc.simulink;

import java.util.LinkedHashSet;
import java.util.Set;

import com.mathworks.engine.MatlabEngine;

public class MatlabEnginePool {
	
	protected static MatlabEnginePool instance;
	protected Set<MatlabEngine> pool = new LinkedHashSet<MatlabEngine>();
	
	public static MatlabEnginePool getInstance() {
		if (instance == null) {
			instance = new MatlabEnginePool();
		}
		return instance;
	}
	
	public MatlabEnginePool() {
		
	}
	
	public MatlabEngine getMatlabEngine() throws Exception {
		if (pool.isEmpty()) {
			return MatlabEngine.connectMatlab();
		}
		else {
			return pool.iterator().next();
		}
	}
	
	public void release(MatlabEngine engine) {
		pool.add(engine);
	}
	
	
}
