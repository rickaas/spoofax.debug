package org.spoofax.debug.interfaces.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A VarEnvironment maintains name-value pairs for each variable introduced in the current scope.
 * The variable names are case-sensitive.
 * 
 * VarEnvironments can be nested (e.g. a for-loop within a for-loop), but this is handled
 * by the FrameVarScope.enter() method.
 * @author rlindeman
 *
 */
public class VarEnvironment {

	private Map<String, Object> variables = new HashMap<String, Object>();
	
	public VarEnvironment() {
		
	}
	
	/**
	 * Sets the value for a variable.
	 * The variable does not need to exist, if it does its old value will be overwritten.
	 * @param varname
	 * @param value
	 */
	public void setVar(String varname, Object value) {
		this.variables.put(varname, value);
	}
	
	public boolean hasVarname(String varname) {
		return this.variables.containsKey(varname);
	}
	
	public Set<String> getVarnames() {
		return this.variables.keySet();
	}
	
	/**
	 * Returns null when variable with varname does not exist.
	 * @param varname
	 * @return
	 */
	public Object getValue(String varname) {
		if (this.variables.containsKey(varname)) {
			return this.variables.get(varname);
		} else {
			return null;
		}
	}
}
