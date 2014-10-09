package com.avvero.blauncher.core;

import static com.avvero.blauncher.core.BlauncherCoreConstants.SELECTED_CONFIGURATIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

/**
 * Useful methods
 * 
 * @author belyaev-ay
 * 
 */
public class Utils {
	
	@Inject
	static Logger logger; 

	/**
	 * Get all configurations available for launch by configuration currentLaunchConfiguration (i.e.
	 * all configurations without currentLaunchConfiguration)
	 * 
	 * @param the instance of ILaunchConfiguration
	 * @return the list of configurations
	 * @throws CoreException
	 */
	public static List<ILaunchConfiguration> getAvailableToRunLaunchConfigurations(
			ILaunchConfiguration currentLaunchConfiguration)
			throws CoreException {
		List<ILaunchConfiguration> result = new ArrayList<ILaunchConfiguration>();
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
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
	 * 
	 * @return the list of configurations
	 * @throws CoreException
	 */
	public static List<ILaunchConfiguration> getAllLaunchConfigurations()
			throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfiguration[] list = launchManager.getLaunchConfigurations();
		return Arrays.asList(list);
	}

	/**
	 * Get configuration by name
	 * 
	 * @param name the name of configuration
	 * @return the configuration with <code>name</code>
	 */
	public static ILaunchConfiguration getLaunchConfigurationByName(String name) {
		try {
			for (ILaunchConfiguration conf : getAllLaunchConfigurations()) {
				if (conf.getName().equals(name)) {
					return conf;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Names of configurations that had been chosen to launch by <code>launchConfig</code>
	 * 
	 * @param launchConfig the instance of ILaunchConfiguration
	 * @return the list of names of configurations 
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getNamesOfStoredConfigurations(
			ILaunchConfiguration launchConfig) {
		List<String> selectedNames;
		try {
			selectedNames = launchConfig.getAttribute(SELECTED_CONFIGURATIONS, new ArrayList<>());
		} catch (CoreException e) {
			selectedNames = new ArrayList<>();
		}
		return selectedNames;
	}

	/**
	 * Return names of configurations which had been chosen to launch <code>by this configuration</code>
	 * but had been lost for any reasons  
	 *
	 * @param storedNames the list of configuration names
	 * @return the list of names
	 */
	public static List<String> getStoredButLostConfigurationsNames(
			List<String> storedNames) {
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

	/**
	 * Check is configuration valid (has no STORED_AND_LOST configurations)
	 * @param launchConfig the configuration to check
	 * @return true if configuration valid 
	 */
	public static boolean isConfigurationValid(ILaunchConfiguration launchConfig) {
		return isAllStoredExists(launchConfig) && isConfigurationsSelected(launchConfig);
	}
	
	/**
	 * Check is configuration has no STORED_AND_LOST configurations
	 * @param launchConfig the configuration to check
	 * @return true if condition is satisfied
	 */
	public static boolean isAllStoredExists(ILaunchConfiguration launchConfig) {		
		List<String> storedNames = getNamesOfStoredConfigurations(launchConfig);
		List<String> lostNames = getStoredButLostConfigurationsNames(storedNames);
		return lostNames.size() == 0;
	}
	
	/**
	 * Check is configuration contains STORED_TO_LAUNCH configurations
	 * @param launchConfig the configuration to check
	 * @return true if condition is satisfied
	 */
	public static boolean isConfigurationsSelected(ILaunchConfiguration launchConfig) {
		List<ILaunchConfiguration> list = getStoredConfigurations(launchConfig);
		return list.size() != 0;
	}

	/**
	 * Returns configurations that had been chosen to launch by <code>launchConfig</code> 
	 * @param launchConfig the instance of ILaunchConfiguration 
	 * @return the list of configurations
	 */
	public static List<ILaunchConfiguration> getStoredConfigurations(
			ILaunchConfiguration launchConfig) {
		List<String> storedNames = getNamesOfStoredConfigurations(launchConfig);
		return getStoredConfigurations(storedNames);
	}

	/**
	 * Returns configurations by their names 
	 * @param storedNames the names of configurations  
	 * @return the list of configurations
	 */
	public static List<ILaunchConfiguration> getStoredConfigurations(
			List<String> storedNames) {
		List<ILaunchConfiguration> storedConfigurations = new ArrayList<ILaunchConfiguration>();
		if (storedNames.size() > 0) {
			Iterator<String> i = storedNames.iterator();
			while (i.hasNext()) {
				String name = i.next();
				ILaunchConfiguration foundConfiguration = Utils.getLaunchConfigurationByName(name);
				if (foundConfiguration != null) {
					storedConfigurations.add(foundConfiguration);
				}
			}
		}
		return storedConfigurations;
	}

}
