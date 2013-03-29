package org.spoofax.debug.core.language.events;

import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.interfaces.info.IEventInfo;

/**
 * Implement the IVMMonitor to receive events from the VMContainer.
 * The events can be used to update the Eclipse UI.
 * @author rlindeman
 *
 */
public interface IVMMonitor {

	/**
	 * This method is called when the VM is started.
	 */
	void started();
	
	/**
	 * This method is called when the VM is terminated
	 */
	void terminated();
	
	void resumed(int detail);

	/**
	 * Fired when the client requested the suspend
	 * @param eventInfo
	 */
	void suspended(IEventInfo eventInfo);
	
	void breakpointHit(IEventInfo eventInfo, AbstractBreakPoint breakpoint);

	void stepped(IEventInfo eventInfo);
	
	/**
	 * Called when the debugger is disconnected from the VM.
	 */
	void disconnected();
	
}
