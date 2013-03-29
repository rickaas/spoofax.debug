/*
 * @(#)EventThread.java	1.6 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-2001 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package org.spoofax.debug.core.control.java;

import org.spoofax.debug.core.control.java.events.IJavaEventHandler;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.EventRequest;

/**
 * This class processes incoming JDI events and displays them
 * 
 * @version @(#) EventThread.java 1.6 05/11/17 13:07:51
 * @author Robert Field
 */
public class EventThread extends Thread {
	
	private final VMContainer vm; // Running VM
	private final String[] excludes; // Packages to exclude

	private boolean connected = false; // Connected to VM
	private boolean vmDied = false; // VMDeath occurred

	// Maps ThreadReference to ThreadTrace instances
//	private Map<ThreadReference, ThreadEventHandler> traceMap = new HashMap<ThreadReference, ThreadEventHandler>();

	// keeps track of all event specifications (e.g. breakpoints)
	//private EventSpecManager eventSpecManager = null;
	
	// changes to the vm are reported to the monitor (e.g. a thread was suspended because a breakpoint was hit)
	//private VMMonitor vmMonitor = null;
	
	// returns the current state of the running stratego program
	// e.g. current linenumber
	//private StrategoState strategoState = null;
	
	
	public EventThread(VMContainer vm/*, EventSpecManager eventSpecManager, VMMonitor vmMonitor*/) {
		super("event-handler");
		if (vm != null)
		{
			// we are connected to a vm
			this.connected = true;
		}
		this.vm = vm;
		this.excludes = vm.getExcludes();
//		this.eventSpecManager = eventSpecManager;
//		this.vmMonitor = vmMonitor;
//		this.strategoState = new StrategoState();
	}

	/**
	 * Run the event handling thread. As long as we are connected, get event
	 * sets off the queue and dispatch the events within them.
	 */
	public void run() {
		System.out.println("Running EventThread");
		EventQueue queue = vm.getVM().eventQueue();
		while (connected) {
			try {
				EventSet eventSet = queue.remove();
				boolean suspendThread = false;
				
				EventIterator it = eventSet.eventIterator();
				while (it.hasNext()) {
					try {
						suspendThread = handleEvent(it.nextEvent());
					} catch (Exception e) {
						System.out.println("Exception in event handling code.");
						e.printStackTrace();
					}
				}
				// handled all events in this set
				if (!suspendThread) {
					eventSet.resume();
				} else if (eventSet.suspendPolicy() == EventRequest.SUSPEND_ALL) {
					// The entire JVM was suspended...
					log("All threads suspended");
					// all threads are suspended
                    //setCurrentThread(eventSet);
                    //notifier.vmInterrupted();
                }
				else
				{
					log("Not all threads are suspended");
				}
			} catch (InterruptedException exc) {
				// Ignore
			} catch (VMDisconnectedException discExc) {
				System.out.println("VMDisconnectedException");
				System.out.println(discExc.getMessage());
				//System.out.println("Cause:");
				//discExc.getCause().printStackTrace();
				handleDisconnectedException();
				break;
			}
		}
	}

	/**
	 * Create the desired event requests, and enable them so that we will get
	 * events.
	 * 
	 * @param watchFields
	 *            Do we want to watch assignments to fields
	 */
	void setEventRequests(boolean watchFields) {
		DebugEventRequestInstaller.installDebugEventRequests(this.vm.getVM(), watchFields, this.excludes);
	}

