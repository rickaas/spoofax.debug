package org.spoofax.debug.core.control.events;

public class EnterBreakPoint extends AbstractBreakPoint {

	private String functionName;
	
	public EnterBreakPoint(String filename, String name, int lineNumber) {
		super(filename, lineNumber);
		functionName = name;
	}

	@Override
	public boolean match(AbstractBreakPoint breakPoint) {
		if (breakPoint.isVirtual())
		{
			return false;
		}
		else if (breakPoint.isEnter())
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
		return true;
	}

	@Override
	public boolean isExit() {
		return false;
	}

	public String getFunctionName() {
		return functionName;
	}
}
