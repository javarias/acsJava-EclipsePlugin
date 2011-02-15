package edu.nrao.acs;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class IntlistContainerPage extends WizardPage implements
		IClasspathContainerPage, IClasspathContainerPageExtension {

	private final static String DEFAULT_EXTS = "jar,zip";
	private IJavaProject proj;
	
	protected IntlistContainerPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());
        
 /*       createDirGroup(composite);
        
        createExtGroup(composite);*/
        
        setControl(composite); 

	}

	@Override
	public void initialize(IJavaProject project,
			IClasspathEntry[] currentEntries) {
		proj = project;
		
	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IClasspathEntry getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		// TODO Auto-generated method stub
		
	}

}
