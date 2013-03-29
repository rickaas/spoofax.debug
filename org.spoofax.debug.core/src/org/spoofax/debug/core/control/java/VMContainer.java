package org.spoofax.debug.core.control.java;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.spoofax.debug.core.control.IVMContainer;
import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.java.events.IJavaEventHandler;
import org.spoofax.debug.core.language.events.IVMMonitor;
import org.spoofax.debug.core.language.events.VMMonitorStub;
import org.spoofax.debug.core.language.model.StepController;
import org.spoofax.debug.core.language.model.Value;
import org.spoofax.debug.core.model.IProgramState;
import org.spoofax.debug.core.util.StreamRedirectThread;
import org.spoofax.debug.interfaces.info.IEventInfo;

import com.ibm.icu.text.MessageFormat;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.LocatableEvent;

/**
 * This is a wrapper around the Java Virtual Machine
 * @author rlindeman
 *
 */
public abstract class VMContainer implements IVMContainer {

	protected VirtualMachine vm = null;
	
	/**
	 * Thread transferring remote error stream to our error stream
	 */
	private StreamRedirectThread errThread = null;

	/**
	 * Thread transferring remote output stream to our output stream
	 */
	private StreamRedirectThread outThread = null;
	
	/**
	 * Receives the JDI events
	 */
	private EventThread eventThread = null;
	
	// Do we want to watch assignments to fields?
	private boolean watchFields = false;
	
	// forward event to this monitor
	protected IVMMonitor vmMonitor = null;
	

	
	// true when VM is suspended
	protected boolean isSuspended = false;
	
	// true when VM has terminated
	protected boolean isTerminated = false;
	
	// true when VM is stepping
	protected boolean isStepping = false;
	
	// true when we disconnected from the VM
	protected boolean isDisconnected = false;
	
	public VMContainer(VirtualMachine vm) {
		this.vm = vm; // the java vm
	}
	
	/**
	 * The stderr and stdout of the process will be captured
	 */
	public void redirectOutput() {
		Process process = this.vm.process();
		if (process != null)
		{
			// Copy target's output and error to our output and error.
			errThread = new StreamRedirectThread("error reader", process
					.getErrorStream(), System.err);
			outThread = new StreamRedirectThread("output reader", process
					.getInputStream(), System.out);
			errThread.start();
			outThread.start();
		}
		else
		{
			log("no process with vm");
		}
	}
	
	// Class patterns for which we don't want events
	private String[] excludes = { "java.*"
			, "javax.*"
			, "sun.*"
			, "com.sun.*"
			,"org.strategoxt.lang.terms.*"
			//, "org.strategoxt.*"
			//, "org.spoofax.*"
			//, "aterm.*"
		};
	
	/**
	 * Initializes the java debug-event listeners for the current virtual machine
	 */
	public void setupEventListeners() {
		int debugTraceMode = 0; // -dbgtrace 
		vm.setDebugTraceMode(debugTraceMode);
		this.eventThread = new EventThread(this/*, eventSpecManager, vmMonitor*/);
		this.eventThread.setEventRequests(watchFields); // install the debug events
		this.eventThread.start(); // only start when it is connected
	}
	
	/**
	 * This method blocks untill the stdout and stderr are processed
	 */
	public void runVM() {
		log("runVM start");
        vm.resume();

        // Shutdown begins when event thread terminates
        try {
        	if (eventThread != null)
        	{
        		eventThread.join();
        	}
        	if (errThread != null)
        	{
	            errThread.join(); // Make sure output is forwarded
	        	errThread.closeStream();
        	}
        	if (outThread != null)
        	{
	            outThread.join(); // before we exit
	            outThread.closeStream();
        	}
        } catch (InterruptedException exc) {
            // we don't interrupt
        }
        log("runVM end");
	}
	
	public void log(String message)
	{
		System.out.println(message);
	}
	
	public String[] getExcludes()
	{
		return excludes;
	}
	
	public abstract IJavaEventHandler getHandler(Class clazz);
	
	public VirtualMachine getVM() {
		return this.vm;
	}
	
	public abstract StepController getStepController();
	
	public abstract IProgramState getProgramState();
	
	public abstract boolean processDebugEvent(IEventInfo eventInfo);

	public void attachEvent(IEventInfo eventInfo, LocatableEvent event) {
		// we can use the ThreadReference to extract value from the Java VM.
		this.getProgramState().getThread(eventInfo.getThreadName()).attachEvent(event);
	}
	
	/**
	 * Returns whether this target is available to
	 * handle VM requests
	 */
	public boolean isAvailable() {
		// TODO: Implement isTerminating
		return !(isTerminated() /*|| isTerminating()*/ || isDisconnected());
	}
	
	// start terminate/resume/suspend
	
	@Override
	public boolean canTerminate() {
		return isAvailable();
	}

	@Override
	public boolean isTerminated() {
		return this.isTerminated;
	}

