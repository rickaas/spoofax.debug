package org.spoofax.debug.core.control.events;

public class ExitBreakPoint extends AbstractBreakPoint {

	private String functionName;
	
	public ExitBreakPoint(String filename, String name, int lineNumber) {
		super(filename, lineNumber);
		functionName = name;
	}

	@Override
	public boolean match(AbstractBreakPoint breakPoint) {
		if (breakPoint.isVirtual())
		{
			return false;
		}
		else if (breakPoint.isExit())
		{
			return true; // TODO: match on wildcards?
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isEnter() {
		return false;
	}

	@Override
	public boolean isExit() {
		return true;
	}
	
	public String getFunctionName() {
		return functionName;
	}

}
