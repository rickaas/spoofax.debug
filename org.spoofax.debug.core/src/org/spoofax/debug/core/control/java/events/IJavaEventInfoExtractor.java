package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.interfaces.info.IEventInfo;


/**
 * Extract debug information from a java virtual machine
 * @author rlindeman
 *
 */
public interface IJavaEventInfoExtractor<T extends com.sun.jdi.event.Event> {

	IEventInfo extract(T event);
}
