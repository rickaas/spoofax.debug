package org.spoofax.debug.interfaces.java.trace;

public class StepEvent extends org.spoofax.debug.interfaces.java.StepEvent {

	public StepEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		System.out.println("StepEvent  " + this.eventInfo);
		super.execute();
	}
}
