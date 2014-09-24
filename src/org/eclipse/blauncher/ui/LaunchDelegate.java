package org.eclipse.blauncher.ui;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class LaunchDelegate implements ILaunchConfigurationDelegate  {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		List<ILaunchConfiguration> list = Utils.getAvailableToRunLaunchConfigurations(configuration); 
		for (ILaunchConfiguration conf : list) {
			if (!conf.getName().equals(configuration.getName())) {
				System.out.println(conf.getName());				
				conf.launch(mode, new SubProgressMonitor(monitor, 1), true);	
			}			
		}		
	}

}
