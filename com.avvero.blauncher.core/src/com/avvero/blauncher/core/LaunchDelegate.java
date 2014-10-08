package com.avvero.blauncher.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
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
			List<ILaunchConfiguration> storedConfigurations = Utils.getStoredConfigurations(configuration);			
			SubMonitor progress = SubMonitor.convert(monitor, configuration.getName(), storedConfigurations.size());
			for (ILaunchConfiguration conf : storedConfigurations) {
				launchAndAttach(conf, mode, launch, progress.newChild(1));
			}
			monitor.done();
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
	
	/**
	 * Launches the given configuration in the specified mode and attach contributed by launch method
	 * debug targets and/or processes to parent launch.
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 * @throws CoreException
	 */
	public void launchAndAttach(final ILaunchConfiguration configuration, final String mode, ILaunch launch, 
			IProgressMonitor monitor) throws CoreException {
		if (configuration.supportsMode(mode)) {
			// Launch
			ILaunch result = configuration.launch(mode, monitor);
			// Attach
			for (IDebugTarget target : result.getDebugTargets()) {
				launch.addDebugTarget(target);
			}
			for (IProcess process : result.getProcesses()) {
				launch.addProcess(process);
			}
		}	
	}
}
