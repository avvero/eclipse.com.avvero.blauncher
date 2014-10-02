package org.eclipse.blauncher.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.blauncher.ui.messages"; //$NON-NLS-1$
	public static String SelectedConfigurationTabName;
	public static String ChooseConfigurationsLabel;
	public static String NotFoundConfiguartions;
	public static String SelectedConfigurationsError1;
	public static String SelectedConfigurationsError2;
	public static String DeleteDead;
	public static String Purge;
	public static String  ConfiguartionsNotSelected;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
