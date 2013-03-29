package org.spoofax.debug.core.control.java.events;

import com.sun.jdi.event.Event;


public interface IJavaEventHandler {

	public static final boolean SHOULD_SUSPEND = true;
	
	/**
	 * Take care of the Java event: extract DSL debug event data and determine if we should suspend.
	 * @param event
	 * @return
	 */
	boolean handle(Event event);
}
