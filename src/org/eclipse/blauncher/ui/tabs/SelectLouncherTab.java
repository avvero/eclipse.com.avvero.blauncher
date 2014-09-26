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
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;


public class SelectLouncherTab extends AbstractLaunchConfigurationTab {
	
	SelectLouncherTab () {
//		setHelpContextId(BlauncherConstants.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
	}

	private List<ILaunchConfiguration> launchConfigurations;	
	private List<ILaunchConfiguration> selectedLaunchConfigurations;
	private CheckboxTreeViewer checkboxTreeViewer;

	@Override
	public void createControl(Composite parent) {						
		Composite composite = new Composite(parent, SWT.NONE);
        setControl(composite);	
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), getHelpContextId());
		
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
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				ILaunchConfiguration configuration = (ILaunchConfiguration)event.getElement();
				updateSelectedLaunchConfigurations(configuration, event.getChecked());
				updateLaunchConfigurationDialog();
			}
		});
		checkboxTreeViewer.setCheckStateProvider(new ICheckStateProvider(){
			@Override
			public boolean isChecked(Object element) {
				// TODO Auto-generated method stub
				return getSelectedLaunchConfigurations().contains(element);
			}
			@Override
			public boolean isGrayed(Object element) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		checkboxTreeViewer.setLabelProvider(new SimpleTreeLabelProvider());
		checkboxTreeViewer.setInput("root");	
	}

	public void refreshTree() {
		checkboxTreeViewer.refresh();
	}
	
	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);
		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(Messages.ChooseConfigurationsLabel);

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
		configuration.setContainer(null);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {		 
		try {
			setLaunchConfigurations(Utils.getAvailableToRunLaunchConfigurations(configuration));
		} catch (CoreException e) {
			setLaunchConfigurations(new ArrayList<ILaunchConfiguration>());
		}			
		updateSelectedConfigurationsFromConfig(configuration);	
		refreshTree();	
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		updateConfigFromSelectedConfigurations(configuration);
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
		try {
			List<String> selectedNames = configuration.getAttribute(SELECTED_CONFIGURATIONS, 
					new ArrayList<>());
			List<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();
			List<String> notFounded = new ArrayList<>(); 
			if (selectedNames.size() > 0) {
				Iterator<String> i = selectedNames.iterator();
				while (i.hasNext()) {
					String name = i.next(); // must be called before you can call i.remove()
					ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
					if (foundConfiguration == null) {
						notFounded.add(name);	
					} else {
						selectedConfigurations.add(foundConfiguration);
					}
				}
			}
			setSelectedLaunchConfigurations(selectedConfigurations);
			if (notFounded.size() > 0) {
				StringBuilder names = new StringBuilder();
				for (String name: notFounded) {
					if (names.length() > 0) {
						names.append(", ");	
					}
					names.append(name);				
				}
				setErrorMessage(String.format(Messages.NotFoundConfiguartions, names));
				//updateConfigFromSelectedConfigurations(configuration.getWorkingCopy());
				//updateLaunchConfigurationDialog();
			}
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
		}
	}	

	@Override
	public String getName() {
		return Messages.SelectedConfigurationTabName;
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		try {
			List<String> selectedNames = launchConfig.getAttribute(SELECTED_CONFIGURATIONS, 
					new ArrayList<>());
			List<ILaunchConfiguration> selectedConfigurations = new ArrayList<ILaunchConfiguration>();
			List<String> notFounded = new ArrayList<>(); 
			if (selectedNames.size() > 0) {
				Iterator<String> i = selectedNames.iterator();
				while (i.hasNext()) {
					String name = i.next(); // must be called before you can call i.remove()
					ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
					if (foundConfiguration == null) {
						notFounded.add(name);	
					} else {
						selectedConfigurations.add(foundConfiguration);
					}
				}
			}
			if (notFounded.size() > 0) {
				StringBuilder names = new StringBuilder();
				for (String name: notFounded) {
					if (names.length() > 0) {
						names.append(", ");	
					}
					names.append(name);				
				}
				setErrorMessage(String.format(Messages.NotFoundConfiguartions, names));
				return true;
			}
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
		}
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
	
}
