package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.core.control.java.VMContainer;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMDisconnectEvent;

public class VMDisconnectHandler implements IJavaEventHandler {

	protected VMContainer vmContainer;

	public VMDisconnectHandler(VMContainer vmContainer) {
		this.vmContainer = vmContainer;
	}
	
	public void handleVMDisconnect(VMDisconnectEvent event) {
		vmContainer.disconnected();
	}
	
	@Override
	public boolean handle(Event event) {
		handleVMDisconnect((VMDisconnectEvent) event);
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}

}
