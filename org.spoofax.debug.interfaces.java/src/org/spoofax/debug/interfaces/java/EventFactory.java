package org.spoofax.debug.interfaces.java;


public class EventFactory implements IEventFactory {

	@Override
	public DebugEventBase createEnter(String eventInfo) {
		return new EnterEvent(eventInfo);
	}

	@Override
	public DebugEventBase createExit(String eventInfo) {
		return new ExitEvent(eventInfo);
	}

	@Override
	public DebugEventBase createStep(String eventInfo) {
		return new StepEvent(eventInfo);
	}

	@Override
	public DebugEventBase createVar(String eventInfo) {
		return new VarEvent(eventInfo);
	}

}
