package org.eclipse.epsilon.emc.simulink;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.EolInternalException;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;

public class SimulinkModel extends CachedModel<SimulinkElement> {
	
	protected File file = null;
	protected SimulinkEngine engine;
	protected SimulinkPropertyGetter propertyGetter;
	
	public static String PROPERTY_FILE = "file";
	
	public void load(StringProperties properties, IRelativePathResolver resolver)
			throws EolModelLoadingException {
		
		super.load(properties, resolver);
		
		String filePath = properties.getProperty(SimulinkModel.PROPERTY_FILE);
		
		if (filePath != null && filePath.trim().length() > 0) {
			file = new File(resolver.resolve(filePath));
		}
		
		load();
	}
	
	@Override
	protected void loadModel() throws EolModelLoadingException {
		try {
			engine = SimulinkEnginePool.getInstance().getSimulinkEngine();
			if (readOnLoad) {
				engine.eval("open_system " + file.getAbsolutePath());
			}
			else {
				engine.eval("new_system");
			}
		} catch (Exception e) {
			throw new EolModelLoadingException(e, this);
		};
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public Object getEnumerationValue(String enumeration, String label) throws EolEnumerationValueNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeNameOf(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getElementById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setElementId(Object instance, String newId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean owns(Object instance) {
		return instance instanceof SimulinkElement && 
			((SimulinkElement) instance).getOwningModel() == this;
	}

	@Override
	public boolean isInstantiable(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasType(String type) {
		return true;
	}

	@Override
	public boolean store(String location) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean store() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Collection<SimulinkElement> allContentsFromModel() {
		try {
			return getElementsForPaths(engine.evalWithResult("find_system"));
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	protected Collection<SimulinkElement> getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		try {
			return getElementsForPaths(engine.evalWithResult("find_system('BlockType', '" + type + "')"));
		} catch (Exception e) {
			throw new EolModelElementTypeNotFoundException(this.getName(), type);
		}
	}

	@Override
	protected Collection<SimulinkElement> getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		if ("Block".equals(kind)) {
			return allContentsFromModel();
		}
		else return getAllOfTypeFromModel(kind);
	}
	
	protected List<SimulinkElement> getElementsForPaths(Object paths) {
		if (paths instanceof String) {
			paths = new String[]{(String) paths};
		}
		
		List<SimulinkElement> elements = new ArrayList<SimulinkElement>();
		for (String path : (String[]) paths) {
			elements.add(new SimulinkElement(this, path));
		}
		
		return elements;
	}
	
	@Override
	protected SimulinkElement createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void disposeModel() {
		SimulinkEnginePool.getInstance().release(engine);
	}

	@Override
	protected boolean deleteElementInModel(Object instance) throws EolRuntimeException {
		try {
			engine.eval("delete_block " + ((SimulinkElement) instance).getPath());
			return true;
		} catch (Exception e) {
			throw new EolInternalException(e);
		}
	}

	@Override
	protected Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
		return type;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IPropertyGetter getPropertyGetter() {
		if (propertyGetter == null) {
			propertyGetter = new SimulinkPropertyGetter(engine);
		}
		return propertyGetter;
	}
	
	public SimulinkEngine getEngine() {
		return engine;
	}
	
}
