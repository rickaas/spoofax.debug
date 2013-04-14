package org.spoofax.debug.interfaces.java.trace;

public class ExitEvent extends org.spoofax.debug.interfaces.java.ExitEvent {

	public ExitEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		System.out.println("ExitEvent  " + this.eventInfo);
		super.execute();
	}
}
