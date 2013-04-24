package org.spoofax.debug.core.launching;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.spoofax.debug.core.language.LIConstants;

@SuppressWarnings("restriction")
public abstract class LIRemoteLaunchDelegate extends LaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	private IProject project;
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
        if (monitor == null){
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Connecting to "+this.getLanguageName()+" program", IProgressMonitor.UNKNOWN);
        
        // we connect to an existing program
		String projectAttributeValue = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		if (projectAttributeValue == null) {
			abort(this.getLanguageName() + " projectName unspecified.", null);
			return;
		}
		configuration.getAttributes().put(LIConstants.ATTR_LANGUAGE, this.getLIConstants().getLanguage());
		// TODO: check if project exists or value is valid
		this.project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectAttributeValue);
		
		// connect to the JVM
		
		String connectorId = getVMConnectorId(configuration);
		org.eclipse.jdt.launching.IVMConnector connector = null;
		if (connectorId == null) {
			connector = JavaRuntime.getDefaultVMConnector();
		} else {
			connector = JavaRuntime.getVMConnector(connectorId);
		}
		if (connector == null) {
			abort("Connector not specified", null); 
		}
		
		Map<String, String> argMap = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map<String, String>)null);
		
		int connectTimeout = Platform.getPreferencesService().getInt(
        		LaunchingPlugin.ID_PLUGIN, 
        		JavaRuntime.PREF_CONNECT_TIMEOUT, 
        		JavaRuntime.DEF_CONNECT_TIMEOUT, 
        		null);
        argMap.put("timeout", Integer.toString(connectTimeout));  //$NON-NLS-1$

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}
		
		monitor.worked(1);
		
		setDefaultSourceLocator(launch, configuration);
		monitor.worked(1);		
		
		// connect to remote VM
		connector.connect(argMap, monitor, launch);
		
		// check for cancellation
		if (monitor.isCanceled()) {
			IDebugTarget[] debugTargets = launch.getDebugTargets();
            for (int i = 0; i < debugTargets.length; i++) {
                IDebugTarget target = debugTargets[i];
                if (target.canDisconnect()) {
                    target.disconnect();
                }
            }
            return;
		}

		monitor.done();
	}
	
	public abstract String getLanguageName();

	public abstract LIConstants getLIConstants();
	
	/**
	 * Throws an exception with a new status containing the given
	 * message and optional exception.
	 * 
	 * @param message error message
	 * @param e underlying exception
	 * @throws CoreException
	 */
	protected void abort(String message, Throwable e) throws CoreException {
		// TODO: the plug-in code should be the example plug-in, not Stratego debug model id
		IStatus status = new Status(IStatus.ERROR, this.getLIConstants().getDebugModel(), 0, message, e);
		//throw new CoreException(status);
		
		IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
		
		if (handler != null) {
			Object result = handler.handleStatus(status, null);
			System.out.println(result);
		}
	}
	
	public IProject getProject() {
		return this.project;
	}
	
	/**
	 * Assigns a default source locator to the given launch if a source locator
	 * has not yet been assigned to it, and the associated launch configuration
	 * does not specify a source locator.
	 * 
	 * @param launch
	 *            launch object
	 * @param configuration
	 *            configuration being launched
	 * @exception CoreException
	 *                if unable to set the source locator
	 */
	protected void setDefaultSourceLocator(ILaunch launch,
			ILaunchConfiguration configuration) throws CoreException {
		//  set default source locator if none specified
		if (launch.getSourceLocator() == null) {
//			ISourceLookupDirector sourceLocator = new JavaSourceLookupDirector();
//			sourceLocator
//					.setSourcePathComputer(getLaunchManager()
//							.getSourcePathComputer(
//									"org.eclipse.jdt.launching.sourceLookup.javaSourcePathComputer")); //$NON-NLS-1$
//			sourceLocator.initializeDefaults(configuration);
//			launch.setSourceLocator(sourceLocator);
		}
	}
	/**
	 * Returns the VM connector identifier specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the VM connector identifier specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String getVMConnectorId(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
				(String) null);
	}
}
