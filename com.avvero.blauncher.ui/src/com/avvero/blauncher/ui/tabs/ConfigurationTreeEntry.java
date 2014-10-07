package com.avvero.blauncher.ui.tabs;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Entry element of tree, wraper for ILaunchConfiguration
 * @author belyaev-ay
 *
 */
public class ConfigurationTreeEntry {
	
	private ILaunchConfiguration configuration;
	private boolean isValid;
	private String name;
	
	public ConfigurationTreeEntry(ILaunchConfiguration configuration, String name, boolean isValid) {
		this.configuration = configuration;
		this.isValid = isValid;
		this.name = name;
	}
	public ILaunchConfiguration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(ILaunchConfiguration configuration) {
		this.configuration = configuration;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