	/**
	 * Dispatch incoming events
	 * 
	 * Returns true if the thread should stay suspended (we hit a breakpoint!)
	 * Returns false if the thread should resume
	 */
	private boolean handleEvent(Event event) {
		//System.out.println("Event " + event);
		if (event instanceof ExceptionEvent) {
			return exceptionEvent((ExceptionEvent) event);
		} else if (event instanceof ModificationWatchpointEvent) {
			return fieldWatchEvent((ModificationWatchpointEvent) event);
		} else if (event instanceof MethodEntryEvent) {
			return methodEntryEvent((MethodEntryEvent) event);
		} else if (event instanceof MethodExitEvent) {
			return methodExitEvent((MethodExitEvent) event);
		} else if (event instanceof StepEvent) {
			return stepEvent((StepEvent) event);
		} else if (event instanceof ThreadDeathEvent) {
			return threadDeathEvent((ThreadDeathEvent) event);
		} else if (event instanceof ClassPrepareEvent) {
			return classPrepareEvent((ClassPrepareEvent) event);
		} else if (event instanceof VMStartEvent) {
			return vmStartEvent((VMStartEvent) event);
		} else if (event instanceof VMDeathEvent) {
			return vmDeathEvent((VMDeathEvent) event);
		} else if (event instanceof VMDisconnectEvent) {
			return vmDisconnectEvent((VMDisconnectEvent) event);
		} else if (event instanceof BreakpointEvent) {
			// TODO: instead of the MethodEntry/Exit events which are _VERY_ time consuming!!
			return breakpointEvent((BreakpointEvent) event);
		} else {
			System.out.println(event.getClass().toString());
			//ThreadStartEvent
			// 
			//throw new Error("Unexpected event type");
			return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
		}
		
		//System.out.println("handleEvent - EXIT");
	}

	/***
	 * A VMDisconnectedException has happened while dealing with another event.
	 * We need to flush the event queue, dealing only with exit events (VMDeath,
	 * VMDisconnect) so that we terminate correctly.
	 */
	synchronized void handleDisconnectedException() {
		EventQueue queue = vm.getVM().eventQueue();
		while (connected) {
			try {
				EventSet eventSet = queue.remove();
				EventIterator iter = eventSet.eventIterator();
				while (iter.hasNext()) {
					Event event = iter.nextEvent();
					if (event instanceof VMDeathEvent) {
						vmDeathEvent((VMDeathEvent) event);
					} else if (event instanceof VMDisconnectEvent) {
						vmDisconnectEvent((VMDisconnectEvent) event);
					}
				}
				eventSet.resume(); // Resume the VM
			} catch (InterruptedException exc) {
				// ignore
				System.out.println("InterruptedException");
				exc.printStackTrace();
			} catch (com.sun.jdi.VMDisconnectedException exc) {
				System.out.println();
			}
		}
	}

