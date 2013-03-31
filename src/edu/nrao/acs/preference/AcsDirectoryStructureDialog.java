package edu.nrao.acs.preference;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public class AcsDirectoryStructureDialog extends TitleAreaDialog {

	private final static String DEFAULT_EXTS = "jar,zip";
	private Text _extText;
	private Combo _dirCombo;
	private Button _dirBrowseButton;
	private Text _nameText;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AcsDirectoryStructureDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TITLE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/wizban/addlibrary_wiz.png"));
		Composite container = (Composite) super.createDialogArea(parent);

		createNameGroup(container);
		createDirGroup(container);
		createExtGroup(container);
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
	
	private void createNameGroup(Composite parent) {
		Composite classpathNameGroup = new Composite(parent, SWT.NONE);
		classpathNameGroup.setLayout(new GridLayout(3, false));
        GridData gd_dirSelectionGroup = new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL | GridData.FILL);
        gd_dirSelectionGroup.horizontalAlignment = SWT.FILL;
        classpathNameGroup.setLayoutData(gd_dirSelectionGroup);
        
        new Label(classpathNameGroup, SWT.NONE).setText("Name:");
        
        _nameText = new Text(classpathNameGroup, SWT.BORDER);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        _nameText.setLayoutData(gridData);
	}
	
	private void createExtGroup(Composite parent) {
        Composite extSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.numColumns = 2;
        extSelectionGroup.setLayout(layout);
        GridData gd_extSelectionGroup = new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL);
        gd_extSelectionGroup.horizontalAlignment = SWT.FILL;
        extSelectionGroup.setLayoutData(gd_extSelectionGroup);

        new Label(extSelectionGroup, SWT.NONE).setText("Extensions (comma separated, not including .)");
        
        _extText = new Text(extSelectionGroup,SWT.BORDER);
        _extText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        _extText.setText(getInitExts() + "         ");

    }
	
    private String getInitExts() {
        return DEFAULT_EXTS;
    }
    
    protected void handleDirBrowseButtonPressed(Composite parent) {
        DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.SAVE);
        dialog.setMessage("Select directory");
        dialog.setFilterPath(getDirValue());
        String dir = dialog.open();
        if (dir != null) {
            _dirCombo.setText(dir);
            if (_nameText.getText().equals("")) {
            	_nameText.setText(dir.substring(dir.lastIndexOf(IPath.SEPARATOR) + 1, dir.length()));
            }
        }            
    }
    
    protected String getDirValue() {
        return _dirCombo.getText();
    }

    private void createDirGroup(final Composite parent) {
        Composite dirSelectionGroup = new Composite(parent, SWT.NONE);
        dirSelectionGroup.setLayout(new GridLayout(3, false));
        GridData gd_dirSelectionGroup = new GridData(GridData.GRAB_HORIZONTAL| GridData.VERTICAL_ALIGN_FILL | GridData.FILL);
        gd_dirSelectionGroup.horizontalAlignment = SWT.FILL;
        dirSelectionGroup.setLayoutData(gd_dirSelectionGroup);

        new Label(dirSelectionGroup, SWT.NONE).setText("Directory:");

        _dirCombo = new Combo(dirSelectionGroup, SWT.SINGLE | SWT.BORDER);
        _dirCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _dirCombo.setText( getInitDir() );
        
        _dirBrowseButton= new Button(dirSelectionGroup, SWT.PUSH);
        _dirBrowseButton.setText( "Browse...");
        _dirCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        _dirBrowseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleDirBrowseButtonPressed(parent);
           }
        });    
    }
    
    private String getInitDir() {
        String homeDir =  System.getenv("HOME");
        return homeDir;//.substring(0, homeDir.lastIndexOf(IPath.SEPARATOR));
    }
}
