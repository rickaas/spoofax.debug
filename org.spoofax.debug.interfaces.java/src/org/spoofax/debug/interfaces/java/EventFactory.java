package org.spoofax.debug.interfaces.java;


public class EventFactory implements IEventFactory {

	private DebugEventBase enterEvent;
	private DebugEventBase exitEvent;
	private DebugEventBase stepEvent;
	private DebugEventBase varEvent;
	
	public EventFactory() {
		this.enterEvent = new EnterEvent("");
		this.exitEvent = new ExitEvent("");
		this.stepEvent = new StepEvent("");
		this.varEvent = new VarEvent("");
	}
	
	@Override
	public DebugEventBase createEnter(String eventInfo) {
		//return new EnterEvent(eventInfo);
		this.enterEvent.setEventInfo(eventInfo);
		return enterEvent;
	}

	@Override
	public DebugEventBase createExit(String eventInfo) {
		//return new ExitEvent(eventInfo);
		this.exitEvent.setEventInfo(eventInfo);
		return exitEvent;

	}

	@Override
	public DebugEventBase createStep(String eventInfo) {
		//return new StepEvent(eventInfo);
		this.stepEvent.setEventInfo(eventInfo);
		return stepEvent;

	}

	@Override
	public DebugEventBase createVar(String eventInfo) {
		//return new VarEvent(eventInfo);
		this.varEvent.setEventInfo(eventInfo);
		return varEvent;
	}

}
