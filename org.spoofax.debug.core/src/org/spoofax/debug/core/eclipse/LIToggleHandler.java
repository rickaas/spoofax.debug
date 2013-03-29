package org.spoofax.debug.core.eclipse;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.imp.services.IToggleBreakpointsHandler;

public abstract class LIToggleHandler implements IToggleBreakpointsHandler {

	@Override
	public void clearLineBreakpoint(IFile resource, int lineNumber) throws CoreException {
		for(ILineBreakpoint breakpoint : this.getLineBreakpointsAt(resource, lineNumber)) {
			breakpoint.delete();
		}
	}

	@Override
	public void disableLineBreakpoint(IFile resource, int lineNumber)
			throws CoreException {
		for(ILineBreakpoint breakpoint : this.getLineBreakpointsAt(resource, lineNumber)) {
			breakpoint.setEnabled(false);
		}
	}

	@Override
	public void enableLineBreakpoint(IFile resource, int lineNumber) throws CoreException {
		for(ILineBreakpoint breakpoint : this.getLineBreakpointsAt(resource, lineNumber)) {
			breakpoint.setEnabled(true);
		}
	}

	@Override
	public void setLineBreakpoint(IFile resource, int lineNumber) throws CoreException {
		if (!this.getLineBreakpointsAt(resource, lineNumber).isEmpty()) {
			// breakpoint already exists...
			System.out.println("ILineBreakpoint already exists at " + resource + " " + lineNumber);
		}
		IBreakpoint breakpoint = create(resource, lineNumber);
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
	}
	
	protected abstract IBreakpoint create(IFile resource, int lineNumber) throws CoreException;

	protected abstract List<ILineBreakpoint> getLineBreakpointsAt(IFile resource, int lineNumber) throws CoreException;
}
