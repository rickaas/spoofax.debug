package org.spoofax.debug.core.eclipse;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class LIValue extends LIDebugElement implements IValue {

	private String stringValue = "NO_VALUE";
	
	public LIValue(LIDebugTarget target) {
		super(target, target.getLanguageID());
		// TODO Auto-generated constructor stub
	}
	
	public LIValue(LIDebugTarget target, String stringValue) {
		super(target, target.getLanguageID());
		this.stringValue = stringValue;
	}

	public String getReferenceTypeName() throws DebugException {
		// TODO Auto-generated method stub
		return "Type";
	}

	public String getValueString() throws DebugException {
		// TODO generate the String representation
		return stringValue;
	}

	public boolean isAllocated() throws DebugException {
		return true;
	}

	public IVariable[] getVariables() throws DebugException {
		// TODO: support complex objects
		return new IVariable[0];
	}

	public boolean hasVariables() throws DebugException {
		// TODO: support complex objects
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LIValue)) return false;
		LIValue other = (LIValue) obj;
		boolean sameValue = false;
		try {
			if (other.getValueString() == null && this.getValueString() == null) {
				sameValue = true; 
			} else if (other.getValueString() == null) {
				sameValue = false;
			} else if (this.getValueString() == null) {
				sameValue = false;
			} else {
				sameValue = other.getValueString().equals(this.getValueString());
			}
		} catch (DebugException e) {
			// ignore exceptions
		}
		return sameValue;
	}

}
