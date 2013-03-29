package org.spoofax.debug.interfaces.java;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * A FrameVarScope corresponds to the scope of a method.
 * Inside the method scope it is possible to have nested scopes, e.g. the code block
 * in a for-loop has a child scope in which the variables are not accessible in the parent scope.
 * These scope are represented as VarEnvironments. 
 * 
 * Each FrameVarScope starts with a top level VarEnvironment which contains the method arguments. 
 * @author rlindeman
 *
 */
public class FrameVarScope {

	private Stack<VarEnvironment> scopes = new Stack<VarEnvironment>();
	
	public FrameVarScope() {
		// top level scope in this stack frame
		scopes.push(new VarEnvironment());
	}
	
	/**
	 * Called when a new block-scope is entered in a stackframe.
	 */
	public void enter() {
		scopes.push(new VarEnvironment());
	}
	
	/**
	 * Called when a block-scope is exited.
	 */
	public void exit() {
		scopes.pop();
	}
	
	public Set<String> getVarnames() {
		Set<String> varnames = new HashSet<String>();
		for(VarEnvironment env : this.scopes) {
			varnames.addAll(env.getVarnames());
		}
		return varnames;
	}
	
	public Object getValue(String varname) {
		Iterator<VarEnvironment> it = this.scopes.iterator();
		// TODO: what is the order?
		while(it.hasNext()) {
			VarEnvironment next = it.next();
			if (next.hasVarname(varname)) {
				return next.getValue(varname);
			}
			// varname not found, lets try a deeper VarEnvironment
		}
		// varname not found in this stack
		return null;
	}
	
	public void setVar(String varname, Object value) {
		this.scopes.peek().setVar(varname, value);
	}
}
