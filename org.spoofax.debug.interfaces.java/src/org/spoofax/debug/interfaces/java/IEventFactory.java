package org.spoofax.debug.interfaces.java;


public interface IEventFactory {

	DebugEventBase createEnter(String eventInfo);
	DebugEventBase createExit(String eventInfo);
	DebugEventBase createStep(String eventInfo);
	DebugEventBase createVar(String eventInfo);
}
