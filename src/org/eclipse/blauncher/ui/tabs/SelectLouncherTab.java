package org.eclipse.blauncher.ui.tabs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.eclipse.blauncher.ui.IBlauncherUIConstants.SELECTED_CONFIGURATIONS;

import org.eclipse.blauncher.ui.Messages;
//import org.eclipse.blauncher.ui.BlauncherConstants;
import org.eclipse.blauncher.ui.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy; 
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;


public class SelectLouncherTab extends AbstractLaunchConfigurationTab {
	
	/**
	 * @wbp.parser.entryPoint
	 */
	SelectLouncherTab () {
//		setHelpContextId(BlauncherConstants.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
	}

	private List<ILaunchConfiguration> availableLaunchConfigurations; //available to select
	private List<ILaunchConfiguration> selectedLaunchConfigurations;  //selected in tree	
	private List<String> storedNamesOfselectedConfigurations;         //stored 
	private CheckboxTreeViewer checkboxTreeViewer;
	private Composite fixTreeSection;
	private Composite treeSection;

	@Override
	public void createControl(Composite parent) {						
		Composite composite = createDefaultComposite(parent);
        setControl(composite);	
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), getHelpContextId());
		createFixComponent(composite);
		addFirstSection(composite);
		addSeparator(composite);
		addTree(composite);		
	}

	public void refreshTree() {
		checkboxTreeViewer.refresh();
	}
	
	private void createFixComponent(Composite parent) {		
		fixTreeSection = createDefaultSection(parent);
		Composite composite = SWTFactory.createComposite(fixTreeSection, parent.getFont(), 2, 2, 
				GridData.FILL_BOTH, 0, 0);		
		Label pathLabel = SWTFactory.createLabel(composite, Messages.DeleteDead, 1);
		Button fixTreeButton = createPushButton(composite, Messages.Purge, null);
		fixTreeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				deleteMissedConfigurations();
				refreshTree();
				updateLaunchConfigurationDialog();
			}
		});	
	}
	
	private void deleteMissedConfigurations() {
		storedNamesOfselectedConfigurations = new ArrayList<>();
	}
	
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultSection(parent);
		Label pathLabel = new Label(composite, GridData.FILL_HORIZONTAL);	    
		//Label for path field		
		pathLabel.setText(Messages.ChooseConfigurationsLabel);

	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}
	
	private void addTree(Composite parent) {
		treeSection = createDefaultComposite(parent);
		checkboxTreeViewer = new CheckboxTreeViewer(treeSection, SWT.BORDER);
		Tree tree = checkboxTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		checkboxTreeViewer.setContentProvider(new SimpleTreeContentProvider(){
			@Override
			public Object[] getElements(Object inputElement) {				
				return getAllConfigurationsForTree().toArray();
			}});		
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				ConfigurationTreeEntry entry = (ConfigurationTreeEntry)event.getElement();
				updateSelectedLaunchConfigurations(entry.getConfiguration(), event.getChecked());
				updateLaunchConfigurationDialog();
			}
		});
		checkboxTreeViewer.setCheckStateProvider(new ICheckStateProvider(){
			@Override
			public boolean isChecked(Object element) {
				// TODO Auto-generated method stub
				ConfigurationTreeEntry entry = (ConfigurationTreeEntry) element; 
				return entry.getConfiguration() == null ? true : getSelectedLaunchConfigurations().contains(entry.getConfiguration());
			}
			@Override
			public boolean isGrayed(Object element) {
				ConfigurationTreeEntry entry = (ConfigurationTreeEntry) element;
				return !entry.isValid();
			}
		});
		checkboxTreeViewer.setLabelProvider(new SimpleTreeLabelProvider());
		checkboxTreeViewer.setInput("root");			
	}
	
	/**
	 * Get all configurations for tree (include deleted)
	 * @return
	 */
	private List<ConfigurationTreeEntry> getAllConfigurationsForTree() {
		List<ConfigurationTreeEntry> result = new ArrayList<>(); 
		List<ILaunchConfiguration> availableLaunchConfigurations = getAvailableLaunchConfigurations();
		for (ILaunchConfiguration configuration : availableLaunchConfigurations) {
			result.add(new ConfigurationTreeEntry(configuration, configuration.getName(), true));	
		}	
		boolean isValid = checkStoredAndAvailableConfigurations();
		if (!isValid) {
			List<String> excluded = getExcludedConfigurationsNames(); 
			if (excluded.size() > 0) {
				for (String name: excluded) {
					result.add(new ConfigurationTreeEntry(null, name, false));				
				}				
			}
		}
		return result;
	}

	private Composite createDefaultSection(Composite parent) {
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
		
	private Composite createDefaultComposite(Composite parent) {
		Composite tt = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		tt.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		tt.setLayoutData(data);
		return tt;
	}	

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {        
		configuration.setContainer(null);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {		 
		try {
			setAvailableLaunchConfigurations(Utils.getAvailableToRunLaunchConfigurations(configuration));
		} catch (CoreException e) {
			setAvailableLaunchConfigurations(new ArrayList<ILaunchConfiguration>());
		}			
		setStoredNamesOfselectedConfigurations(configuration);
		updateSelectedConfigurationsFromConfig(configuration);		
		refreshTree();
		updateEnableState();
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (canSave()) {
			updateConfigFromSelectedConfigurations(configuration);	
		}
		updateEnableState();
	}
	
	private void updateEnableState() {
		boolean isValid = checkStoredAndAvailableConfigurations();
		fixTreeSection.setEnabled(!isValid);
		fixTreeSection.setVisible(!isValid);
		treeSection.setEnabled(isValid);
	}
	
	private void updateConfigFromSelectedConfigurations(ILaunchConfigurationWorkingCopy configuration) {
		List<ILaunchConfiguration> configurations = getSelectedLaunchConfigurations();
		if (configurations != null && configurations.size() > 0) {
			List<String> names = new ArrayList<>();
			for (ILaunchConfiguration iLaunchConfiguration: configurations) {
				names.add(iLaunchConfiguration.getName());				
			}
			configuration.setAttribute(SELECTED_CONFIGURATIONS, names);
		}
	}
	
	private void updateSelectedConfigurationsFromConfig(ILaunchConfiguration configuration) {
		List<String> selectedNames = getStoredNamesOfselectedConfigurations();
		List<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();		
		if (selectedNames.size() > 0) {
			Iterator<String> i = selectedNames.iterator();
			while (i.hasNext()) {
				String name = i.next(); // must be called before you can call i.remove()
				ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
				if (foundConfiguration != null) {
					selectedConfigurations.add(foundConfiguration);
				}
			}
		}
		setSelectedLaunchConfigurations(selectedConfigurations);
	}	

	@Override
	public String getName() {
		return Messages.SelectedConfigurationTabName;
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		boolean isValid = checkStoredAndAvailableConfigurations();
		if (!isValid) {
			setErrorMessage(Messages.NotFoundConfiguartions);
			try {
				updateConfigFromSelectedConfigurations(launchConfig.getWorkingCopy());
			} catch (CoreException e) {DebugUIPlugin.log(e);}
		}
		return isValid;	
	}	
	
	/**
	 * @see ILaunchConfigurationTab#canSave()
	 */
	public boolean canSave() {
		boolean isValid = checkStoredAndAvailableConfigurations();
		return isValid;	
	}
	
	private boolean checkStoredAndAvailableConfigurations() {		
		List<String> notFounded = getExcludedConfigurationsNames(); 
		if (notFounded.size() > 0) {
			StringBuilder names = new StringBuilder();
			for (String name: notFounded) {
				if (names.length() > 0) {
					names.append(", ");	
				}
				names.append(name);				
			}
			return false;
		}
		return true;
	}
	
	private List<String> getExcludedConfigurationsNames() {
		List<String> selectedNames = getStoredNamesOfselectedConfigurations();			
		List<String> notFounded = new ArrayList<>(); 
		if (selectedNames.size() > 0) {
			Iterator<String> i = selectedNames.iterator();
			while (i.hasNext()) {
				String name = i.next(); // must be called before you can call i.remove()
				ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
				if (foundConfiguration == null) {
					notFounded.add(name);	
				}
			}
		}
		return notFounded;
	}
	
	public List<ILaunchConfiguration> getAvailableLaunchConfigurations() {
		if (availableLaunchConfigurations == null) {
			availableLaunchConfigurations = new ArrayList<ILaunchConfiguration>();
		}
		return availableLaunchConfigurations;
	}

	public void setAvailableLaunchConfigurations(List<ILaunchConfiguration> availableLaunchConfigurations) {
		this.availableLaunchConfigurations = availableLaunchConfigurations;
	}
	
	
	public List<ILaunchConfiguration> getSelectedLaunchConfigurations() {
		if (selectedLaunchConfigurations == null) {
			selectedLaunchConfigurations = new ArrayList<ILaunchConfiguration>();
		}
		return selectedLaunchConfigurations;
	}

	public void setSelectedLaunchConfigurations(List<ILaunchConfiguration> selectedLaunchConfigurations) {
		this.selectedLaunchConfigurations = selectedLaunchConfigurations;
	}	

	
	/**
	 * Change list of launch configurations selected in tree 
	 * @param configuration
	 * @param addNew
	 * @return
	 */
	public List<ILaunchConfiguration> updateSelectedLaunchConfigurations(ILaunchConfiguration configuration, boolean addNew) {
		if (addNew) {
			for(ILaunchConfiguration entry : selectedLaunchConfigurations) {
				if (entry.equals(configuration)) {
					setErrorMessage(Messages.SelectedConfigurationsError1);
					return selectedLaunchConfigurations;
				}
			}
			selectedLaunchConfigurations.add(configuration);
		} else {
			boolean isDeleted = false;
			Iterator<ILaunchConfiguration> i = selectedLaunchConfigurations.iterator();
			while (i.hasNext()) {
				ILaunchConfiguration entry = i.next(); // must be called before you can call i.remove()
				if (entry.equals(configuration)) {
					i.remove();
					isDeleted = true;
					break;
				} 			   
			}
			if (!isDeleted) {
				setErrorMessage(Messages.SelectedConfigurationsError2);
			}
		}
		return selectedLaunchConfigurations;
	}

	public List<String> getStoredNamesOfselectedConfigurations() {
		if (storedNamesOfselectedConfigurations == null) {
			storedNamesOfselectedConfigurations = new ArrayList<>();
		}
		return storedNamesOfselectedConfigurations;
	}

	public void setStoredNamesOfselectedConfigurations(List<String> storedNamesOfselectedConfigurations) {
		this.storedNamesOfselectedConfigurations = storedNamesOfselectedConfigurations;
	}	
	
	public void setStoredNamesOfselectedConfigurations(ILaunchConfiguration launchConfig) {
		List<String> selectedNames;
		try {
			selectedNames = launchConfig.getAttribute(SELECTED_CONFIGURATIONS, 
					new ArrayList<>());
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
			selectedNames = new ArrayList<>();
		}
		setStoredNamesOfselectedConfigurations(selectedNames);
	}		
	
}
