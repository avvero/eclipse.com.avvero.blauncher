package org.eclipse.blauncher.ui.tabs;

import org.eclipse.blauncher.ui.tabs.SelectLouncherTab.TreeContentProvider;
import org.eclipse.blauncher.ui.tabs.SelectLouncherTab.TreeLabelProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

public class TabComposite extends Composite {
	
	private CheckboxTreeViewer checkboxTreeViewer;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TabComposite(Composite parent, TreeContentProvider contentProvider, TreeLabelProvider labelProvider, int style) {
		super(parent, style);		
		Composite composite = this;
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		checkboxTreeViewer = new CheckboxTreeViewer(composite, SWT.BORDER);
		Tree tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		checkboxTreeViewer.setContentProvider(contentProvider);
		checkboxTreeViewer.refresh();
		checkboxTreeViewer.setLabelProvider(labelProvider);
		checkboxTreeViewer.setInput("root");
	}
	
	public void refreshTree() {
		checkboxTreeViewer.refresh();
	}
	
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText("Choose configurations to launch in bunch");

	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);		

		return composite;
	}	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
