package edu.nrao.acs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public abstract class GenericAcsswContainerPage extends WizardPage implements
		IClasspathContainerPage, IClasspathContainerPageExtension {

	private final static String DEFAULT_EXTS = "jar,zip";
	private IJavaProject proj;
	private IPath _initPath = null;
	
    protected Text _extText;
    protected Table table;
	
	public GenericAcsswContainerPage() {
		super("ACS Installation Wizard","ACS Installation", null );
		setDescription("Select ACS installation directory");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setFont(parent.getFont());
        
        createAvailableLibsGroup(composite);
        
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
        } else if(!isDirValid(getSelectionEntry())) {
			setErrorMessage(NLS
					.bind("Select an ACS installation. If no ACS is listed below check your preferences",
							proj.getProject().getName()));
			return false;
        }        
        return true; 
	}

	@Override
	public abstract IClasspathEntry getSelection();

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
    private void createAvailableLibsGroup(Composite parent) {
        Composite dirSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 1;
        dirSelectionGroup.setLayout(layout);
        dirSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        table = new Table(dirSelectionGroup, SWT.SINGLE | SWT.BORDER | 
				SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(parent.getFont());
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.heightHint = 500;
		data.verticalSpan = 5;
		table.setLayoutData(data);
		String titles[] = {"Name     ", "Path"};
		for (String t: titles) {
			TableColumn c = new TableColumn(table, SWT.LEFT);
			c.setText(t);
			c.pack();
		}
		table.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				table = null;
				
			}
		});
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = layout.numColumns;
        gd.grabExcessHorizontalSpace = true;
        table.setLayoutData(gd);
        
        populateSwVersionsTable(table);
    }
    
    /**
     * @return the current directory
     */
    protected String getSelectionEntry() {
    	if (table.getSelectionIndex() == -1)
    		return "";
        return table.getSelection()[0].getText(0);
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
    	if (dir.equals(""))
    		return false;
    	return true;
    }

	protected void addItemToTable(String name, String path) {
		TableItem ti = new TableItem(table, SWT.NONE);
		ti.setText(0, name);
		ti.setImage(0, ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/obj16/library_obj.gif"));
		ti.setText(1, path);
		table.getColumn(0).pack();
		table.getColumn(1).pack();
	}

	public abstract void populateSwVersionsTable(Table table);
	
}
