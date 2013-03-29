package org.spoofax.debug.core.language.model;

import java.util.HashMap;
import java.util.Map;

import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.events.LineBreakPoint;
import org.spoofax.debug.core.model.IProgramState;

public class BasicProgramState implements IProgramState {

	private Map<String, Thread> threads = new HashMap<String, Thread>();
	@Override
	public Thread getThread(String name) {
		if (!threads.containsKey(name)) {
			// create a new thread
			threads.put(name, new Thread(name));
		}
		return threads.get(name);
	}
	
	@Override
	public AbstractBreakPoint createBreakpointFromThreadState(String threadName) {
		Thread thread = this.getThread(threadName);
		StackFrame stackFrame = thread.current();
		if (stackFrame != null) {
			if (stackFrame.currentLocation() != null) {
				LineBreakPoint breakpoint = new LineBreakPoint(stackFrame.currentLocation());
				return breakpoint;
			}
		}
		return null;
	}

	@Override
	public Thread[] getThreads() {
		return this.threads.values().toArray(new Thread[this.threads.size()]);
	}

}
