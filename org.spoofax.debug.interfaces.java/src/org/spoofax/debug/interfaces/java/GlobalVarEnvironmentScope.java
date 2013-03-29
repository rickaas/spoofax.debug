package org.spoofax.debug.interfaces.java;

import java.util.HashMap;
import java.util.Map;

/**
 * The GlobalVarEnvironmentScope contains a top-level scope per Thread.
 * 
 * @author rlindeman
 *
 */
public class GlobalVarEnvironmentScope {

	private static GlobalVarEnvironmentScope globalScope;
	
	static {
		globalScope = new GlobalVarEnvironmentScope();
	}
	
	public static GlobalVarEnvironmentScope get() {
		return globalScope;
	}
	
	private Map<String, ThreadVarScope> threads = new HashMap<String, ThreadVarScope>();
	
	public String getThreadName() {
		String name = java.lang.Thread.currentThread().getName();
		return name;
	}

	public void enterFrame() {
		String name = this.getThreadName();
		this.getThreadVarScope(name).enter();
	}
	
	public void exitFrame() {
		String name = this.getThreadName();
		this.getThreadVarScope(name).exit();
	}
	
	public void setVar(String varName, Object value) {
		String name = this.getThreadName();
		this.getActiveScope(name).setVar(varName, value);
	}
	
	/**
	 * Gets the active scope
	 * @param threadName
	 * @return
	 */
	public FrameVarScope getActiveScope(String threadName) {
		ThreadVarScope threadScope = getThreadVarScope(threadName);
		return threadScope.getActiveScope();
	}
	
	public ThreadVarScope getThreadVarScope(String threadName) {
		if (!this.threads.containsKey(threadName)) {
			this.threads.put(threadName, new ThreadVarScope());
		}
		ThreadVarScope threadScope = this.threads.get(threadName);
		return threadScope;
	}
	
	public FrameVarScope getFrameScope(String threadName, int frameIndex) {
		ThreadVarScope threadScope = getThreadVarScope(threadName);
		FrameVarScope frameScope = threadScope.get(frameIndex);
		return frameScope;
	}
}
