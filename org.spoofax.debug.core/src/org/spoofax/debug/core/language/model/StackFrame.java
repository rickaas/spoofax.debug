package org.spoofax.debug.core.language.model;

import org.eclipse.debug.core.model.IStackFrame;
import org.spoofax.debug.core.eclipse.LIStackFrame;
import org.spoofax.debug.core.eclipse.LIThread;
import org.spoofax.debug.interfaces.java.FrameVarScope;

public class StackFrame {

	// TODO: A stack also have stacked scopes for variables
	
	// The name of the function
	private String name;
	
	private SourceLocation currentLocation = null;;
	
	private SourceLocation definitionLocation;
	
	private org.spoofax.debug.core.language.model.Thread thread;
	
	private org.spoofax.debug.core.language.model.StackFrame parent;
	
	private FrameVarScope frameVarScope;
	
	public StackFrame(String name, SourceLocation definitionLocation, Thread thread, StackFrame parent) {
		this.name = name;
		this.definitionLocation = definitionLocation;
		this.currentLocation = definitionLocation;
		this.thread = thread;
		this.parent = parent;
		this.frameVarScope = new FrameVarScope();
	}
	
	public void step(SourceLocation newLocation) {
		currentLocation = newLocation;
	}
	
	protected void setParent(StackFrame parent) {
		this.parent = parent;
	}
	
	protected void setThread(Thread thread) {
		this.thread = thread;
	}
	
	public org.spoofax.debug.core.language.model.StackFrame getParent() {
		return parent;
	}
	
	public org.spoofax.debug.core.language.model.Thread getThread() {
		return thread;
	}
	
	public SourceLocation currentLocation() {
		return this.currentLocation;
	}
	
	public SourceLocation definitionLocation() {
		return this.definitionLocation;
	}
	
	/**
	 * Returns the name of the function.
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public IStackFrame create(LIThread thread) {
		LIStackFrame stackframe = new LIStackFrame(getDepth(), thread);
		stackframe.init(this);
		return stackframe;
	}
	
	public FrameVarScope getFrameVarScope() {
		return this.frameVarScope;
	}
	
	protected int getDepth() {
		if (this.parent == null) {
			return 0;
		} else {
			return parent.getDepth() + 1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof StackFrame)) return false;
		if (obj == this) return true;
		
		StackFrame other = (StackFrame) obj;
		if (!this.getName().equals(other.getName())) return false;
		
		// currentLocation
		if (!this.currentLocation().equals(other.currentLocation())) return false;
		if (!this.definitionLocation().equals(other.definitionLocation())) return false;
		// definitionLocation
		
		return true;
	}
}
