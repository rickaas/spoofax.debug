package org.spoofax.debug.core.control.java;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.spoofax.debug.core.util.FileUtil;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.VMStartException;

public class VMLaunchHelper {

	//  Do we want to watch assignments to fields
    private boolean watchFields = false;
    
    //public String classpath = "";
    
    /*
    public final String defaultClasspath = "/home/rlindeman/workspace/short-examples/bin" +
    		":/home/rlindeman/workspace/strj-dbg-generated/bin";
	
    public final String defaultJars = DebugSessionSettings.strategoxtjar + ":" + DebugSessionSettings.libstrategodebuglib + ":" + DebugSessionSettings.strjdebugruntime;    
    */
    
    /**
     * The VMManager is used to create VirtualMachines.
     */
	private VirtualMachineManager vmManager = null;
    
	/**
	 * Determines what Launching connector to use when starting a VM.
	 */
	private String connectorType = null;
	
	public VMLaunchHelper(VirtualMachineManager vmManager, String connectorType) {
		this.vmManager = vmManager;
		this.connectorType = connectorType;
	}

	/**
	 * Find a com.sun.jdi.CommandLineLaunch connector.
	 * 
	 * @param vmManager
	 * @return
	 */
	public static LaunchingConnector findCLLaunchingConnector(VirtualMachineManager vmManager) {
		@SuppressWarnings("unchecked")
		List<Connector> connectors = vmManager.allConnectors();

		Iterator<Connector> iter = connectors.iterator();
		while (iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
				return (LaunchingConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.CommandLineLaunch connector");
	}

	/**
	 * Find a com.sun.jdi.SocketAttach connector.
	 * 
	 * @param vmManager
	 * @return
	 */
	public static AttachingConnector findSocketAttachConnector(VirtualMachineManager vmManager)
	{
		@SuppressWarnings("unchecked")
		List<Connector> connectors = vmManager.allConnectors();
		
		Iterator<Connector> iter = connectors.iterator();
		while (iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.SocketAttach")) {
				return (AttachingConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.SocketAttach connector");
	}
	
	public static ListeningConnector findSocketListenConnector(VirtualMachineManager vmManager)
	{
		@SuppressWarnings("unchecked")
		List<Connector> connectors = vmManager.allConnectors();
		
		Iterator<Connector> iter = connectors.iterator();
		while(iter.hasNext()) {
			Connector connector = (Connector) iter.next();
			if (connector.name().equals("com.sun.jdi.SocketListen")) {
				return (ListeningConnector) connector;
			}
		}
		throw new Error("No com.sun.jdi.SocketListen connector");
	}
	
	private Map<String, ? extends Connector.Argument> connectorAttachArguments(AttachingConnector connector, String mainArgs)
	{
		@SuppressWarnings("unchecked")
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("port").setValue("8000");
		
		return arguments;
	}
	
	private Map<String, ? extends Connector.Argument> connectorListenArguments(ListeningConnector connector, String mainArgs)
	{
		@SuppressWarnings("unchecked")
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("port").setValue("8000");
		if (arguments.containsKey("localAddress"))
		{
			arguments.get("localAddress").setValue("localhost");
		}
		
		
		return arguments;
	}
	
	public final static String JAVA_VM_OPT = "-Xss8m -Xms256m -Xmx1024m -XX:MaxPermSize=256m -server";
	
	// set arguments for the connector
	/**
	 * Return the launching connector's arguments.
	 */
	private Map<String, ? extends Connector.Argument> connectorLaunchArguments(LaunchingConnector connector, String mainArgs) {
		@SuppressWarnings("unchecked")
		Map<String, ? extends Connector.Argument> arguments = connector.defaultArguments();
		Connector.Argument mainArg = (Connector.Argument) arguments.get("main");
		if (mainArg == null) {
			throw new Error("Bad launching connector");
		}
		mainArg.setValue(mainArgs);
		

		Connector.Argument opts = (Connector.Argument) arguments.get("options");
		String classpath = FileUtil.convertIPathToClasspath(this.getClasspaths());
		String optionValue = JAVA_VM_OPT + " -classpath " + classpath;
		opts.setValue(optionValue);
		
		Connector.Argument susp = (Connector.Argument) arguments.get("suspend");
		susp.setValue("true");
		
		if (watchFields) {
			// We need a VM that supports watchpoints
			Connector.Argument optionArg = (Connector.Argument) arguments.get("options");
			if (optionArg == null) {
				throw new Error("Bad launching connector");
			}
			optionArg.setValue("-classic");
		}
		return arguments;
	}
	
	private VirtualMachine attachToTarget(String mainArgs)
	{
		AttachingConnector connector = null;
		connector = VMLaunchHelper.findSocketAttachConnector(this.vmManager);
		
		Map<String, ? extends Connector.Argument> arguments = connectorAttachArguments(connector, mainArgs);
		try {
			return connector.attach(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to attach target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		}
	}
	
	/**
	 * target vm should have server=n in the -Xrunjdwp or
	 * -Xrunjdwp:transport=dt_socket,address=8000
	 * @param mainArgs
	 * @return
	 */
	private VirtualMachine listenForTarget(String mainArgs){
		ListeningConnector connector = null;
		connector = VMLaunchHelper.findSocketListenConnector(this.vmManager);
		
		Map<String, ? extends Connector.Argument> arguments = connectorListenArguments(connector, mainArgs);
		try {
			return connector.accept(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to listen for target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		}
	}
	
	public static void setArguments(Map<String, Connector.Argument> args, String host, String port) {
		
		Connector.Argument pidArgument = args.get("port");
		if (pidArgument == null) {
			throw new IllegalStateException();
		}
		pidArgument.setValue(port);
		
		if (host != null) {
			Connector.Argument hostArgument = args.get("hostname");
			if (hostArgument == null) {
				throw new IllegalStateException();
			}
			hostArgument.setValue(host);
		}
		// TODO: set timeout?
//		Connector.IntegerArgument timeoutArg= (Connector.IntegerArgument) map.get("timeout"); //$NON-NLS-1$
//		if (timeoutArg != null) {
//			int timeout = JavaRuntime.getPreferences().getInt(JavaRuntime.PREF_CONNECT_TIMEOUT);
//			timeoutArg.setValue(timeout);
//		}
	}

	
	public VirtualMachine setupListener(String port) throws CoreException
	{
		//VirtualMachineManager vmManager = org.eclipse.jdi.Bootstrap.virtualMachineManager(); // eclipse vmManager
		//VirtualMachineManager m2 = Bootstrap.virtualMachineManager(); // com.sun.jdi.BootStrap, this method returns null
		
		System.out.println("LISTEN@"+port);
		ListeningConnector listenConnector = VMLaunchHelper.findSocketListenConnector(this.vmManager);	
		
		VirtualMachine vm = null;
		try {
			vm = VMLaunchHelper.listen(listenConnector, port);
		} catch (IllegalConnectorArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//abort("Unable to connect to Stratego VM", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//abort("Unable to connect to Stratego VM", e);
		}
		//System.out.println("Connected to VM! " + vm.description());
		return vm;
	}
	
	@SuppressWarnings("unchecked")
	public static VirtualMachine listen(ListeningConnector connector, String port) throws IOException, IllegalConnectorArgumentsException
	{
		VirtualMachine vm = null;
		Map<String, Connector.Argument> args = connector.defaultArguments();
		Connector.Argument pidArgument = args.get("port");
		if (pidArgument == null) {
			throw new IllegalStateException();
		}
		pidArgument.setValue(port);

		connector.startListening(args); // returns listening address
        vm = connector.accept(args);
        connector.stopListening(args);
        
		return vm;
	}
	
	// attach to target vm (target vm runs the stratego program)
	/**
	 * Launch target VM. Forward target's output and error.
	 */
	private VirtualMachine launchTarget(String mainArgs) {
		LaunchingConnector connector = null;
		
		connector = VMLaunchHelper.findCLLaunchingConnector(this.vmManager);
		Map<String, ? extends Connector.Argument> arguments = connectorLaunchArguments(connector, mainArgs);
		try {
			return connector.launch(arguments);
		} catch (IOException exc) {
			throw new Error("Unable to launch target VM: " + exc);
		} catch (IllegalConnectorArgumentsException exc) {
			throw new Error("Internal error: " + exc);
		} catch (VMStartException exc) {
			throw new Error("Target VM failed to initialize: "
					+ exc.getMessage());
		}
	}
	
	/**
	 * Returns a VirtualMachine that is initialized with the connecterType set in the constructor of VMLaunchHelper.
	 * @param mainArgs
	 * @return
	 */
	public VirtualMachine getTargetVM(String mainArgs)
	{
		if (this.connectorType == null || "LAUNCH".equals(this.connectorType)) {
			return this.launchTarget(mainArgs);
		} else if ("LISTEN".equals(this.connectorType)) {
			return this.listenForTarget(mainArgs);
		} else if ("ATTACH".equals(this.connectorType)) { // server=y, start program before the debugger
			return this.attachToTarget(mainArgs);
		} else {
			return null;
		}
		
	}
	
	/**
	 * The VM will use this classpath.
	 */
	private List<IPath> classpaths = null;
	
	/**
	 * Set the classpath that will be used when launching the VM. 
	 * 
	 * All required jars should be on the classpath (strategoxt.jar, runtime.jar, application-directory)
	 * @param cp
	 */
	public void setClasspaths(List<IPath> classpaths) {
		this.classpaths = classpaths;
	}
	
	/**
	 * Returns all classpaths for the VM.
	 * @return
	 */
	public List<IPath> getClasspaths() {
		return classpaths;
	}
	
}
