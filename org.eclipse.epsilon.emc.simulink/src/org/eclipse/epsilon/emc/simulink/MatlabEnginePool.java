package org.eclipse.epsilon.emc.simulink;

import java.util.LinkedHashSet;
import java.util.Set;

public class MatlabEnginePool {
	
	protected static MatlabEnginePool instance;
	protected Set<MatlabEngine> pool = new LinkedHashSet<MatlabEngine>();
	protected String libraryPath;
	protected String engineJarPath;
	
	private MatlabEnginePool(String libraryPath, String engineJarPath) {
		this.libraryPath = libraryPath;
		this.engineJarPath = engineJarPath;
	}
	
	public static MatlabEnginePool getInstance(String libraryPath, String engineJarPath) {
		if (instance == null) {
			instance = new MatlabEnginePool(libraryPath, engineJarPath);
		}
		return instance;
	}
	
	public MatlabEngine getMatlabEngine() throws Exception {
		if (pool.isEmpty()) {
			return new MatlabEngine(libraryPath, engineJarPath);
		}
		else {
			MatlabEngine engine = pool.iterator().next();
			pool.remove(engine);
			return engine;
		}
	}
	
	public void release(MatlabEngine engine) {
		pool.add(engine);
	}
	
	
}
