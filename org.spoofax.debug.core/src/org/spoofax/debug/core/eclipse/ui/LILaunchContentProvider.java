package org.spoofax.debug.core.eclipse.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.model.elements.ElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.IDebugUIConstants;

@SuppressWarnings("restriction")
public class LILaunchContentProvider
		/* implements IElementContentProvider */extends ElementContentProvider {

	public LILaunchContentProvider() {

	}

	@Override
	protected int getChildCount(Object element, IPresentationContext context,
			IViewerUpdate monitor) throws CoreException {
		return getLaunchChildren((ILaunch) element).length;
	}

	@Override
	protected Object[] getChildren(Object parent, int index, int length,
			IPresentationContext context, IViewerUpdate monitor)
			throws CoreException {
		return getElements(getLaunchChildren((ILaunch) parent), index, length);
	}

	@Override
	protected boolean hasChildren(Object element, IPresentationContext context,
			IViewerUpdate monitor) throws CoreException {
		return getLaunchChildren((ILaunch) element).length > 0;
	}

	@Override
	protected boolean supportsContextId(String id) {
		return IDebugUIConstants.ID_DEBUG_VIEW.equals(id);
	}

	  private Object[] getLaunchChildren(ILaunch launch) throws CoreException {
		    return launch.getDebugTargets();

		    // TODO(devoncarew): while this behavior is what we want, a selection provider is hard-coded
		    // to assume a normal debug object hierarchy. We lose the selection focus on pause that we
		    // want.

//		    IDebugTarget debugTarget = launch.getDebugTarget();
		//
//		    if (debugTarget != null) {
//		      return debugTarget.getThreads();
//		    } else {
//		      return new Object[0];
//		    }
		  }
}
