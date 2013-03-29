package org.spoofax.debug.core.control.java.events;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.LocatableEvent;

public class InfoExtractorBase {

	protected LocatableEvent event = null;
	
	public LocatableEvent getEvent() {
		return this.event;
	}
	
	/**
	 * The current com.sun.jdi.StackFrame.
	 */
	private StackFrame stackFrame = null;
	
	/**
	 * Returns the current com.sun.jdi.StackFrame
	 * @return
	 */
	protected StackFrame getStackFrame() {
		if (stackFrame == null)
		{
			LocatableEvent event = this.getEvent();
			if (event != null)
			{
				ThreadReference thread = event.thread();
				
				StackFrame fr = null;
				try {
					fr = thread.frame(0); // get current frame
				} catch (IncompatibleThreadStateException e) {
					e.printStackTrace(); // thread should be suspended
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace(); // invalid index
				}
				this.stackFrame = fr;
			}
		}
		return stackFrame;
	}
	
	/**
	 * An ObjectReference that is "this" in the current thread.
	 */
	protected ObjectReference thisObject = null;
	
	protected ObjectReference getThisObject() {
		if (thisObject == null) {
			this.thisObject = this.getStackFrame().thisObject();
		}
		return thisObject;
	}
	
	public void reset() {
		this.event = null;
		this.stackFrame = null;
	}
}
