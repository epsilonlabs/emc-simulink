package org.eclipse.epsilon.emc.simulink.dt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.epsilon.common.dt.EpsilonCommonsPlugin;
import org.eclipse.epsilon.emc.simulink.SimulinkModel;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SimulinkModelPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	protected List<FieldEditor> fieldEditors = new ArrayList<FieldEditor>();
	
	@Override
	protected Control createContents(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.FILL);

		fieldEditors.add(new DirectoryFieldEditor(SimulinkModel.PROPERTY_LIBRARY_PATH, "Library path", composite));
		fieldEditors.add(new FileFieldEditor(SimulinkModel.PROPERTY_ENGINE_JAR_PATH, "Engine JAR path", true, composite));

		for (FieldEditor fieldEditor : fieldEditors) {
			fieldEditor.setPreferenceStore(EpsilonCommonsPlugin.getDefault().getPreferenceStore());
			fieldEditor.load();
		}
		
		return composite;
	}
	
	public void init(IWorkbench workbench) {

	}

	@Override
	public boolean performOk() {
		for (FieldEditor fieldEditor : fieldEditors) {
			fieldEditor.store();
		}
		return true;
	}

}
