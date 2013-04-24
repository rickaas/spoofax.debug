package org.spoofax.debug.interfaces.java.profiler;

public class EnterEvent extends org.spoofax.debug.interfaces.java.EnterEvent {
	
	public EnterEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().start("enter"); // event handling duration

		FrameDuration.instance().start(eventInfo); // stackframe duration
		
		super.execute();
		if (EventFactoryProfiler.TrackEventHandlingDuration) EventHandlingDuration.instance().stop("enter"); // event handling duration
	}
}
