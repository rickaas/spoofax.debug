package org.spoofax.debug.interfaces.java;

import org.spoofax.debug.interfaces.events.IVarEvent;

public class VarEvent extends DebugEventBase implements IVarEvent {

	public static int BREAKPOINT_LINENUMBER = 13;
	
	public VarEvent(String eventInfo) {
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
		return VAR;
	}

}
