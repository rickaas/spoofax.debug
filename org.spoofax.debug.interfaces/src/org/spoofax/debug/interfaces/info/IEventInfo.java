package org.spoofax.debug.interfaces.info;

public interface IEventInfo {
	String getFilename();
	String getLocation();
	String getEventType();
	String getThreadName();
}
