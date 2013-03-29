package org.spoofax.debug.core.model;

import org.spoofax.debug.core.control.events.AbstractBreakPoint;

/**
 * Captures the program state of a running DSL program
 * @author rlindeman
 *
 */
public interface IProgramState {

	org.spoofax.debug.core.language.model.Thread getThread(String name);
	
	org.spoofax.debug.core.language.model.Thread[] getThreads();
	
	/**
	 * Lookup the thread and convert the current StackFrame to a breakpoint.
	 * @param threadName
	 * @return
	 */
	AbstractBreakPoint createBreakpointFromThreadState(String threadName);
}
