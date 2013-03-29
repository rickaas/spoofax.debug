package org.spoofax.debug.interfaces.java;

import org.spoofax.debug.interfaces.events.IStepEvent;

public class StepEvent extends DebugEventBase implements IStepEvent {

	public static int BREAKPOINT_LINENUMBER = 15;
	
	public StepEvent(String eventInfo) {
		this.eventInfo = eventInfo;
	}

	@Override
	public void execute() {
		nothing();
	}
	
	@Override
	public String getFullClassName() {
		return this.getClass().getName();
	}

	@Override
	public int getBreakpointLineNumber() {
		return BREAKPOINT_LINENUMBER;
	}

	@Override
	public String getEventType() {
		return STEP;
	}

}
