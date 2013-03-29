package org.spoofax.debug.core.eclipse;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.spoofax.debug.core.control.java.VMLaunchHelper;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.ListeningConnector;

public class JVMConnector {
	
	/**
	 * Call back method
	 */
	private final IJVMLaunched jvmLaunched;
	
	private final String port;

	private final String pluginID;
	
	/**
	 * A jvm has already been launched which is accepting connections at the given port.
	 * @param jvmLaunched
	 * @param port
	 */
	public JVMConnector(IJVMLaunched jvmLaunched, String port, String pluginID) {
		this.jvmLaunched = jvmLaunched;
		this.port = port;
		this.pluginID = pluginID;
	}
	
	public void tryAttaching() throws CoreException {
		VirtualMachineManager eclipseVMManager = org.eclipse.jdi.Bootstrap.virtualMachineManager();
		final AttachingConnector connector = VMLaunchHelper.findSocketAttachConnector(eclipseVMManager);
		
		// TODO: maybe require some more initialization
		final Map<String, Connector.Argument> args = connector.defaultArguments();
		VMLaunchHelper.setArguments(args, "127.0.0.1", port);
		
		try {
			VirtualMachine jvm = connector.attach(args);
			jvmLaunched.connected(jvm);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, pluginID, "Cannot attach to JVM.", e));
		} catch (IllegalConnectorArgumentsException e) {
			throw new CoreException(new Status(IStatus.ERROR, pluginID, "Cannot attach to JVM.", e));
		}
		

	}
	
	// tryListening
	public void tryConnecting() throws CoreException {
		VirtualMachineManager eclipseVMManager = org.eclipse.jdi.Bootstrap.virtualMachineManager();
		final ListeningConnector connector = VMLaunchHelper.findSocketListenConnector(eclipseVMManager);
		
		// TODO: maybe require some more initialization
		final Map<String, Connector.Argument> args = connector.defaultArguments();
		VMLaunchHelper.setArguments(args, null, port);
		
		try {
			connector.startListening(args); // returns listening address
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, pluginID, "Cannot connect to JVM.", e));
		} catch (IllegalConnectorArgumentsException e) {
			throw new CoreException(new Status(IStatus.ERROR, pluginID, "Cannot connect to JVM.", e));
		} 
		
		Thread thread = new Thread("Listen on socket for target VM.") {
			public void run()  {
				VirtualMachine jvm = null;
				try {
					jvm = connector.accept(args);
			        connector.stopListening(args);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalConnectorArgumentsException e) {
					e.printStackTrace();
				}
				jvmLaunched.connected(jvm);
			}
		};
		
		
		thread.start(); // start the listener thread
	}

	public static interface IJVMLaunched {
		public void connected(VirtualMachine jvm);
	}
}
