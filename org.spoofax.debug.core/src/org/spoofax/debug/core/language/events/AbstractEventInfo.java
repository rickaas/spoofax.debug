package org.spoofax.debug.core.language.events;

import org.spoofax.debug.interfaces.info.IEventInfo;

public abstract class AbstractEventInfo implements IEventInfo {

	protected String filename;
	protected String location;
	protected String threadName;
	
	public AbstractEventInfo(String threadName, String filename, String location) {
		this.filename = filename;
		this.location = location;
		this.threadName = threadName;
	}
	
	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getLocation() {
		return location;
	}
	
	@Override
	public String getThreadName() {
		return threadName;
	}

	@Override
	public abstract String getEventType();

}
