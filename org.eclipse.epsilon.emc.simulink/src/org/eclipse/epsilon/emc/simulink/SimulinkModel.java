package org.eclipse.epsilon.emc.simulink;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.epsilon.eol.execute.introspection.IPropertySetter;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;

public class SimulinkModel extends CachedModel<SimulinkElement> {
	
	protected File file = null;
	protected SimulinkEngine engine;
	protected SimulinkPropertyGetter propertyGetter;
	protected SimulinkPropertySetter propertySetter;
	
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
	protected SimulinkElement createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {
		
		return new SimulinkElement(this, getSimulinkModelName() + "/" + getSimpleTypeName(type), type, engine);
	}
	
	protected String getSimpleTypeName(String type) {
		if (type.indexOf("/") > -1) {
			String[] parts = type.split("/");
			return parts[parts.length-1];
		}
		else {
			return type;
		}
	}
	
	@Override
	protected void loadModel() throws EolModelLoadingException {
		try {
			engine = SimulinkEnginePool.getInstance().getSimulinkEngine();
			if (readOnLoad) {
				// TODO: Add a flag for using the invisible load_system instead
				engine.eval("open_system " + file.getAbsolutePath());
			}
			else {
				try {
					engine.eval("new_system('?', 'Model')", getSimulinkModelName());
				}
				catch (Exception ex) {
					// Ignore; system already exists
				}
				engine.eval("open_system " + getSimulinkModelName());
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
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeNameOf(Object instance) {
		return ((SimulinkElement) instance).getType();
	}

	@Override
	public Object getElementById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementId(Object instance) {
		return ((SimulinkElement) instance).getHandle() + "";
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
		return hasType(type);
	}

	@Override
	public boolean hasType(String type) {
		return true;
	}

	@Override
	public boolean store(String location) {
		try {
			engine.eval("save_system ('?', '?')", getSimulinkModelName(), location);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean store() {
		store(file.getAbsolutePath());
		return true;
	}

	@Override
	protected Collection<SimulinkElement> allContentsFromModel() {
		try {
			return getElementsForHandles(engine.evalWithResult("find_system('?', 'FindAll', 'on')", getSimulinkModelName()), null);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@Override
	protected Collection<SimulinkElement> getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		try {
			return getElementsForHandles(engine.evalWithResult("find_system('?', 'FindAll', 'on', 'BlockType', '?')", getSimulinkModelName(), type), type);
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
	
	protected List<SimulinkElement> getElementsForHandles(Object handles, String type) {
		if (handles instanceof Double) {
			handles = new Double[]{(Double) handles};
		}
		
		List<SimulinkElement> elements = new ArrayList<SimulinkElement>();
		for (Double handle : (Double[]) handles) {
			elements.add(new SimulinkElement(this, handle, type, engine));
		}
		
		return elements;
	}

	@Override
	protected void disposeModel() {
		SimulinkEnginePool.getInstance().release(engine);
	}

	@Override
	protected boolean deleteElementInModel(Object instance) throws EolRuntimeException {
		try {
			engine.eval("handle = ? \n delete_block (handle)", ((SimulinkElement) instance).getHandle());
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
		return Arrays.asList("Block", ((SimulinkElement)instance).getType());
	}
	
	@Override
	public IPropertySetter getPropertySetter() {
		if (propertySetter == null) {
			propertySetter = new SimulinkPropertySetter(engine);
		}
		return propertySetter;
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
	
	public String getSimulinkModelName() {
		String name = file.getName();
		int pos = name.lastIndexOf(".");
		if (pos > 0) { name = name.substring(0, pos); }
		return name;
	}
	
}