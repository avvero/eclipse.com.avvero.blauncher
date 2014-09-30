package org.eclipse.blauncher.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class LaunchDelegate implements ILaunchConfigurationDelegate {

	@Override
	public void launch(final ILaunchConfiguration configuration,
			final String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		if (Utils.isConfigurationValid(configuration)) {
			List<ILaunchConfiguration> list = Utils.getStoredConfigurations(configuration);
			for (ILaunchConfiguration conf : list) {
				if (!conf.getName().equals(configuration.getName())) {
					System.out.println(conf.getName());
					conf.launch(mode, new SubProgressMonitor(monitor, 1), true);
					// DebugUITools.launch(configuration, mode);
				}
			}
		} else {
			// Open dialog to show problem
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (workbenchWindow != null) {
						DebugUITools.openLaunchConfigurationDialog(
								workbenchWindow.getShell(), 
								configuration,
								DebugUITools.getLaunchGroup(configuration, mode).getIdentifier(), 
								null);
					}
				}
			});
		}
	}
}
