package com.avvero.blauncher.core.tabs;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class SimpleTreeLabelProvider implements ILabelProvider {

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
		ConfigurationTreeEntry entry = ((ConfigurationTreeEntry) element);
		ILaunchConfiguration configuration = entry.getConfiguration();
		if (configuration != null) {
			IDebugModelPresentation debugModelPresentation = DebugUITools.newDebugModelPresentation();
			Image image = debugModelPresentation.getImage(configuration);
			return image;	
		}
		return null;
	}

	@Override
	public String getText(Object element) {
	    // Get the name of the file
		ConfigurationTreeEntry entry = ((ConfigurationTreeEntry) element);
	    return entry.getName();
	}
	
} 