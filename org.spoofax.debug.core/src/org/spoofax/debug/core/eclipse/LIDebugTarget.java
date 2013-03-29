package org.spoofax.debug.core.eclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.debug.core.model.IThread;
import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.events.LineBreakPoint;
import org.spoofax.debug.core.control.java.VMContainer;
import org.spoofax.debug.core.control.java.VMContainerStub;
import org.spoofax.debug.core.control.java.VMLaunchHelper;
import org.spoofax.debug.core.language.events.IVMMonitor;
import org.spoofax.debug.interfaces.info.IEventInfo;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;

/**
 * A debug target is a debuggable execution context. For example, a debug target
 * may represent a debuggable process or a virtual machine. A debug target is
 * the root of the debug element hierarchy. A debug target contains threads.
 * Minimally, a debug target supports the following:
 * <ul>
 * <li>terminate
 * <li>suspend/resume
 * <li>breakpoints
 * <li>disconnect
 * </ul>
 * <p>
 * Generally, launching a debug session results in the creation of a debug
 * target. Launching is a client responsibility, as is debug target creation.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @see ITerminate
 * @see ISuspendResume
 * @see IBreakpointListener
 * @see IDisconnect
 * @see IMemoryBlockRetrieval
 * @see org.eclipse.debug.core.ILaunch
 */
