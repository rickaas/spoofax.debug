package org.spoofax.debug.core.eclipse.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.model.elements.DebugElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.TreePath;

@SuppressWarnings("restriction")
public class LILaunchElementLabelProvider
		/* implements IElementLabelProvider */extends DebugElementLabelProvider {

	public LILaunchElementLabelProvider() {

	}

	@Override
	protected String getLabel(TreePath elementPath,
			IPresentationContext presentationContext, String columnId)
			throws CoreException {
		Object element = elementPath.getLastSegment();

		if (element instanceof ILaunch) {
			return getLaunchText((ILaunch) element);
		} else {
			return super.getLabel(elementPath, presentationContext, columnId);
		}
	}

	private String getLaunchText(ILaunch launch) {
		ILaunchConfiguration config = launch.getLaunchConfiguration();

		if (config == null) {
			return "unknown";
		}

		String name = config.getName();

		if (isTerminated(launch)) {
			name = "<" + name + ">";
		}

		IProcess[] processes = launch.getProcesses();

		if (processes.length > 0) {
			IProcess process = processes[0];

			try {
				int exitCode = process.getExitValue();

				name += " exit code=" + exitCode;
			} catch (DebugException ex) {
				// The process has not yet exited.

			}
		}

		return name;
	}

	private boolean isTerminated(ILaunch launch) {
		IProcess[] processes = launch.getProcesses();

		if (processes.length > 0) {
			IProcess process = processes[0];

			try {
				@SuppressWarnings("unused")
				int exitCode = process.getExitValue();

				return true;
			} catch (DebugException ex) {
				// The process has not yet exited.

				return false;
			}
		} else {
			return true;
		}
	}

}
