package edu.nrao.acs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Table;

import edu.nrao.acs.preference.PreferenceConstants;

public class IntlistContainerPage extends GenericAcsswContainerPage {

	public IntlistContainerPage() {
		super("Intlist Wizard","INTLIST", "Select an INTLILST directory");
	}

	@Override
	public void populateSwVersionsTable(Table table) {
		IPreferenceStore ps = AcsJavaPluginActivator.getDefault().getPreferenceStore();
		String prefValue = ps.getString(PreferenceConstants.INTLIST_LIBS);
		if (prefValue.equals(""))
			return;
		for (String duple: prefValue.split(":")){
			String[] values = duple.substring(1, duple.length() - 1).split(",");
			addItemToTable(values[0], values[1]);
		}
	}
	
	@Override
	public IClasspathEntry getSelection() {
		String entry = getSelectionEntry();
        IPath containerPath = IntlistContainer.ID.append(entry + "/" + getExtValue());
        return JavaCore.newContainerEntry(containerPath);
	}
      
}