public class LIDebugTarget extends LIDebugElement implements IDebugTarget,
		IVMMonitor {

	// containing launch object
	private ILaunch launch;

	/**
	 * The threads for this target. This should be an array
	 */
	private LIThread thread;

	/**
	 * the array only contains the single LIThread (referenced by thread)
	 * because we target single-threaded programs for now...
	 */
	private IThread[] threads;

	private String debugTargetName;

	private VMContainer vmContainer;

	private final VMLaunchHelper vmLaunchHelper;

	/**
	 * Helper class to convert line-based character offsets to file-based
	 * character offsets.
	 */
	protected LISourceOffsetConvertor sourceOffsetConvertor;

	/**
	 * The vm should be suspended.
	 * 
	 * @param javaTarget
	 * @param languageID
	 * @param launch
	 * @param vm
	 */
	public LIDebugTarget(IDebugTarget javaTarget, String languageID,
			ILaunch launch, VirtualMachine vm) {
		super(null, languageID);

		// RL: needed because of the final modifier...
		VirtualMachineManager eclipseVMManager = org.eclipse.jdi.Bootstrap
				.virtualMachineManager();
		this.vmLaunchHelper = new VMLaunchHelper(eclipseVMManager, "LAUNCH");

		this.launch = launch;
		// running threads
		thread = new LIThread(this, languageID, "main");
		threads = new IThread[] { thread };

		this.initSourceConvertor();

		// DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
		// DebugPlugin.getDefault().getBreakpointManager().addBreakpointManagerListener(this);
		// DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
		// already connected to VM
		connectedToVM(vm);
	}

	// This one launches the VM by itself
	public LIDebugTarget(String languageID, ILaunch launch, final String port) {
		super(null, languageID); // super requires an IDebugTarget, but "this"
									// is the target and we cannot use this in a
									// constructor.
		this.launch = launch;
		// running threads
		thread = new LIThread(this, languageID, "main");
		threads = new IThread[] { thread };

		VirtualMachineManager eclipseVMManager = org.eclipse.jdi.Bootstrap
				.virtualMachineManager();
		this.vmLaunchHelper = new VMLaunchHelper(eclipseVMManager, "LAUNCH");

		this.initSourceConvertor();

		// VirtualMachine vm = this.setupAttacher(port); // when suspended at
		// this line the debug target has time to setup the socket
		// TODO: Use SocketListen (wait for VM to connect) instead of
		// SocketAttach (connect to existing VM)

		Thread thread = new Thread("Listen on socket for target VM at port: "
				+ port) {
			public void run() {
				VirtualMachine t_vm = null;
				try {
					t_vm = vmLaunchHelper.setupListener(port);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connectedToVM(t_vm);
			}
		};

		// TODO: maybe require some more initialization

		thread.start(); // start the listener thread
	}

	private IDebugTarget javaTarget;

	/**
	 * The virtual machine has been initialized, now attach all debug related
	 * stuff.
	 * 
	 * @param vm
	 */
	private void connectedToVM(VirtualMachine vm) {
		if (vm != null) {
			// RL: two target are shown but they are not in sync... lets look at
			// CDT plugin, they use this to debug multi-core
			// boolean allowTerminate = false;
			// boolean allowDisconnect = false;
			// boolean resume = true;
			// resume is optional
			// IDebugTarget target = JDIDebugModel.newDebugTarget(this.launch,
			// vm, "JavaAlng", null, allowTerminate, allowDisconnect, resume);
			// javaTarget = target;
			// this.launch.addDebugTarget(target);

			System.out.println("Creating VM container...");
			vmContainer = this.debugServiceFactory.createVMContainer(vm);
			vmContainer.setVMMonitor(this);
			// manager.setVirtualMachine(vm);
			System.out.println("init listeners");
			vmContainer.setupEventListeners();
			System.out.println("Redirect");
			vmContainer.redirectOutput();
			try {
				vmContainer.resume();
			} catch (DebugException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("RUN");
			// manager.runVM();
			// System.out.println("Done");
		}
	}

	private void initSourceConvertor() {
		// program name
		ILaunchConfiguration configuration = this.launch
				.getLaunchConfiguration();
		String program = null;
		try {
			program = configuration.getAttribute(this.debugServiceFactory
					.getLIConstants().getProgram(), (String) null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(program));
		String project = file.getProject().getName();
		this.sourceOffsetConvertor = new LISourceOffsetConvertor(project);
	}

	public IDebugTarget getDebugTarget() {
		// overrides LIDebugElement
		return this;
	}

	// LIDebugElement.getLaunch() is deferred to LIDebugTarget.getLaunch().
	public ILaunch getLaunch() {
		return this.launch;
	}

	protected VMContainer getVMContainer() {
		if (this.vmContainer == null)
			return new VMContainerStub();
		return this.vmContainer;
	}

	// start ITerminate

	public boolean canTerminate() {
		return this.getVMContainer().canTerminate();
	}

	public boolean isTerminated() {
		return this.getVMContainer().isTerminated();
	}

	public void terminate() throws DebugException {
		this.getVMContainer().terminate();
	}

	// end ITerminate

	// start ISuspendResume

	public boolean canResume() {
		return this.getVMContainer().canResume();
	}

	public boolean canSuspend() {
		return this.getVMContainer().canSuspend();
	}

	public boolean isSuspended() {
		return this.getVMContainer().isSuspended();
	}

	public void resume() throws DebugException {
		this.getVMContainer().resume();
	}

	public void suspend() throws DebugException {
		this.getVMContainer().suspend();
	}

	// end ISuspendResume
	
	/**
	 * Returns whether this target is available to
	 * handle VM requests
	 */
	public boolean isAvailable() {
		return !(isTerminated()/* || isTerminating()*/ || isDisconnected());
	}
	
	// start IBreakpointListener

	/**
	 * Notifies this listener that the given breakpoint has been added to the
	 * breakpoint manager.
	 * 
	 * @param breakpoint
	 *            the added breakpoint
	 * @since 2.0
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (!isAvailable()) {
			return;
		}
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					// only add the breakpoint to the debugger when the
					// breakpoint is enabled
					int linenumber = breakpoint.getMarker().getAttribute(
							IMarker.LINE_NUMBER, -1);
					// TODO: get marker language type or get language from the
					// resource
					IResource r = breakpoint.getMarker().getResource();
					String pRel = r.getProjectRelativePath().toOSString();
					String filename = pRel;
					if (linenumber > 0) {
						// only linenumbers greater than 0 are valid as
						// linenumber is 1-based
						// convert the Eclipse breakpoint to an internal
						// representation of breakpoints
						AbstractBreakPoint bp = createBreakpoint(filename,
								linenumber);
						if (bp != null) {
							this.getVMContainer().addBreakpoint(bp);
						}
					}
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public IBreakpoint findIBreakpoint(String filename, int lineNumber) {
		IBreakpoint[] breakpoints = DebugPlugin
				.getDefault()
				.getBreakpointManager()
				.getBreakpoints(
						this.debugServiceFactory.getLIConstants()
								.getDebugModel());
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint)) {

				try {
					if (breakpoint.isEnabled()) {
						// only add the breakpoint to the debugger when the
						// breakpoint is enabled
						int l = breakpoint.getMarker().getAttribute(
								IMarker.LINE_NUMBER, -1);
						// TODO: get marker language type or get language from
						// the resource
						IResource r = breakpoint.getMarker().getResource();
						String location = r.getProjectRelativePath()
								.toOSString();
						if (l > 0) {
							// only linenumbers greater than 0 are valid as
							// linenumber is 1-based
							if (location.equals(filename) && lineNumber == l) {
								return breakpoint;
							}
						}
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private AbstractBreakPoint createBreakpoint(String filename, int lineNumber) {
		AbstractBreakPoint breakpoint = new LineBreakPoint(filename, lineNumber);
		return breakpoint;
	}

	/**
	 * Notifies this listener that the given breakpoint has been removed from
	 * the breakpoint manager. If the given breakpoint has been removed because
	 * it has been deleted, the associated marker delta is also provided.
	 * 
	 * @param breakpoint
	 *            the removed breakpoint
	 * @param delta
	 *            the associated marker delta, or <code>null</code> when the
	 *            breakpoint is removed from the breakpoint manager without
	 *            being deleted
	 * 
	 * @see org.eclipse.core.resources.IMarkerDelta
	 * @since 2.0
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (!isAvailable()) {
			return;
		}
		if (supportsBreakpoint(breakpoint)) {
			// convert IBreakpoint to AbstractBreakPoint Breakpoint
			int linenumber = breakpoint.getMarker().getAttribute(
					IMarker.LINE_NUMBER, -1);
			IResource r = breakpoint.getMarker().getResource();
			String pRel = r.getProjectRelativePath().toOSString();
			String filename = pRel;
			if (linenumber > 0) {
				// only linenumbers greater than 0 are valid as linenumber is
				// 1-based
				AbstractBreakPoint bp = createBreakpoint(filename, linenumber);
				if (bp != null) {
					this.getVMContainer().removeBreakpoint(bp);
				}
			}

		}
	}

	/**
	 * Notifies this listener that an attribute of the given breakpoint has
	 * changed, as described by the delta.
	 * 
	 * @param breakpoint
	 *            the changed breakpoint
	 * @param delta
	 *            the marker delta that describes the changes with the marker
	 *            associated with the given breakpoint, or <code>null</code>
	 *            when the breakpoint change does not generate a marker delta
	 * 
	 * @see org.eclipse.core.resources.IMarkerDelta
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					breakpointAdded(breakpoint);
				} else {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// end IBreakpointListener

	// start IDisconnect

	/**
	 * Returns whether this element can currently disconnect.
	 * 
	 * RL: we do not support disconnecting...
	 */
	public boolean canDisconnect() {
		return this.getVMContainer().canDisconnect();
	}

	public void disconnect() throws DebugException {
		this.getVMContainer().disconnect();
	}

	public boolean isDisconnected() {
		return this.getVMContainer().isDisconnected();
	}

	// end IDisconnect

	// start IMemoryBlockRetrieval

	/**
	 * Returns whether this debug target supports the retrieval of memory
	 * blocks.
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return null;
	}

	// end IMemoryBlockRetrieval

	// Start IDebugTarget interface

	/**
	 * Returns the system process associated with this debug target.
	 * 
	 * RL: I think this can be null for debug target that use a JVM.
	 */
	public IProcess getProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the threads contained in this debug target. An empty collection
	 * is returned if this debug target contains no threads.
	 */
	public IThread[] getThreads() throws DebugException {
		return this.threads;
	}

	/**
	 * Returns whether this debug target currently contains any threads.
	 */
	public boolean hasThreads() throws DebugException {
		return true;
	}

	/**
	 * Returns the name of this debug target. Name format is debug model
	 * specific, and should be specified by a debug model.
	 * 
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		if (debugTargetName == null) {
			debugTargetName = "LI Program";
			try {
				String attr = this.debugServiceFactory.getLIConstants()
						.getProgram();
				debugTargetName = getLaunch().getLaunchConfiguration()
						.getAttribute(attr, "LI Program 2");
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return debugTargetName;
	}

	/**
	 * Returns true whether this target can install the given breakpoint. We
	 * have to make sure the language ids match
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(
				this.debugServiceFactory.getLIConstants().getDebugModel())) {
			// TODO: JDIDebugTarget implements this as <code>return breakpoint
			// instanceof IJavaBreakpoint;</code>
			return true;
		} else {
			return false;
		}
	}

	// End IDebugTarget interface

	// start IVMMonitor interface

	/**
	 * Notification we have connected to the VM and it has started. Resume the
	 * VM.
	 */
	public void started() {
		fireCreationEvent();
		installDeferredBreakpoints();
		try {
			resume();
		} catch (DebugException e) {
		}
	}

	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager.
	 */
	public void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin
				.getDefault()
				.getBreakpointManager()
				.getBreakpoints(
						this.debugServiceFactory.getLIConstants()
								.getDebugModel());
		for (int i = 0; i < breakpoints.length; i++) {
			breakpointAdded(breakpoints[i]);
		}
	}

	/**
	 * Called when this debug target terminates.
	 */
	public void terminated() {
		DebugPlugin.getDefault().getBreakpointManager()
				.removeBreakpointListener(this);
		fireTerminateEvent();
	}

	/**
	 * Notification the target has resumed for the given reason
	 * 
	 * @param detail
	 *            reason for the resume
	 */
	public void resumed(int detail) {
		thread.setSuspended(false);
		thread.resumedByVM();

		thread.fireResumeEvent(detail);


		for (IDebugTarget target : this.getLaunch().getDebugTargets()) {
			if (target != this) {
				try {
					target.resume();
				} catch (DebugException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Notification the target has suspended for the given reason
	 * 
	 * @param detail
	 *            reason for the suspend
	 */
	public void suspended(IEventInfo eventInfo) {
		int detail = DebugEvent.CLIENT_REQUEST; // client hit 'suspend'
		thread.setSuspended(true);
		thread.setStepping(false);
		thread.suspendedBy(null);
		thread.fireSuspendEvent(detail);
	}

	public void breakpointHit(IEventInfo eventInfo,
			AbstractBreakPoint breakpoint) {
		IBreakpoint bp = this.findIBreakpoint(breakpoint.getFilename(),
				breakpoint.getLineNumber());
		int detail = DebugEvent.BREAKPOINT; // breakpoint was hit
		thread.setSuspended(true);
		thread.setStepping(false);
		thread.suspendedBy(bp);
		thread.fireSuspendEvent(detail);
	}

	public void stepped(IEventInfo eventInfo) {
		int detail = DebugEvent.STEP_END;
		thread.setSuspended(true);
		thread.setStepping(true);
		thread.suspendedBy(null);
		thread.fireSuspendEvent(detail);
	}

	public void disconnected() {
		cleanup();
		DebugPlugin.getDefault().getBreakpointManager()
				.removeBreakpointListener(this);
		fireTerminateEvent();
	}

	// end IVMMonitor interface

	/**
	 * Cleans up the internal state of this debug target as a result of a
	 * session ending with a VM (as a result of a disconnect or termination of
	 * the VM).
	 * <p>
	 * All threads are removed from this target. This target is removed as a
	 * breakpoint listener, and all breakpoints are removed from this target.
	 * </p>
	 */
	protected void cleanup() {
		removeAllThreads();
		DebugPlugin plugin = DebugPlugin.getDefault();
		plugin.getBreakpointManager().removeBreakpointListener(this);
		// plugin.getLaunchManager().removeLaunchListener(this);
		// plugin.getBreakpointManager().removeBreakpointManagerListener(this);
		// plugin.removeDebugEventListener(this);
		removeAllBreakpoints();
	}

	/**
	 * Removes all threads from this target's collection of threads, firing a
	 * terminate event for each.
	 */
	protected void removeAllThreads() {
		Iterator<IThread> itr = getThreadIterator();
		while (itr.hasNext()) {
			LIThread child = (LIThread) itr.next();
			child.terminated();
		}
		synchronized (this.threads) {
			this.thread = null;
			this.threads = new LIThread[0];
		}
	}

	/**
	 * Returns an iterator over the collection of threads. The returned iterator
	 * is made on a copy of the thread list so that it is thread safe. This
	 * method should always be used instead of getThreadList().iterator()
	 * 
	 * @return an iterator over the collection of threads
	 */
	private Iterator<IThread> getThreadIterator() {
		List<IThread> threadList = new ArrayList<IThread>();
		synchronized (this.threads) {
			for(IThread thread : this.threads) {
				threadList.add(thread);
			}
		}
		return threadList.iterator();
	}

	/**
	 * Removes all breakpoints from this target, such that each breakpoint can
	 * update its install count. This target's collection of breakpoints is
	 * cleared.
	 */
	protected void removeAllBreakpoints() {
		this.getVMContainer().removeAllBreakpoints();
	}

}
