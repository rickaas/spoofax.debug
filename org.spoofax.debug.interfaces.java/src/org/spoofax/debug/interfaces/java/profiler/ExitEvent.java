package org.spoofax.debug.interfaces.java.profiler;

public class ExitEvent extends org.spoofax.debug.interfaces.java.ExitEvent {

	public ExitEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().start("exit"); // event handling duration
		
		FrameDuration.instance().stop(eventInfo); // stackframe duration
		
		super.execute();
		
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().stop("exit"); // event handling duration
	}
}
