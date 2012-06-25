package edu.nrao.acs;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class AcsJavaPluginActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "ACSJavaEclipsePlugin";
	private static AcsJavaPluginActivator plugin;

	public AcsJavaPluginActivator() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static AcsJavaPluginActivator getDefault() {
		if (plugin == null) {
			plugin = activatePlugin(PLUGIN_ID);
		}
		return plugin;
	}
	
	private static AcsJavaPluginActivator activatePlugin(String pluginID) {
		Bundle bundle = Platform.getBundle(pluginID);
		try {
			bundle.start();
		} catch (BundleException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return AcsJavaPluginActivator.getDefault();
	}
	
	
}
