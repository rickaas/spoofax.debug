package org.spoofax.debug.interfaces.java.trace;

public class VarEvent extends org.spoofax.debug.interfaces.java.VarEvent {

	public VarEvent(String eventInfo) {
		super(eventInfo);
	}

	@Override
	public void execute() {
		System.out.println("VarEvent   " + this.eventInfo);
		super.execute();
	}
}
