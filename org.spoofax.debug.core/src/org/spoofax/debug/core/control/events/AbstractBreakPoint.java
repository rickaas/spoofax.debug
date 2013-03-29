package org.spoofax.debug.core.control.events;


/**
 * Base class for breakpoints for a specific dsl
 * @author rlindeman
 *
 */
public abstract class AbstractBreakPoint {

	private String filename;
	private int lineNumber;

	/**
	 * True if the breakpoint contains wildcards and cannot be mapped to a single program state.
	 */
	private boolean isVirtual = false;
	
	
	/**
	 * Represents a BreakPoint at the given linenumber (1-based).
	 * @param filename
	 * @param lineNumber
	 */
	public AbstractBreakPoint(String filename, int lineNumber) {
		this.filename = filename;
		this.lineNumber = lineNumber;
	}
	
	public String getFilename()
	{
		return filename;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * Returns true if the breakpoint contains a wildcard.
	 * @return
	 */
	public boolean isVirtual() {
		return isVirtual;
	}

	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	/**
	 * Returns true if the given breakpoint matches this breakpoint.
	 * The given breakpoint should be a 'real' breakpoint, no wildcards!
	 * @param breakPoint
	 * @return
	 */
	public abstract boolean match(AbstractBreakPoint breakPoint);

	public abstract boolean isEnter();

	public abstract boolean isExit();

}
