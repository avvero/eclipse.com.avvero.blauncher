package com.avvero.blauncher.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Performs launching for a specific type of launch configuration
 * 
 * @author belyaev-ay
 * 
 */
public class LaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(final ILaunchConfiguration configuration,
			final String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		// Only valid configuration (where all stored configurations exists and
		// can be fetched by launch manager) can be launched
		if (Utils.isConfigurationValid(configuration)) {
			List<ILaunchConfiguration> list = Utils
					.getStoredConfigurations(configuration);
			for (ILaunchConfiguration conf : list) {
				if (!conf.getName().equals(configuration.getName())) {
					conf.launch(mode, new SubProgressMonitor(monitor, 1), true);
				}
			}
		} else {
			// Open dialog to show problem with configuration
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					final IWorkbenchWindow workbenchWindow = PlatformUI
							.getWorkbench().getActiveWorkbenchWindow();
					if (workbenchWindow != null) {
						String identifier = DebugUITools.getLaunchGroup(
								configuration, mode).getIdentifier();
						DebugUITools.openLaunchConfigurationDialog(
								workbenchWindow.getShell(), configuration,
								identifier, null);
					}
				}
			});
		}
	}
}
