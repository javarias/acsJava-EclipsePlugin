package edu.nrao.acs.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.nrao.acs.AcsJavaPluginActivator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AcsJavaPluginActivator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.ACS_LIBS, "(ACS-current,/alma/ACS-current)");
		store.setDefault(PreferenceConstants.INTLIST_LIBS, "");
		store.setDefault(PreferenceConstants.INTLIST_LIBS, "");
	}

}
