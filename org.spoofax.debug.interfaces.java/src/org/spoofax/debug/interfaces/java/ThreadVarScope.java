package org.spoofax.debug.interfaces.java;

import java.util.Stack;

/**
 * Each Thread gets its own variable scope.
 * Each method entry corresponds to an enter() which adds a new FrameVarScope to the stack,
 * a method exit results in a call to exit() and pops the top scope from the stack.
 * 
 * If there are no frame in this thread the active scope is the toplevel scope.
 * This could be used for global variables or when a code block is executed that does not have an enter/exit event.
 * E.g. in Stratego we could have a lifted strategy that is executed in a non-instrumented strategy.
 * @author rlindeman
 *
 */
public class ThreadVarScope {

	private Stack<FrameVarScope> frames = new Stack<FrameVarScope>();
	
	/**
	 * The top level scope is active when there are no frames in this thread.
	 */
	private FrameVarScope toplevel = new FrameVarScope();
	
	public ThreadVarScope() {
		
	}
	
	public void enter() {
		frames.push(new FrameVarScope());
	}
	

	public void exit() {
		frames.pop();
	}
	
	/**
	 * Returns the active FrameVarScope in this thread.
	 * When there are no frames in this thread we return a top level scope.
	 * @return
	 */
	public FrameVarScope getActiveScope() {
		if (this.frames.isEmpty()) return this.toplevel;
		return this.frames.peek();
	}

	/**
	 * Returns null if the frameIndex is greater than the current number of available frames.
	 * @param frameIndex
	 * @return
	 */
	public FrameVarScope get(int frameIndex) {
		if (this.frames.size() < frameIndex) return null;
		
		return this.frames.get(frameIndex);
	}
	
	public FrameVarScope getTopLevelScope() {
		return this.toplevel;
	}
}
