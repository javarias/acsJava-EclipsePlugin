package edu.nrao.acs.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.nrao.acs.AcsJavaPluginActivator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class SoftwareVersions
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SoftwareVersions() {
		super(GRID);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new SoftwareVersionTableEditor(PreferenceConstants.ACS_LIBS,
				"ACS versions", "ACS classpath selection", getFieldEditorParent()));
		
		addField(new SoftwareVersionTableEditor(PreferenceConstants.INTLIST_LIBS, "INTLIST versions", "INTLIST classpath selection", getFieldEditorParent()));
		
		addField(new SoftwareVersionTableEditor(PreferenceConstants.INTROOT_LIBS, "INTROOT versions", "INTROOT classpath selection", getFieldEditorParent()));	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(AcsJavaPluginActivator.getDefault().getPreferenceStore());
		setDescription("Setup the ACS and related software versions available in the filesystem");
	}
	
}