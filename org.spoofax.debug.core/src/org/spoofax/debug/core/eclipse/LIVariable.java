package org.spoofax.debug.core.eclipse;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class LIVariable extends LIDebugElement implements IVariable {

	private String varName;
	
	/**
	 * Cache of current value - see #getValue().
	 */
	private LIValue value = null;

	private LIStackFrame stackframe = null;
	
	public LIVariable(LIStackFrame stackframe, String varName) {
		super(stackframe.target, stackframe.target.getLanguageID());
		this.stackframe = stackframe;
		this.varName = varName;
		//this.value = new LIValue(target);
	}

	public void setValue(String expression) throws DebugException {
		// TODO: supporting setting values
	}

	public void setValue(IValue value) throws DebugException {
		// TODO: support setting values
	}

	public boolean supportsValueModification() {
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		// TODO: support setting values
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		// TODO: support setting values
		return false;
	}

	public IValue getValue() throws DebugException {
		LIValue currentValue = getCurrentValue();
		
		if (value == null) {
			value = currentValue;
		} else {
			LIValue previousValue = value;
			if (currentValue == previousValue) {
				return value;
			}
			if (previousValue == null || currentValue == null) {
				//value = JDIValue.createValue((JDIDebugTarget)getDebugTarget(), currentValue);
				//setChangeCount(getJavaDebugTarget().getSuspendCount());
			} else if (!previousValue.equals(currentValue)) {
				value = currentValue;
				//value = JDIValue.createValue((JDIDebugTarget)getDebugTarget(), currentValue);
				//setChangeCount(getJavaDebugTarget().getSuspendCount());
			}
		}
		
		return this.value;
	}

	public String getName() throws DebugException {
		return this.varName;
	}

	public String getReferenceTypeName() throws DebugException {
		return "LI-ReferenceTypeName";
	}

	public boolean hasValueChanged() throws DebugException {
		// TODO: maybe we have to implement this when we have program with multiple threads
		return true;
	}

	protected final LIValue getCurrentValue() throws DebugException {
		return retrieveValue();
	}
	
	/**
	 * Returns this variable's current Value.
	 */
	protected LIValue retrieveValue() throws DebugException {
		synchronized (stackframe.getThread()) {
			if (getStackFrame().isSuspended()) {
				return getStackFrame().retrieveValue(this.varName);
			}
		}
		// bug 6518
		return getLastKnownValue();
	}
	
	/**
	 * Returns the last known value for this variable
	 */
	protected LIValue getLastKnownValue() {
		if (value == null) {
			return null;
		} 
		return value;
	}
	
	public LIStackFrame getStackFrame() {
		return this.stackframe;
	}
}
