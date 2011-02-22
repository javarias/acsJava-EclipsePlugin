package edu.nrao.acs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class IntlistContainerPage extends WizardPage implements
		IClasspathContainerPage, IClasspathContainerPageExtension {

	private final static String DEFAULT_EXTS = "jar,zip";
	private IJavaProject proj;
	private IPath _initPath = null;
	
    private Text _extText;
    private Combo _dirCombo;
    private Button _dirBrowseButton;
	
	public IntlistContainerPage() {
		super("ACS INTLIST Wizard","ACS INTLIST", null );
		setDescription("INTLIST");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());
        
        createDirGroup(composite);
        
        createExtGroup(composite);
        
        setControl(composite); 

	}

	@Override
	public void initialize(IJavaProject project,
			IClasspathEntry[] currentEntries) {
		proj = project;
		
	}

	@Override
	public boolean finish() {
        if(!areExtsValid(getExtValue())) {
            setErrorMessage("Extensions are not valid.  Verify that the extensions are comma separated and do not include the preceding '.'");
            return false;    
        } else if(!isDirValid(getDirValue())) {
			setErrorMessage(NLS
					.bind("Select the directory for the Simple Dir Container",
							proj.getProject().getName()));
			return false;
        }        
        return true; 
	}

	@Override
	public IClasspathEntry getSelection() {
		String dir = getDirValue();
//        if(dir.equals("")) {
//            dir = IntlistContainer.ROOT_DIR;
//        }
        IPath containerPath = IntlistContainer.ID.append( "/" + dir + "/" + 
                                                                   getExtValue());
        return JavaCore.newContainerEntry(containerPath);
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
        if(containerEntry != null) {
            _initPath = containerEntry.getPath();
        }  
		
	}
	
    /**
     * Creates the extensions label and text box
     * 
     * @param parent parent widget
     */
    private void createExtGroup(Composite parent) {
        Composite extSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 2;
        extSelectionGroup.setLayout(layout);
        extSelectionGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL));

        new Label(extSelectionGroup, SWT.NONE).setText("Extensions (comma separated, not including .)");
        
        _extText = new Text(extSelectionGroup,SWT.BORDER);
        _extText.setText(getInitExts() + "         ");

        setControl(extSelectionGroup);
    }

    /**
     * Extracts the initial extensions list from a path passed in setSelection()
     * 
     * @return the intial comma separated list of extensions
     */
    private String getInitExts() {
        if(_initPath != null && _initPath.segmentCount() > 2 ) {
            return _initPath.segment(_initPath.segmentCount() - 1);
        }
        // else 
        return DEFAULT_EXTS;
    }
    
    /**
     * Creates the directory label, combo, and browse button
     * 
     * @param parent the parent widget
     */
    private void createDirGroup(Composite parent) {
        Composite dirSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 3;
        dirSelectionGroup.setLayout(layout);
        dirSelectionGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL));

        new Label(dirSelectionGroup, SWT.NONE).setText("Directory:");

        _dirCombo = new Combo(dirSelectionGroup, SWT.SINGLE | SWT.BORDER);
        _dirCombo.setText( getInitDir() );                

        _dirBrowseButton= new Button(dirSelectionGroup, SWT.PUSH);
        _dirBrowseButton.setText( "Browse..."); 
        _dirBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        _dirBrowseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleDirBrowseButtonPressed();
           }
        });    
        setControl(dirSelectionGroup);
    }
    
    /**
     * Extracts the initial directory value from a path passed in setSelection()
     * 
     * @return the inital directory value
     */
    private String getInitDir() {
        String projDir = proj.getProject().getLocation().toString();
        if(_initPath != null && _initPath.segmentCount() > 1 ) {
        	String dirPath = "";
        	for (int i = 1; i < _initPath.segmentCount() - 1; i++)
        		dirPath += IPath.SEPARATOR + _initPath.segment(i);
            return dirPath;
        }
        // else
        return projDir;
        
    }
    
    /**
     * Creates a directory dialog 
     */
    protected void handleDirBrowseButtonPressed() {
        DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setMessage("Select directory");
        dialog.setFilterPath(getDirValue());
        String dir = dialog.open();
        if (dir != null) {
            _dirCombo.setText(dir);            
        }            
    }
    
    /**
     * @return the current directory
     */
    protected String getDirValue() {
        return _dirCombo.getText();
    }
    
    /**
     * @return directory relative to the parent project
     */
    protected String getRelativeDirValue() {
        int projDirLen = proj.getProject().getLocation().toString().length();
        return getDirValue().substring( projDirLen );
    }
    
    /**
     * @return the current extension list
     */
    protected String getExtValue() {
        return _extText.getText().trim().toLowerCase();
    }
    
    /**
     * Checks that the list of comma separated extensions are valid.  Must meet the 
     * following:
     *  - non-null and non-empty
     *  - match the regex [a-z_][a-z_,]*
     * 
     * @param exts comma separated list of extensions
     * @return true if the extension list is valid
     */
    private boolean areExtsValid(String exts) {
        if(exts==null || exts.equals("")) {
            return false;
        }        
        //else
        return exts.matches("[a-z_][a-z_,]*");
    }
    
    /**
     * Checks that the directory is a subdirectory of the project being configured
     * 
     * @param dir a directory to validate
     * @return true if the directory is valid
     */
    private boolean isDirValid(String dir) {
//        Path dirPath = new Path(dir);
//        return proj.getProject().getLocation().makeAbsolute().isPrefixOf(dirPath);
    	return true;
    }
      
}
