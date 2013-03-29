package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.core.control.java.VMContainer;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMStartEvent;

public class VMStartHandler implements IJavaEventHandler {

	protected VMContainer vmContainer;

	public VMStartHandler(VMContainer vmContainer) {
		this.vmContainer = vmContainer;
	}
	
	public void handleVMStart(VMStartEvent event) {
		vmContainer.started();
	}
	
	@Override
	public boolean handle(Event event) {
		handleVMStart((VMStartEvent) event);
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}
}
