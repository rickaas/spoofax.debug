package org.spoofax.debug.interfaces.java.profiler;

public class VarEvent extends org.spoofax.debug.interfaces.java.VarEvent {

	public VarEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().start("var"); // event handling duration
		
		super.execute();
		
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().stop("var"); // event handling duration
	}
}
