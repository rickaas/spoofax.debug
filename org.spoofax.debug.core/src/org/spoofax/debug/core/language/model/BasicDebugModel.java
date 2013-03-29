package org.spoofax.debug.core.language.model;

import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.events.GlobalBreakPointList;
import org.spoofax.debug.core.model.IProgramState;
import org.spoofax.debug.interfaces.info.IEventInfo;
import org.spoofax.debug.interfaces.info.IFunctionEventInfo;
import org.spoofax.debug.interfaces.info.IVarEventInfo;

public class BasicDebugModel {

	private GlobalBreakPointList breakpointList = new GlobalBreakPointList();
	
	private IProgramState programState;
	
	private boolean hasPendingSuspend = false;
	
	public BasicDebugModel(IProgramState programState) {
		this.programState = programState;
	}
	
	public void processDebugEvent(IEventInfo eventInfo) { 
		if (eventInfo.getEventType().equals("enter")) {
			enter((IFunctionEventInfo)eventInfo);
		} else if (eventInfo.getEventType().equals("exit")) {
			exit((IFunctionEventInfo)eventInfo);
		} else if (eventInfo.getEventType().equals("step")) {
			step(eventInfo);
		} else if (eventInfo.getEventType().equals("var")) {
			var((IVarEventInfo) eventInfo);
		}
	}
	
	private void enter(IFunctionEventInfo eventInfo) {
		SourceLocation functionSource = new SourceLocation(eventInfo);
		Thread thread = programState.getThread(eventInfo.getThreadName());
		StackFrame parent = thread.current();
		org.spoofax.debug.core.language.model.StackFrame frame = new StackFrame(eventInfo.getFunctionName(), functionSource, thread, parent);
		thread.push(frame);
	}
	
	private void exit(IFunctionEventInfo eventInfo) {
		Thread thread = programState.getThread(eventInfo.getThreadName());
		thread.pop();
	}
	
	private void step(IEventInfo eventInfo) {
		Thread thread = programState.getThread(eventInfo.getThreadName());
		SourceLocation newLocation = new SourceLocation(eventInfo);
		thread.step(newLocation);
	}
	
	private void var(IVarEventInfo eventInfo) {
		Thread thread = programState.getThread(eventInfo.getThreadName());
		String varName = eventInfo.getVarname();;
		thread.var(varName);
	}
	
//	public boolean shouldSuspend(IEventInfo eventInfo) {
//		if (this.hasPendingSuspend) {
//			// always suspend when we have a pending suspend
//			this.hasPendingSuspend = false;
//			return true;
//		} else {
//			// check for breakpoints, convert the current state
//			String threadName = eventInfo.getThreadName();
//			AbstractBreakPoint current = this.programState.createBreakpointFromThreadState(threadName);
//			return this.match(current);
//		}
//	}
	
	public boolean hasPendingSuspend() {
		if (this.hasPendingSuspend) {
			// always suspend when we have a pending suspend
			this.hasPendingSuspend = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean breakpointHit(IEventInfo eventInfo) {
		// check for breakpoints, convert the current state
		String threadName = eventInfo.getThreadName();
		AbstractBreakPoint current = this.programState.createBreakpointFromThreadState(threadName);
		if (eventInfo.getEventType().equals("step")) {
			if (this.match(current)) {
				this.programState.getThread(threadName).suspendedBy(current);
				return true;
			} else {
				return false;
			}
		} else {
			// received an var/enter/exit event, ignore breakpoints
			return false;
		}
	}
	
	public void scheduleSuspend() {
		hasPendingSuspend = true;
	}
	
	// start block with stepping
	
//	public boolean isStepping()
//	{
//		return isStepping;
//	}
//	
//	public boolean canStepInto() {
//		return canStep();
//	}
//
//	public boolean canStepOver() {
//		return canStep();
//	}
//
//	public boolean canStepReturn() {
//		return canStep();
//	}
//	
//	/**
//	 * Returns whether this thread is in a valid state to
//	 * step.
//	 * 
//	 * @return whether this thread is in a valid state to
//	 * step
//	 */
//	protected boolean canStep() {
//		return isSuspended()
//			// && (!isPerformingEvaluation() || isInvokingMethod()) // TODO: implement "perform evaluation"
//			// && !isSuspendVoteInProgress() // TODO:  (conditional breakpoints, etc.).
//			&& !isStepping()
//			//&& getTopStackFrame() != null // just use the frame level
//			&& this.getProgramState().getThread("main").getStackFrameDepth() > 0 // the main-method has depth=1
//			// && !getJavaDebugTarget().isPerformingHotCodeReplace() // TODO: implement hot code replace
//			;
//	}
//	
//
//	
//	public void stepInto()
//	{
//		if (canStepInto())
//		{
//			log("STEP INTO");
//			// stop at the first possible s-enter/r-enter event
//			// if the current statement is not a call to another method, we can only step over
//			this.eventSpecManager.setStepInto(this.getStrategoState());
//			this.resumeVM();
//		}
//	}
//	
//	public void stepOver()
//	{
//		if (this.canStepOver())
//		{
//			log("STEP OVER");
//			// get the thread that is suspended, stratego programs are single threaded, so we always know which thread we need.
//			// just save the step info in the EventSpecManager, if stratego becomes multi-threaded, step info needs to be saved per Thread
//			this.eventSpecManager.setStepOver(this.getStrategoState());
//			// stop at the next s-step that is in the same stackframe as the current one
//			// if the current StackFrame exists (s-exit or r-exit) continue at the returning stackframe
//			this.resume();
//		}
//	}
//	
//	public void stepReturn()
//	{
//		if (this.canStepReturn())
//		{
//			log("STEP RETURN");
//			// continue until the current stackframe fires an s-exit or r-exit event.
//			// we should stop at the next s-step in the parent stackframe.
//			this.eventSpecManager.setStepReturn(this.getStrategoState());
//			this.resumeVM();
//		}
//	}
	
	// breakpoint handling
	/**
	 * Returns true if the given BreakPoint should suspend the vm.
	 * BreakPoint current should not be a virtual breakpoint (should not contain wildcards).
	 * @param current
	 * @return
	 */
	public boolean match(AbstractBreakPoint current) {
		if (current == null) {
			return false;
		} else if (current.isVirtual()) {
			// The current breakpoint cannot be virtual.
			return false;
		}
		
		for(AbstractBreakPoint definedBreakPoint : this.breakpointList.getBreakPoints()) {
			// defined breakpoint may have wildcards
			boolean isMatch = definedBreakPoint.match(current);
			if (isMatch) {
				return true;
			}
		}
		return false;
	}

	public void add(AbstractBreakPoint bp) {
		this.breakpointList.add(bp);
	}
	
	public boolean remove(AbstractBreakPoint bp) {
		return this.breakpointList.remove(bp);
	}
	
	/**
	 * Removes all breakpoints.
	 */
	public void clear() {
		this.breakpointList.clear();
	}
}
