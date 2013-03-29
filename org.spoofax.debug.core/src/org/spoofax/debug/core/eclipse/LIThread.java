package org.spoofax.debug.core.eclipse;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class LIThread extends LIDebugElement implements IThread {

	/**
	 * Breakpoint this thread is suspended at or <code>null</code>
	 * if none.
	 */
	private IBreakpoint breakpoint;
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean isStepping = false;
	
	/**
	 * True when this thread is suspended
	 */
	private boolean isSuspended = false;
	
	private String name;
	
	public LIThread(LIDebugTarget target, String languageID, String name) {
		super(target, languageID);
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
		//return this.target.canResume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isSuspended() && !isTerminated();
		//return this.target.canSuspend();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return isSuspended && !isTerminated();
		//return this.target.isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		//this.target.resume();
		this.target.getVMContainer().resume(this.name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		//this.target.suspend();
		this.target.getVMContainer().suspend(this.name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return !isTerminated() && isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return !isTerminated() && isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return !isTerminated() && isSuspended();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return isStepping;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
		this.target.getVMContainer().getStepController().stepInto(this.name);
		this.resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		this.target.getVMContainer().getStepController().stepOver(this.name);
		this.resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
		this.target.getVMContainer().getStepController().stepReturn(this.name);
		this.resume();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
		//return this.target.canTerminate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
		//return this.target.isTerminated();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		//sendRequest(requestTermination());
		this.target.terminate();
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			IStackFrame[] sfs = this.target.getVMContainer().getProgramState().getThread(this.name).getLIStackFrames(this);
//			return ((LIDebugTarget)getDebugTarget()).getStackFrames();
			return sfs;
		} else {
			return new IStackFrame[0];
		}
	}

	public boolean hasStackFrames() throws DebugException {
		return isSuspended();
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0) {
			return frames[0];
		}
		return null;
	}

	/**
	 * returns: Thread [name]
	 */
	public String getName() throws DebugException {
		return "Thread [" + this.name + "]";
	}
	
	public String getShortName() {
		return this.name;
	}

	public IBreakpoint[] getBreakpoints() {
		if (breakpoint == null) {
			return new IBreakpoint[0];
		}
		return new IBreakpoint[]{breakpoint};
	}
	
	public void suspendedBy(IBreakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}
	
	/**
	 * Notifies this thread that is about to be resumed due
	 * to a VM resume.
	 */
	protected synchronized void resumedByVM() {
		this.setSuspended(false);
		this.suspendedBy(null);
	}
	
	/**
	 * Indicates if the reason for suspending this thread
	 * is a breakpoint hit. 
	 * 
	 * @return suspension caused by breakpoint
	 */
	public boolean isSuspendedByBreakpoint() {
		//org.spoofax.debug.core.language.model.Thread thread = this.target.getVMContainer().getProgramState().getThread(this.name);
		return breakpoint != null && isSuspended();
	}
	
	public void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	public void setStepping(boolean isStepping) {
		this.isStepping = isStepping;
	}
	
	// =============================
	
	/**
	 * Notification this thread has terminated - update state
	 * and fire a terminate event.
	 */
	protected void terminated() {
		//setTerminated(true);
		//setRunning(false);
		fireTerminateEvent();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LIThread)) return false;
		if (obj == this) return true;
		LIThread other = (LIThread) obj;
		try {
			if (!other.getName().equals(this.getName())) return false;
		} catch (DebugException e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
