package org.spoofax.debug.interfaces.java.trace;

public class EnterEvent extends org.spoofax.debug.interfaces.java.EnterEvent {

	public EnterEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		System.out.println("EnterEvent " + this.eventInfo);
		super.execute();
	}
}
