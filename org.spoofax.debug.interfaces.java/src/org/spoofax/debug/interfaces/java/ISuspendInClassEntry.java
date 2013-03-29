package org.spoofax.debug.interfaces.java;

/**
 * This corresponds to a linenumber in a java library. When this line is hit it corresponds to a DSL debug event.
 * @author rlindeman
 *
 */
public interface ISuspendInClassEntry {

	String getFullClassName();
	
	int getBreakpointLineNumber();
	
	String getEventType();
}
