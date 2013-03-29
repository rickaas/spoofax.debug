package org.spoofax.debug.interfaces.java;

import java.util.Stack;

/**
 * Each Thread gets its own variable scope.
 * Each method entry corresponds to an enter() which adds a new FrameVarScope to the stack,
 * a method exit results in a call to exit() and pops the top scope from the stack.
 * @author rlindeman
 *
 */
public class ThreadVarScope {

	private Stack<FrameVarScope> frames = new Stack<FrameVarScope>();
	
	public ThreadVarScope() {
		
	}
	
	public void enter() {
		frames.push(new FrameVarScope());
	}
	

	public void exit() {
		frames.pop();
	}
	
	public FrameVarScope getActiveScope() {
		if (this.frames.isEmpty()) return null;
		return this.frames.peek();
	}

	public FrameVarScope get(int frameIndex) {
		if (this.frames.size() < frameIndex) return null;
		
		return this.frames.get(frameIndex);
	}
}
