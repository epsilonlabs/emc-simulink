package org.eclipse.epsilon.emc.simulink;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflectiveMatlabEngine {
	
	protected Object matlabEngine;
	
	private ReflectiveMatlabEngine(Object matlabEngine) {
		this.matlabEngine = matlabEngine;
	}
	
	public void eval(String cmd) throws Exception {
		matlabEngine.getClass().getMethod("eval", String.class).invoke(matlabEngine, cmd);
	}

	public Object getVariable(String variable) throws Exception {
		return matlabEngine.getClass().getMethod("getVariable", String.class).invoke(matlabEngine, variable);
	}

	public static ReflectiveMatlabEngine connectMatlab(String libraryPath, String engineJarPath) throws Exception {
		System.setProperty("java.library.path", libraryPath);
		final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
	    sysPathsField.setAccessible(true);
	    sysPathsField.set(null, null);
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File(engineJarPath).toURI().toURL()});
		Class<?> matlabEngineClass = classLoader.loadClass("com.mathworks.engine.MatlabEngine");
		return new ReflectiveMatlabEngine(matlabEngineClass.getMethod("connectMatlab").invoke(null));
	}

}
