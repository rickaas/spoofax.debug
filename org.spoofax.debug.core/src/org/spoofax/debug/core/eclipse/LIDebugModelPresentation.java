package org.spoofax.debug.core.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public abstract class LIDebugModelPresentation implements IDebugModelPresentation {

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		} else if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint) {
			return "org.eclipse.imp.runtime.impEditor"; // specialized stratego editor from the imp framework
		}
		return null;
	}

	@Override
	public void setAttribute(String attribute, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof LIDebugTarget) {
			return getTargetText((LIDebugTarget)element);
		} else if (element instanceof LIThread) {
			return getThreadText((LIThread)element);
		} else if (element instanceof LIStackFrame) {
			return getStackFrameText((LIStackFrame)element);
		}
		return null;
	}

	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		// TODO Auto-generated method stub
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}
	
	/**
	 * Returns a label for the given debug target
	 * 
	 * @param target debug target
	 * @return a label for the given debug target
	 */
	private String getTargetText(LIDebugTarget target) {
		try {
			String languageID = target.getLanguageID();
			String name = target.getName();
			String label = "";
			if (target.isTerminated()) {
				label = "<terminated>";
			}
			return label + languageID + " [" + name + "]";
			
//			String pgmPath = target.getLaunch().getLaunchConfiguration().getAttribute(IRascalResources.ATTR_RASCAL_PROGRAM, (String)null);
//			if (pgmPath != null) {
//				IPath path = new Path(pgmPath);
//				String label = "";
//				if (target.isTerminated()) {
//					label = "<terminated>";
//				}
//				return label + "Rascal [" + path.lastSegment() + "]";
//			}
		} catch (CoreException e) {
		}
		return "Rascal";

	}

	/**
	 * Returns a label for the given thread
	 * 
	 * @param thread a thread
	 * @return a label for the given thread
	 */
	private String getThreadText(LIThread thread) {
		String label;
		try {
			label = thread.getName();
		} catch (DebugException e) {
			//TODO: to improve
			label = "noname";
		}
		if (thread.isTerminated()) {
			label = "<terminated> " + label;
		} else if (thread.isStepping()) {
			label += " (stepping)";
		} else if (thread.isSuspendedByBreakpoint()) {
			label += " (suspended by line breakpoint)";
		} else if (thread.isSuspended()) {
			label += " (suspended)";
		}
		return label;
	}
	
	/**
	 * Returns a label for the given stack frame
	 * 
	 * @param frame a stack frame
	 * @return a label for the given stack frame 
	 */
	private String getStackFrameText(LIStackFrame frame) {
		try {
			return frame.getName() + " (line: " + frame.getLineNumber() + ")"; 
		} catch (DebugException e) {
		}
		return null;

	}
}
