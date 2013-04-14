package org.spoofax.debug.interfaces.java;

public class IEventFactoryInstance {

	public static IEventFactory INSTANCE;
	
	static {
		INSTANCE = new EventFactory();
	}
}
