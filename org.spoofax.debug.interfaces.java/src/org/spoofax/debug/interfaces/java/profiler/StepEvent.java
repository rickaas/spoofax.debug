package org.spoofax.debug.interfaces.java.profiler;

public class StepEvent extends org.spoofax.debug.interfaces.java.StepEvent {

	public StepEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().start("step"); // event handling duration
		
		super.execute();
		
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().stop("step"); // event handling duration
	}
}
