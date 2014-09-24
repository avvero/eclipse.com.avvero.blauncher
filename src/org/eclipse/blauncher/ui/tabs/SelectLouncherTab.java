package org.eclipse.blauncher.ui.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.blauncher.ui.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

public class SelectLouncherTab extends AbstractLaunchConfigurationTab {

	private List<ILaunchConfiguration> launchConfigurations;
	private Composite composite;
	private CheckboxTreeViewer checkboxTreeViewer;

	@Override
	public void createControl(Composite parent) {						
		composite = new Composite(parent, SWT.NONE);
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
		checkboxTreeViewer.setContentProvider(new SimpleTreeContentProvider(){
			@Override
			public Object[] getElements(Object inputElement) {				
				return getLaunchConfigurations().toArray();
			}});		
		SimpleTreeLabelProvider labelProvider = new SimpleTreeLabelProvider();
		checkboxTreeViewer.setLabelProvider(labelProvider);
		checkboxTreeViewer.setInput("root");
        setControl(composite);		
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
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {        
		try {
			System.out.print("!!! ===" + configuration.getAttribute("field1", "default"));
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {		 
		try {
			setLaunchConfigurations(Utils.getAvailableToRunLaunchConfigurations(configuration));
		} catch (CoreException e) {
			setLaunchConfigurations(new ArrayList<ILaunchConfiguration>());
		}		
		refreshTree();
		updateLaunchConfigurationDialog();
		try {
			configuration.getWorkingCopy().setAttribute("field1", "value1");
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("field1", "value1"); 
		try {
			configuration.doSave();
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Launchers";
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);
		return true;
	}	
	
	public List<ILaunchConfiguration> getLaunchConfigurations() {
		if (launchConfigurations == null) {
			launchConfigurations = new ArrayList<ILaunchConfiguration>();
		}
		return launchConfigurations;
	}

	public void setLaunchConfigurations(List<ILaunchConfiguration> launchConfigurations) {
		this.launchConfigurations = launchConfigurations;
	}

}
