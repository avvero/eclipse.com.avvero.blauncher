package org.eclipse.blauncher.ui;

import static org.eclipse.blauncher.ui.IBlauncherUIConstants.SELECTED_CONFIGURATIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;

public class Utils {
	
	/**
	 * Get all configurations available for launch by THIS configuration (i.e. all configurations 
	 * without currentLaunchConfiguration) 
	 * @param currentLaunchConfiguration - instance of ILaunchConfiguration
	 * @return list of configurations 
	 * @throws CoreException
	 */
	public static List<ILaunchConfiguration> getAvailableToRunLaunchConfigurations(ILaunchConfiguration currentLaunchConfiguration) 
			throws CoreException {
		List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		//TODO check MODE!!!!
		ILaunchConfiguration[] list = launchManager.getLaunchConfigurations();
		for (ILaunchConfiguration conf : list) {
			if (!conf.getName().equals(currentLaunchConfiguration.getName())) {
				result.add(conf);
			}			
		}
		return result;		
	}
	
	/**
	 * Get all configurations from launchManager
	 * @return list of configurations
	 * @throws CoreException
	 */
	public static List<ILaunchConfiguration> getAllLaunchConfigurations() throws CoreException {		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] list = launchManager.getLaunchConfigurations();
		return Arrays.asList(list);		
	}	
	
	/**
	 * Get configuration by name
	 * @param name
	 * @return configuration with name 
	 */
	public static ILaunchConfiguration getLaunchConfigurationByName(String name) {		
		try {
			for (ILaunchConfiguration conf : getAllLaunchConfigurations()) {
				if (conf.getName().equals(name)) {
					return conf; 
				}			
			}
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
		}
		return null;
	}
	
	/**
	 * Names of configurations that had been stored for launch by this configuration 
	 * @param launchConfig
	 * @return
	 */
	public static List<String> getNamesOfStoredConfigurations(ILaunchConfiguration launchConfig) {
		List<String> selectedNames;
		try {
			selectedNames = launchConfig.getAttribute(SELECTED_CONFIGURATIONS, new ArrayList<>());
		} catch (CoreException e) {
			DebugUIPlugin.log(e);
			selectedNames = new ArrayList<>();
		}		
		return selectedNames;
	}	
	
	/**
	 * 
	 * @param storedNames
	 * @return
	 */
	public static List<String> getStoredButLostConfigurationsNames(List<String> storedNames) {		
		List<String> lostNames = new ArrayList<>(); 
		if (storedNames.size() > 0) {
			Iterator<String> i = storedNames.iterator();
			while (i.hasNext()) {
				String name = i.next(); 
				ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
				if (foundConfiguration == null) {
					lostNames.add(name);	
				}
			}
		}
		return lostNames;
	}		
	
	
	public static boolean isSomeStoredLost(List<String> lostNames) {		
		if (lostNames.size() > 0) {
			StringBuilder names = new StringBuilder();
			for (String name: lostNames) {
				if (names.length() > 0) {
					names.append(", ");	
				}
				names.append(name);				
			}
			return false;
		}
		return true;
	}
	
	public static boolean isConfigurationValid(ILaunchConfiguration launchConfig) {
		List<String> storedNames = getNamesOfStoredConfigurations(launchConfig); 
		List<String> lostNames = getStoredButLostConfigurationsNames(storedNames);		
		return isSomeStoredLost(lostNames);
	}
	
	public static List<ILaunchConfiguration> getStoredConfigurations(ILaunchConfiguration launchConfig) {
		List<String> storedNames = getNamesOfStoredConfigurations(launchConfig);
		return getStoredConfigurations(storedNames);
	}
	
	public static List<ILaunchConfiguration> getStoredConfigurations(List<String> storedNames) {		
		List<ILaunchConfiguration> storedConfigurations = new ArrayList<ILaunchConfiguration>();		
		if (storedNames.size() > 0) {
			Iterator<String> i = storedNames.iterator();
			while (i.hasNext()) {
				String name = i.next(); // must be called before you can call i.remove()
				ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
				if (foundConfiguration != null) {
					storedConfigurations.add(foundConfiguration);
				}
			}
		}
		return storedConfigurations;
	}		
		
}
