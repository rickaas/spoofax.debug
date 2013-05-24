package org.spoofax.debug.interfaces.java;

public abstract class DebugEventBase implements ISuspendInClassEntry {

	/**
	 * Name of the field that contains the eventInfo. The type of the variable should be String.
	 */
	public static String EVENTINFO_FIELD = "eventInfo";
	public static String BREAKPOINT_LINENUMBER_STATIC_FIELD = "BREAKPOINT_LINENUMBER";
	/**
	 * String representation of the eventinfo.
	 * 
	 * Should be protected so it can be accessed in any of its subclasses.
	 */
	protected String eventInfo = null;
	
	/**
	 * Empty stub. Subclasses of DebugEvent should always call this method so we can set a breakpoint on that line.
	 * The debug event info should be initialized before calling this method so the debugger can request that information.
	 */
	protected void nothing()
	{

	}
	
	public abstract void execute();
	
	public static final String ENTER = "enter";
	public static final String EXIT = "exit";
	public static final String VAR = "var";
	public static final String STEP = "step";
	
//	public Object getVariable(String threadName, String varName) {
//		FrameVarScope scope = GlobalVarEnvironmentScope.get().getActiveScope(threadName);
//		return scope.getValue(varName);
//	}
	
	public Object getVariable(String threadName, int frameIndex, String varName) {
		FrameVarScope scope = GlobalVarEnvironmentScope.get().getFrameScope(threadName, frameIndex);
		return scope.getValue(varName);
	}
	
	public void setEventInfo(String eventInfo) {
		this.eventInfo = eventInfo;
	}
}
