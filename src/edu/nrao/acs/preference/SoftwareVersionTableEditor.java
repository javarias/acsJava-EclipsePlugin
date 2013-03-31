package edu.nrao.acs.preference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;

/**
 * 
 * 
 * @author javarias
 *
 */
public class SoftwareVersionTableEditor extends FieldEditor {
    /**
     * The Add button.
     */
    private Button addButton;

    /**
     * The Remove button.
     */
    private Button removeButton;
	
    /**
	 * The table to show different versions of software
	 */
	private Table table;
	
	public SoftwareVersionTableEditor(String name, String labelText,
			String dirChooserLabelText, Composite fieldEditorParent) {
		init(name, labelText);
		createControl(fieldEditorParent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) table.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        table = getTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        table.setLayoutData(gd);

        addButton = getAddButtonControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        addButton.setLayoutData(gd);
        
//        buttonBox = getButtonBoxControl(parent);
//        gd = new GridData();
//        gd.verticalAlignment = GridData.BEGINNING;
//        buttonBox.setLayoutData(gd);

	}

	@Override
	protected void doLoad() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doLoadDefault() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doStore() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTableControl(parent);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
	}
	
	private Table getTableControl(Composite parent) {
		if (table == null) {
			table = new Table(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setFont(parent.getFont());
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.heightHint = 500;
			table.setLayoutData(data);
			String titles[] = {"Name", "Path"};
			for (String t: titles) {
				TableColumn c = new TableColumn(table, SWT.NONE);
				c.setText(t);
			}
			TableItem i = new TableItem (table, SWT.NONE);
			i.setText(0, "ACS");
			i.setImage(0, ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/obj16/library_obj.gif"));
			i.setText(1, "/test/path");
			
			table.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					table = null;
					
				}
			});
			
			table.getColumn(0).pack();
			table.getColumn(1).pack();
		}
		return table;
	}
	
	private Button getAddButtonControl(final Composite parent) {
		if (addButton != null)
			return addButton;
		addButton = new Button(parent, SWT.PUSH|SWT.CENTER);
		addButton.setText("Add ACS library");
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AcsDirectoryStructureDialog dialog = new AcsDirectoryStructureDialog(parent.getShell());
				dialog.create();
				dialog.setTitle("ACS classpath selction");
				if (dialog.open() == IDialogConstants.OK_ID) {
					TableItem ti = new TableItem(table, SWT.NONE);
					System.out.println(dialog.getName() + " -- "+ dialog.getDirectoryPath());
					ti.setText(0, dialog.getName());
					ti.setImage(0, ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/obj16/library_obj.gif"));
					ti.setText(1, dialog.getDirectoryPath());
					table.getColumn(0).pack();
					table.getColumn(1).pack();
				}
			}
			
		});
		return addButton;
	}
	

}
