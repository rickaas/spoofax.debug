package org.spoofax.debug.interfaces.java.trace;

import org.spoofax.debug.interfaces.java.DebugEventBase;
import org.spoofax.debug.interfaces.java.IEventFactory;

public class EventFactoryTrace implements IEventFactory {

	@Override
	public DebugEventBase createEnter(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.trace.EnterEvent(eventInfo);
	}

	@Override
	public DebugEventBase createExit(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.trace.ExitEvent(eventInfo);
	}

	@Override
	public DebugEventBase createStep(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.trace.StepEvent(eventInfo);
	}

	@Override
	public DebugEventBase createVar(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.trace.VarEvent(eventInfo);
	}
	
}
