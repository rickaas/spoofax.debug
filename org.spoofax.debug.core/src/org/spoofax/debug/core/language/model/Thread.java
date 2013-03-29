package org.spoofax.debug.core.language.model;

import java.util.Stack;

import org.eclipse.debug.core.model.IStackFrame;
import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.java.events.ValueExtractor;
import org.spoofax.debug.core.eclipse.LIThread;

import com.sun.jdi.event.LocatableEvent;

public class Thread {

	private Stack<StackFrame> stackFrames = new Stack<StackFrame>();
	
	private String name;
	
	/**
	 * When suspended by a breapoint this points to the responsible breakpoint
	 */
	private AbstractBreakPoint breakpoint = null;
	
	private boolean isSuspended = false;
	
	/**
	 * When suspended this is the original java breakpoint
	 */
	private LocatableEvent event = null;
	
	public Thread(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void push(StackFrame stackFrame) {
		stackFrames.push(stackFrame);
	}
	
	public void pop() {
		stackFrames.pop();
	}
	
	public void step(SourceLocation newLocation) {
		// clear previous state
		this.breakpoint = null;
		//  setStepping(false);
		
		if (stackFrames.isEmpty()) throw new RuntimeException("Cannot step in thread that has no current stackframe.");
		stackFrames.peek().step(newLocation);
	}
	
	public void var(String varName) {
		// TODO: for now we do not support global vars...
		if (stackFrames.isEmpty()) throw new RuntimeException("Cannot introduce a var in thread that has no current stackframe.");
		stackFrames.peek().getFrameVarScope().setVar(varName, null);
	}

	public StackFrame current() {
		if (stackFrames.isEmpty()) { 
			return null; 
		} else {
			return stackFrames.peek();
		}
	}
	
	/**
	 * When the thread has a single stack (e.g. it is still in the main-method), then the depth is 1.
	 * @return
	 */
	public int getStackFrameDepth() {
		return stackFrames.size();
	}
	
	public IStackFrame[] getLIStackFrames(LIThread thread) {
		IStackFrame[] sfs = new IStackFrame[this.stackFrames.size()];
		for(int i = 0; i < stackFrames.size(); i++) {
		//for(int i = stackFrames.size()-1; i >= 0; i--) {
			IStackFrame sf = stackFrames.get(i).create(thread);
			// last stack in list should be first one in list
			int index = sfs.length - i - 1;
			sfs[index] = sf;
		}
		return sfs;
	}
	
	public void setSuspend(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}
	
	public boolean isSuspended() {
		return this.isSuspended;
	}

	public void suspendedBy(AbstractBreakPoint breakpoint) {
		this.breakpoint = breakpoint;
	}
	
	public AbstractBreakPoint getBreakpoint() {
		return this.breakpoint;
	}
	
	public void attachEvent(LocatableEvent event) {
		this.event = event;
	}
	
	public Value retrieveValue(int stackID, String varName) {
		if (event != null) {
			ValueExtractor ex = new ValueExtractor(event, this.name, stackID, varName);
			ex.extract();
			String s = ex.getStringValue();
			return new Value(s);
		} else {
			System.out.println("NO THREAD REFERENCE");
			return null;
		}
	}
}
