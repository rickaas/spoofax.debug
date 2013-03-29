package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.core.control.java.VMContainer;
import org.spoofax.debug.interfaces.extractor.IDeserializeEventInfo;
import org.spoofax.debug.interfaces.info.IEventInfo;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;

/**
 * A breakpoint was hit in java code.
 * Extract DSL debug event info and update the program state.
 * @author rlindeman
 *
 */
public class BreakpointHandler implements IJavaEventHandler {
	
	/**
	 * Extract IEventInfo from a java breakpoint event.
	 */
	protected BreakpointEventInfoExtractor extractor;
	
	protected VMContainer vmContainer;
	
	public BreakpointHandler(VMContainer vmContainer, IDeserializeEventInfo deserializer) {
		this.vmContainer = vmContainer;
		this.extractor = new BreakpointEventInfoExtractor(deserializer);
	}
	
	public boolean handleBreakpoint(BreakpointEvent event) {
		extractor.reset();
		IEventInfo eventInfo = extractor.extract(event);
		
		boolean shouldSuspend = this.vmContainer.processDebugEvent(eventInfo);
		if (shouldSuspend) {
			this.vmContainer.attachEvent(eventInfo, event);
		}
		return shouldSuspend;
	}
	
	@Override
	public boolean handle(Event event) {
		return this.handleBreakpoint((BreakpointEvent) event);
	}

}
