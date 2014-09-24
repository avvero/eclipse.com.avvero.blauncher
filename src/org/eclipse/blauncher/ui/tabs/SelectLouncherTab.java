package org.eclipse.blauncher.ui.tabs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.blauncher.ui.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class SelectLouncherTab extends AbstractLaunchConfigurationTab {

	private List<ILaunchConfiguration> launchConfigurations;
	private TabComposite content;

	@Override
	public void createControl(Composite parent) {
		try {
			setLaunchConfigurations(Utils.getAllLaunchConfigurations());
		} catch (CoreException e) {
			setLaunchConfigurations(new ArrayList<ILaunchConfiguration>());
		}
		TreeContentProvider contentProvider = new TreeContentProvider();		
		TreeLabelProvider labelProvider = new TreeLabelProvider();
		content = new TabComposite(parent, contentProvider, labelProvider, SWT.NONE);
        setControl(content);		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.print("arg0");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			setLaunchConfigurations(Utils.getAvailableToRunLaunchConfigurations(configuration));
		} catch (CoreException e) {
			setLaunchConfigurations(new ArrayList<ILaunchConfiguration>());
		}		
		content.refreshTree();
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		System.out.print("arg0");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Launchers";
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

	class TreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {		
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getLaunchConfigurations().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {			// 
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}
	
	class TreeLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object element) {
		    // Get the name of the file
			ILaunchConfiguration configuration = ((ILaunchConfiguration) element);
		    return configuration.getName();
		}
		
	} 

}
