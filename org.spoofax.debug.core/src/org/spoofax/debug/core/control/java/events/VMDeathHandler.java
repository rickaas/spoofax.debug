package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.core.control.java.VMContainer;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMDeathEvent;

public class VMDeathHandler implements IJavaEventHandler {

	protected VMContainer vmContainer;
	
	public VMDeathHandler(VMContainer vmContainer) {
		this.vmContainer = vmContainer;
	}
	
	public void handleVMDeath(VMDeathEvent event) {
		vmContainer.terminated();
	}
	
	@Override
	public boolean handle(Event event) {
		handleVMDeath((VMDeathEvent) event);
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}

}
