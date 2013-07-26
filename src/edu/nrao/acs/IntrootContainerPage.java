package edu.nrao.acs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Table;

import edu.nrao.acs.preference.PreferenceConstants;

public class IntrootContainerPage extends GenericAcsswContainerPage {

	@Override
	public void populateSwVersionsTable(Table table) {
		IPreferenceStore ps = AcsJavaPluginActivator.getDefault().getPreferenceStore();
		String prefValue = ps.getString(PreferenceConstants.INTROOT_LIBS);
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
        IPath containerPath = IntrootContainer.ID.append(entry + "/" + getExtValue());
        return JavaCore.newContainerEntry(containerPath);
	}
	
}