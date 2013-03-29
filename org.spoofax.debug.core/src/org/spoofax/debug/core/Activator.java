package org.spoofax.debug.core;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.spoofax.debug.core.eclipse.LIDebugTarget;
import org.spoofax.debug.core.eclipse.ui.DebugElementAdapterFactory;
import org.spoofax.debug.core.language.ServiceRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.spoofax.debug.core";

	// The shared instance
	private static Activator plugin;
	
	private final ServiceRegistry serviceRegistry;
	
	/**
	 * The constructor
	 */
	public Activator() {
		super();
		plugin = this;
		this.serviceRegistry = new ServiceRegistry();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting " + PLUGIN_ID);
		super.start(context);
		//plugin = this; // RL: handled by constructor. Or is it better to do this in start due to performance?
		
		DebugElementAdapterFactory.init(); // Maybe this will fix the the cleanup of the thread contents after a resume (in the Debug View tree)
	}

	/**
	 * The plugin is shutting down, stop all jobs in each LIDebugTarget.
	 * 
	 * Method is inspired by JDIDebugPlugin.stop(BundleContext context)
	 * org.strategoxt.imp.debug.core.str.model.StrategoDebugTarget$EventDispatchJob.
	 * 
	 * TODO: do we also have to shutdown all plugins that depend on us?
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// TODO: Shutdown jobs from org.strategoxt.imp.debug.core.str.model.StrategoDebugTarget$EventDispatchJob
		try {
			if (DebugPlugin.getDefault() != null) {
				// find all LIDebugTarget
				ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
				IDebugTarget[] targets= launchManager.getDebugTargets();
				for (int i= 0 ; i < targets.length; i++) {
					IDebugTarget target= targets[i];
					if (target instanceof LIDebugTarget) {
						//((LIDebugTarget)target).shutdown();
					}
				}
			}
			
		} finally
		{
			// always stop
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public ServiceRegistry getServiceRegistry()
	{
		return this.serviceRegistry;
	}
	
	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}
	
	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
}
