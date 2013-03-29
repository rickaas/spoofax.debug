package org.spoofax.debug.core.control.java.events;

import java.util.ArrayList;
import java.util.List;

import org.spoofax.debug.core.control.java.DebugEventRequestInstaller;
import org.spoofax.debug.interfaces.java.ISuspendInClassEntry;

import com.sun.jdi.ClassType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.EventRequestManager;

/**
 * Fired when a class is prepared. If the prepared class is in the list of suspend target we must install a breakpoint in that class.
 * A breakpoint in such a class corresponds to a DSL event.
 * @author rlindeman
 *
 */
public class ClassPrepareHandler implements IJavaEventHandler {

	private List<ISuspendInClassEntry> suspendTargets = new ArrayList<ISuspendInClassEntry>();
	
	public ClassPrepareHandler(List<ISuspendInClassEntry> suspendTargets)
	{
		this.suspendTargets = suspendTargets;
	}
	
	@SuppressWarnings("rawtypes")
	public void installLoadedClasses(VirtualMachine vm) {
		List foo = vm.allClasses();
		for(int i = 0; i < foo.size(); i++) {
			Object o = foo.get(i); // ClassTypeImpl
			if (o instanceof ClassType) {
				ClassType clazz = (ClassType) o;
				requiresBreakpointInstall(vm, clazz);
			}
		}
		
	}
	
	/**
	 * Install the breakpoint in the clazz when it corresponds to an ISuspendInClassEntry.
	 * @param vm
	 * @param clazz
	 */
	private void requiresBreakpointInstall(VirtualMachine vm, ClassType clazz) {
		EventRequestManager mgr = vm.eventRequestManager();
		
		for(ISuspendInClassEntry suspendInClass : this.suspendTargets) {
			if (suspendInClass.getFullClassName().equals(clazz.name())) {
				int l = suspendInClass.getBreakpointLineNumber();
				DebugEventRequestInstaller.createBreakpointEntryRequest(mgr, clazz, l, suspendInClass.getEventType());
			}
		}
	}
	
	/**
	 * If we have a ISuspendInClassEntry that targets the class that is being prepared then we should install a breakpoint.
	 * @param event
	 */
	public void handleClassPrepare(ClassPrepareEvent event) {
		EventRequestManager mgr = event.virtualMachine().eventRequestManager();
		
		//String name = event.referenceType().name();
		if (event.referenceType() instanceof ClassType) {
			ClassType clazz = (ClassType) event.referenceType();
			requiresBreakpointInstall(event.virtualMachine(), clazz);
		}
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof ClassPrepareEvent) {
			this.handleClassPrepare((ClassPrepareEvent)event);
		}
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}
}
