package org.spoofax.debug.core.control.events;

import org.spoofax.debug.core.language.model.SourceLocation;

public class LineBreakPoint extends AbstractBreakPoint {

	public LineBreakPoint(SourceLocation sourceLocation) {
		super(sourceLocation.getFilename(), sourceLocation.getLocationInfo().getStart_line_num());
	}
	
	public LineBreakPoint(String filename, int lineNumber) {
		super(filename, lineNumber);
	}

	/**
	 * Try to match the given breakpoint with this.
	 */
	@Override
	public boolean match(AbstractBreakPoint breakPoint) {
		if (breakPoint.isVirtual()) {
			return false;
		} else {
			boolean onSameLine = breakPoint.getLineNumber() == this.getLineNumber();
			boolean sameFile = breakPoint.getFilename().equals(this.getFilename());
			return onSameLine && sameFile;
		}
	}

	/**
	 * Not an enter breakpoint.
	 */
	@Override
	public boolean isEnter() {
		return false;
	}

	/**
	 * Not an exit breakpoint.
	 */
	@Override
	public boolean isExit() {
		return false;
	}
	
	/**
	 * LineBreakPoints aren't virtual because we don't know the token offset.
	 * @return
	 */
	public boolean isVirtual() {
		return false;
	}

}