	private boolean vmStartEvent(VMStartEvent event) {
		log("-------------------------------- VM started");
		if (this.vm.getHandler(VMStartEvent.class) != null)
		{
			IJavaEventHandler handler = this.vm.getHandler(VMStartEvent.class);
			handler.handle(event);
		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	// Forward event for thread specific processing
	private boolean methodEntryEvent(MethodEntryEvent event) {
//		if (this.vm.getHandler(MethodEntryEvent.class) != null)
//		{
//			IJavaEventHandler<MethodEntryEvent> handler = this.vm.getHandler(MethodEntryEvent.class);
//			handler.handle(event);
//		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	// Forward event for thread specific processing
	private boolean methodExitEvent(MethodExitEvent event) {
//		if (this.vm.getHandler(MethodExitEvent.class) != null)
//		{
//			IJavaEventHandler<MethodExitEvent> handler = this.vm.getHandler(MethodExitEvent.class);
//			handler.handle(event);
//		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	// Forward event for thread specific processing
	private boolean stepEvent(StepEvent event) {
//		if (this.vm.getHandler(StepEvent.class) != null)
//		{
//			IJavaEventHandler<StepEvent> handler = this.vm.getHandler(StepEvent.class);
//			handler.handle(event);
//		}

//		// Step to exception catch
//		EventRequestManager mgr = event.virtualMachine().eventRequestManager();
//		mgr.deleteEventRequest(event.request());
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	// Forward event for thread specific processing
	private boolean fieldWatchEvent(ModificationWatchpointEvent event) {
//		if (this.vm.getHandler(ModificationWatchpointEvent.class) != null)
//		{
//			IJavaEventHandler<ModificationWatchpointEvent> handler = this.vm.getHandler(ModificationWatchpointEvent.class);
//			handler.handle(event);
//		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	private boolean threadDeathEvent(ThreadDeathEvent event) {
		log("-------------------------------- THREAD DEATH");
		if (this.vm.getHandler(ThreadDeathEvent.class) != null)
		{
			IJavaEventHandler handler = this.vm.getHandler(ThreadDeathEvent.class);
			handler.handle(event);
		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	/**
	 * A new class has been loaded. 
	 * If the loaded class requires a breakpoint set it. 
	 * Set watchpoints on each of its fields.
	 * 
	 * Example of a class that requires a breakpoint is org.spoofax.debug.java.library.events.DebugEventBase
	 */
	private boolean classPrepareEvent(ClassPrepareEvent event) {

		//String name = event.referenceType().name();
		if (this.vm.getHandler(ClassPrepareEvent.class) != null)
		{
			IJavaEventHandler handler = this.vm.getHandler(ClassPrepareEvent.class);
			handler.handle(event);
		}

		
		// TODO: if watchFields is true, for now skip
		/*
		List<Field> fields = event.referenceType().visibleFields();
		for (Iterator<Field> it = fields.iterator(); it.hasNext();) {
			Field field = (Field) it.next();
			ModificationWatchpointRequest req = mgr
					.createModificationWatchpointRequest(field);
			for (int i = 0; i < excludes.length; ++i) {
				req.addClassExclusionFilter(excludes[i]);
			}
			req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
			req.enable();
		}*/
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	private boolean exceptionEvent(ExceptionEvent event) {
//		// Step to the catch
//		EventRequestManager mgr = event.virtualMachine().eventRequestManager();
//		StepRequest req = mgr.createStepRequest(event.thread(), StepRequest.STEP_MIN,
//				StepRequest.STEP_INTO);
//		req.addCountFilter(1); // next step only
//		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//		req.enable();
//		
//		//System.out.println("EXCEPTION: " + event.exception().toString());
//		ThreadEventHandler trace = (ThreadEventHandler) traceMap.get(event.thread());
//		if (trace != null) { // only want threads we care about
//			trace.exceptionEvent(event); // Forward event
//		}
//		
//		try {
//			int count = event.thread().frameCount();
//			if (count > 0)
//			{
//				com.sun.jdi.StackFrame sf = event.thread().frame(0);
//				@SuppressWarnings("rawtypes")
//				java.util.List visVars = sf.visibleVariables();
//				if (visVars != null && visVars.size() > 0)
//				{
//					com.sun.jdi.LocalVariable lv = (com.sun.jdi.LocalVariable) visVars.get(0);
//					com.sun.jdi.Value val = sf.getValue(lv);
//					System.out.println("VAL: " + val);
//				}
//				//java.util.List list = event.thread().frames();
//				//System.out.println("exception");
//			}
//		} catch (com.sun.jdi.IncompatibleThreadStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (com.sun.jdi.AbsentInformationException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
		
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	public boolean vmDeathEvent(VMDeathEvent event) {
		log("-------------------------------- VM DEATH");
		vmDied = true;
		if (this.vm.getHandler(VMDeathEvent.class) != null)
		{
			IJavaEventHandler handler = this.vm.getHandler(VMDeathEvent.class);
			return handler.handle(event);
		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}

	public boolean vmDisconnectEvent(VMDisconnectEvent event) {
		log("-------------------------------- VM DISCONNECT");
		connected = false;
		if (!vmDied) {
			// The application has been disconnected
			if (this.vm.getHandler(VMDisconnectEvent.class) != null)
			{
				IJavaEventHandler handler = this.vm.getHandler(VMDisconnectEvent.class);
				return handler.handle(event);
			}
		}
		return !IJavaEventHandler.SHOULD_SUSPEND; // resume thread
	}
	
	public boolean breakpointEvent(BreakpointEvent event)
	{
		if (this.vm.getHandler(BreakpointEvent.class) != null)
		{
			IJavaEventHandler handler = this.vm.getHandler(BreakpointEvent.class);
			return handler.handle(event);
		}
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}
	
	public boolean getVMDied()
	{
		return this.vmDied;
	}
	
	public boolean getConnected()
	{
		return this.connected;
	}
	
	
	private void log(String message)
	{
		System.out.println(message);
	}

}
