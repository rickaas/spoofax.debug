package org.spoofax.debug.core.launching;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdi.TimeoutException;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.SocketAttachConnector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMConnector;
import org.spoofax.debug.core.language.IDebugServiceFactory;
import org.spoofax.debug.core.language.LIConstants;

import com.ibm.icu.text.MessageFormat;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

@SuppressWarnings("restriction")
public class SpoofaxSocketAttachConnector extends SocketAttachConnector implements IVMConnector{

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMConnector#connect(java.util.Map, org.eclipse.core.runtime.IProgressMonitor, org.eclipse.debug.core.ILaunch)
	 */
	public void connect(Map arguments, IProgressMonitor monitor, ILaunch launch) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask(LaunchingMessages.SocketAttachConnector_Connecting____1, 2); 
		subMonitor.subTask(LaunchingMessages.SocketAttachConnector_Configuring_connection____1); 
		
		AttachingConnector connector= getAttachingConnector();
		String portNumberString = (String)arguments.get("port"); //$NON-NLS-1$
		if (portNumberString == null) {
			abort(LaunchingMessages.SocketAttachConnector_Port_unspecified_for_remote_connection__2, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_PORT); 
		}
		String host = (String)arguments.get("hostname"); //$NON-NLS-1$
		if (host == null) {
			abort(LaunchingMessages.SocketAttachConnector_Hostname_unspecified_for_remote_connection__4, null, IJavaLaunchConfigurationConstants.ERR_UNSPECIFIED_HOSTNAME); 
		}
		Map map= connector.defaultArguments();
		
        Connector.Argument param= (Connector.Argument) map.get("hostname"); //$NON-NLS-1$
		param.setValue(host);
		param= (Connector.Argument) map.get("port"); //$NON-NLS-1$
		param.setValue(portNumberString);
        
        String timeoutString = (String)arguments.get("timeout"); //$NON-NLS-1$
        if (timeoutString != null) {
            param= (Connector.Argument) map.get("timeout"); //$NON-NLS-1$
            param.setValue(timeoutString);
        }
        
		ILaunchConfiguration configuration = launch.getLaunchConfiguration();
		boolean allowTerminate = false;
		if (configuration != null) {
			allowTerminate = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE, false);
		}
		subMonitor.worked(1);
		subMonitor.subTask(LaunchingMessages.SocketAttachConnector_Establishing_connection____2); 
		try {
			VirtualMachine vm = connector.attach(map);
			String vmLabel = constructVMLabel(vm, host, portNumberString, configuration);
			
			
			// Here we create a debug target
			// RL: Don't create a Java Debug Target.
			//IDebugTarget debugTarget= JDIDebugModel.newDebugTarget(launch, vm, vmLabel, null, allowTerminate, true);
			org.spoofax.debug.core.Activator debuggerPluginBase = org.spoofax.debug.core.Activator.getDefault();
			String languageID = launch.getLaunchConfiguration().getAttribute(LIConstants.ATTR_LANGUAGE, (String)null);
			if (languageID == null) {
				// no language set...
				abort("Language not set for Launch Configuration" ,null, LIConstants.INTERNAL_ERROR);
			}
			IDebugServiceFactory factory = debuggerPluginBase.getServiceRegistry().getServiceFactory(languageID);
			IDebugTarget debugTarget = factory.createDebugTarget(languageID, launch, vm);
			
			launch.addDebugTarget(debugTarget);
			subMonitor.worked(1);
			subMonitor.done();
        } catch (TimeoutException e) {
            abort(LaunchingMessages.SocketAttachConnector_0, e, IJavaLaunchConfigurationConstants.ERR_REMOTE_VM_CONNECTION_FAILED);
		} catch (UnknownHostException e) {
			abort(MessageFormat.format(LaunchingMessages.SocketAttachConnector_Failed_to_connect_to_remote_VM_because_of_unknown_host____0___1, new String[]{host}), e, IJavaLaunchConfigurationConstants.ERR_REMOTE_VM_CONNECTION_FAILED); 
		} catch (ConnectException e) {
			abort(LaunchingMessages.SocketAttachConnector_Failed_to_connect_to_remote_VM_as_connection_was_refused_2, e, IJavaLaunchConfigurationConstants.ERR_REMOTE_VM_CONNECTION_FAILED); 
		} catch (IOException e) {
			abort(LaunchingMessages.SocketAttachConnector_Failed_to_connect_to_remote_VM_1, e, IJavaLaunchConfigurationConstants.ERR_REMOTE_VM_CONNECTION_FAILED); 
		} catch (IllegalConnectorArgumentsException e) {
			abort(LaunchingMessages.SocketAttachConnector_Failed_to_connect_to_remote_VM_1, e, IJavaLaunchConfigurationConstants.ERR_REMOTE_VM_CONNECTION_FAILED); 
		}
	}

	@Override
	public String getName() {
		return "Spoofax Socket Attach Connector";
	}

	@Override
	public String getIdentifier() {
		return LIConstants.SpoofaxSocketAttachConnector;
	}

}
