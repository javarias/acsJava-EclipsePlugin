package edu.nrao.acs.preference;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.ResourceManager;

import edu.nrao.acs.AcsJavaPluginActivator;

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
	
	private String dirChooserLabelText;
	
	public SoftwareVersionTableEditor(String name, String labelText,
			String dirChooserLabelText, Composite fieldEditorParent) {
		init(name, labelText);
		createControl(fieldEditorParent);
		this.dirChooserLabelText = dirChooserLabelText;
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
        GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
        gd.horizontalSpan = numColumns;
        gd.verticalSpan = 5;
        control.setLayoutData(gd);

        table = getTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        table.setLayoutData(gd);

        Composite buttonsGroup = new Composite(parent, SWT.NONE);
        buttonsGroup.setLayout( new GridLayout(1, true));
        addButton = getAddButtonControl(buttonsGroup);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        gd.horizontalAlignment = GridData.FILL;
        addButton.setLayoutData(gd);
        
        removeButton = getRemoveButtonControl(buttonsGroup);
        removeButton.setLayoutData(gd);

	}
	
	@Override
	protected void doLoad() {
		IPreferenceStore ps = AcsJavaPluginActivator.getDefault().getPreferenceStore();
		String prefValue = ps.getString(this.getPreferenceName());
		if (prefValue.equals(""))
			return;
		for (String duple: prefValue.split(":")){
			String[] values = duple.substring(1, duple.length() - 1).split(",");
			addItemToTable(values[0], values[1]);
		}

	}

	@Override
	protected void doLoadDefault() {
		table.removeAll();
		IPreferenceStore ps = AcsJavaPluginActivator.getDefault().getPreferenceStore();
		String prefDefaultValue = ps.getDefaultString(this.getPreferenceName());
		if (prefDefaultValue.equals(""))
			return;
		for (String duple: prefDefaultValue.split(":")){
			String[] values = duple.substring(1, duple.length() - 1).split(",");
			addItemToTable(values[0], values[1]);
		}
		
	}

	@Override
	protected void doStore() {
		String prefValue = new String("");
		for (TableItem item: table.getItems()) {
			String duple = "(" + item.getText(0) + "," + item.getText(1) + ")";
			prefValue += duple + ":";
		}
		if (prefValue.endsWith(":"))
			prefValue = prefValue.substring(0, prefValue.length() - 1);
		AcsJavaPluginActivator.getDefault().getPreferenceStore().setValue(getPreferenceName(), prefValue);
	}

	@Override
	public int getNumberOfControls() {
		return 3;
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
			table = new Table(parent, SWT.MULTI | SWT.BORDER | 
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
			}
			table.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					table = null;
					
				}
			});
		}
		return table;
	}
	
	private Button getAddButtonControl(final Composite parent) {
		if (addButton != null)
			return addButton;
		addButton = new Button(parent, SWT.PUSH|SWT.CENTER);
		addButton.setText("Add library");
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AcsDirectoryStructureDialog dialog = new AcsDirectoryStructureDialog(parent.getShell());
				dialog.create();
				dialog.setTitle(dirChooserLabelText);
				if (dialog.open() == IDialogConstants.OK_ID) {
					addItemToTable(dialog.getName(), dialog.getDirectoryPath());
				}
			}
			
		});
		return addButton;
	}
	
	private Button getRemoveButtonControl(Composite parent) {
		if (removeButton != null)
			return removeButton;
		removeButton = new Button(parent, SWT.PUSH|SWT.CENTER);
		removeButton.setText("Remove selection");
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionCount() > 0) {
					table.remove(table.getSelectionIndices());
					table.getColumn(0).pack();
					table.getColumn(1).pack();
				}
			}
			
		});
		return removeButton;
	}
	
	private void addItemToTable(String name, String path) {
		TableItem ti = new TableItem(table, SWT.NONE);
		ti.setText(0, name);
		ti.setImage(0, ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/obj16/library_obj.gif"));
		ti.setText(1, path);
		table.getColumn(0).pack();
		table.getColumn(1).pack();
	}

}
