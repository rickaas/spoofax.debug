package org.spoofax.debug.core.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class LIStackFrame extends LIDebugElement implements IStackFrame {

	/**
	 * The name of the function.
	 */
	private String name;
	
	private org.spoofax.debug.core.language.model.StackFrame data;
	
	private LIThread thread;
	
	private List<String> varNames = new ArrayList<String>();
	
	/**
	 * Should be unique in each thread, IDs can be reused after a stacked is pop.
	 */
	private int stackID = -1;
	
	public LIStackFrame(int stackID, LIThread thread) {
		super(thread.target, thread.getLanguageID());
		this.stackID = stackID;
		this.thread = thread;
	}
	
	public void init(org.spoofax.debug.core.language.model.StackFrame data) {
		this.data = data;
		name = data.getName();
		// copy the values from the StackFrame to LIStackFrame
		// getting the values for each variable should be done via the VMContainer
		for(String varName : data.getFrameVarScope().getVarnames()) {
			varNames.add(varName);
		}
	}

	public boolean canStepInto() {
		return getThread().canStepInto();
	}

	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	public boolean isStepping() {
		return getThread().isStepping();
	}

	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	public boolean canResume() {
		return getThread().canResume();
	}

	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	public boolean isSuspended() {
		return getThread().isSuspended();
	}

	public void resume() throws DebugException {
		getThread().resume();
	}

	public void suspend() throws DebugException {
		getThread().suspend();
	}

	public boolean canTerminate() {
		return getThread().canTerminate();
	}

	public boolean isTerminated() {
		return getThread().isTerminated();
	}

	public void terminate() throws DebugException {
		getThread().terminate();
	}

	public IThread getThread() {
		return thread;
	}

	public IVariable[] getVariables() throws DebugException {
		if (this.varNames == null) return new IVariable[] {};
		
		IVariable[] vars = new LIVariable[this.varNames.size()];
		for(int i = 0; i < this.varNames.size(); i++) {
			IVariable var = new LIVariable(this, this.varNames.get(i));
			// TODO: extract value from VMContainer
			vars[i] = var;
		}
		return vars;
	}

	public boolean hasVariables() throws DebugException {
		if (this.varNames == null) {
			return false;
		} else {
			return this.varNames.size() != 0;
		}
	}

	public int getLineNumber() throws DebugException {
		if (data == null || data.currentLocation() == null) {
			return -1;
		} else {
			return data.currentLocation().getLocationInfo().getStart_line_num();
		}
	}

	public int getCharStart() throws DebugException {
		if (this.target.sourceOffsetConvertor == null) return -1;
		
		String filename = data.currentLocation().getFilename();
		int linenumber = data.currentLocation().getLocationInfo().getStart_line_num();
		int lineOffset = this.target.sourceOffsetConvertor.getLineOffset(filename, linenumber);
		int charStart = -1;
		if (lineOffset > -1) {
			charStart = lineOffset + data.currentLocation().getLocationInfo().getStart_token_pos();
		}
		return charStart;
	}

	public int getCharEnd() throws DebugException {
		if (this.target.sourceOffsetConvertor == null) return -1;
		
		String filename = data.currentLocation().getFilename();
		int linenumber = data.currentLocation().getLocationInfo().getEnd_line_num();
		int lineOffset = this.target.sourceOffsetConvertor.getLineOffset(filename, linenumber);
		int charEnd = -1;
		if (lineOffset > -1) {
			// correct by one, for including/excluding of by one error
			charEnd = lineOffset + data.currentLocation().getLocationInfo().getEnd_token_pos() + 1;
		}
		return charEnd;
	}

	/**
	 * Name of the function
	 */
	public String getName() throws DebugException {
		return name;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[] {};
	}

	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}
	
	public LIValue retrieveValue(String varName) {
		String threadName = this.thread.getShortName();
		org.spoofax.debug.core.language.model.Value value = this.target.getVMContainer().retrieveValue(threadName, this.stackID, varName);
		return new LIValue(target, value.getStringValue());
	}
	
	
	/**
	 * The location of the source file
	 * @return
	 */
	public String getSourceLocation() {
		return data.currentLocation().getFilename();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LIStackFrame)) return false;
		if (obj == this) return true;
		LIStackFrame other = (LIStackFrame) obj;
		try {
			// name
			if (!this.getName().equals(other.getName())) return false;
			// stackframe data
			if (!this.data.equals(other.data)) return false;
			// thread
		} catch (DebugException e) {
			return false;
		}
		return true;
	}
}