	@Override
	public void terminate() throws DebugException {
		isTerminated = true;
		isSuspended = false;
		
		if (this.vm != null)
		{
			this.isTerminated = true;
			vm.exit(1);
		}
		else
		{
			System.out.println("No VM!");
		}
	}

	@Override
	public boolean canResume() {
		return isAvailable() && isSuspended();
	}

	@Override
	public boolean canSuspend() {
		return isAvailable() && !isSuspended();
	}

	@Override
	public boolean isSuspended() {
		return isSuspended;
	}

	@Override
	public void resume() throws DebugException {
		// TODO: cannot resume a VM that has died
		this.isSuspended = false;
		this.resumed(); // TODO: or is this fired by a java debug event
		this.vm.resume();
	}

	@Override
	public void suspend() throws DebugException {
		// suspend at next event
		this.scheduleSuspend();
	}
	
	@Override
	public void disconnect() throws DebugException {
		try {
			disposeThreadHandler(); // TODO: clean up handlers?
			VirtualMachine vm = getVM();
			if (vm != null) {
				vm.dispose();
			}
		} catch (VMDisconnectedException e) {
			// if the VM disconnects while disconnecting, perform
			// normal disconnect handling
			disconnected();
		} catch (RuntimeException e) {
			throw e;
		}
		
		//this.isDisconnected = true;
		//this.vmMonitor.disconnected();
	};
	
	/**
	 * Allows for ThreadStartHandler to do clean up/disposal.
	 */
	private void disposeThreadHandler() {
		//ThreadStartHandler handler = getThreadStartHandler();
		//if (handler != null) {
		//	handler.deleteRequest();
		//}
	}
	
	public boolean supportsDisconnect() {
		return false;
	}
	
	@Override
	public boolean canDisconnect() {
		return supportsDisconnect() && isAvailable();
	}

	@Override
	public boolean isDisconnected() {
		return this.isDisconnected;
	}
	
	// Thread specific suspend/resume 
	

	public void resume(String threadName) throws DebugException {
		// TODO: cannot resume a VM that has died
		this.getProgramState().getThread(threadName).setSuspend(false);
		if (!this.hasSuspendedThread()) {
			this.isSuspended = false; // no suspended threads left
		}
		this.resumed(); // TODO: or is this fired by a java debug event
		this.vm.resume();
	}
	
	public void suspend(String threadName) throws DebugException {
		// TODO: suspend at next event
		this.scheduleSuspend();
	}
	
	public abstract void scheduleSuspend();
	
	// start IVMMonitor code
	
	public void setVMMonitor(IVMMonitor vmMonitor) {
		this.vmMonitor = vmMonitor;
	}
	
	protected IVMMonitor getVMMonitor() {
		if (this.vmMonitor == null) return new VMMonitorStub();
		return this.vmMonitor;
	}
	
	public void terminated() {
		this.isTerminated = true;
		this.vmMonitor.terminated();
	}
	
	public void started() {
		this.vmMonitor.started();
	}
	
	public void resumed() {
// resume can be caused by a step, or a normal resume		
		int detail = DebugEvent.UNSPECIFIED;
		this.vmMonitor.resumed(detail);
	}
	
	public void suspended(IEventInfo eventInfo) {
		this.vmMonitor.suspended(eventInfo);
	}
	
	public void stepped(IEventInfo eventInfo) {
		this.vmMonitor.stepped(eventInfo);
	}
	
	public void disconnected() {
		this.isDisconnected = true;
		this.isTerminated = true;
		this.vmMonitor.disconnected();
	}
	
	public void breakpointHit(IEventInfo eventInfo) {
		// A breakpoint was hit, lookup the breakpoint and notify the ui.
		AbstractBreakPoint breakpoint = this.getProgramState().getThread(eventInfo.getThreadName()).getBreakpoint();
		this.vmMonitor.breakpointHit(eventInfo, breakpoint);
	}

	// start breakpoint
	
	public abstract void addBreakpoint(AbstractBreakPoint breakpoint);

	public abstract void removeBreakpoint(AbstractBreakPoint breakpoint);

	public abstract void removeAllBreakpoints();
	
	/**
	 * Returns the LIValue
	 * @param threadName
	 * @param stackID
	 * @param varName
	 * @return
	 */
	public org.spoofax.debug.core.language.model.Value retrieveValue(String threadName, int stackID, String varName) {
		// TODO Auto-generated method stub
		org.spoofax.debug.core.language.model.Thread thread = this.getProgramState().getThread(threadName);
		Value v = thread.retrieveValue(stackID, varName);
		if (v == null) {
			v = new Value("No Value");
		}
		return v;
	}
	
	public boolean hasSuspendedThread() {
		org.spoofax.debug.core.language.model.Thread[] threads = this.getProgramState().getThreads();
		for(org.spoofax.debug.core.language.model.Thread t : threads) {
			if (t.isSuspended()) {
				return true;
			}
		}
		return false;
	}
}
