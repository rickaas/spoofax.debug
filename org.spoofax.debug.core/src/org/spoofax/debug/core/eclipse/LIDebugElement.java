package org.spoofax.debug.core.eclipse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.spoofax.debug.core.Activator;
import org.spoofax.debug.core.language.IDebugServiceFactory;

/**
 * A generic base class for all debug elements.
 * Language Independent elements must have a language name so we can distingish between them.
 * Language dependent features should be implemented in subclasses in a language specific plugin.
 * @author rlindeman
 *
 */
public abstract class LIDebugElement extends PlatformObject implements IDebugElement {

	// containing target 
	protected LIDebugTarget target;
	
	/**
	 * Use this to construct dls specific debug services.
	 */
	protected final IDebugServiceFactory debugServiceFactory;
	
	protected String languageID;
	
	/**
	 * Constructs a new debug element contained in the given
	 * debug target.
	 * 
	 * @param target
	 */
	public LIDebugElement(LIDebugTarget target, String languageID) {
		this.target = target;
		this.languageID = languageID;
		this.debugServiceFactory = Activator.getDefault().getServiceRegistry().getServiceFactory(languageID);
	}
	
	public String getLanguageID() {
		return this.languageID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 * 
	 * Should this include the unique language identifier?
	 */
	public String getModelIdentifier() {
		//return IStrategoConstants.ID_STRATEGO_DEBUG_MODEL;
		//return null;
		return this.debugServiceFactory.getLIConstants().getDebugModel();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return target;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class) {
			return this;
		}
		else if (adapter == ILaunch.class)
		{
			return this.getLaunch();
		}
		else if (adapter == IDebugTarget.class) {
			return getDebugTarget();
		}
		if (adapter == org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider.class) {
			System.out.println("FOO");
		}
		// org.eclipse.debug.core.model.IDebugModelProvider
        //Platform.getAdapterManager().getAdapter(this, adapter);
        org.spoofax.debug.core.util.AdapterDebug.printDontKnow(this, adapter);
        // ongoing, I do not fully understand all the interfaces they'd like me to support
		return super.getAdapter(adapter);
	}
	
	/**
	 * Throws a new DebugException, subclasses should use this when they want to throw exceptions.
	 * @param message
	 * @param e
	 * @throws DebugException
	 */
	protected void abort(String message, Throwable e) throws DebugException {
		// was DebugExamplesPlugin.getDefault().getDescriptor().getUniqueIdentifier(),
		// deprecated: getDescriptor().getUniqueIdentifier()
		throw new DebugException(new Status(IStatus.ERROR, org.spoofax.debug.core.Activator.getDefault().getBundle().getSymbolicName(), 
				DebugPlugin.INTERNAL_ERROR, message, e));
	}
	
	/**
	 * Fires a debug event
	 * 
	 * @param event the event to be fired
	 */
	protected void fireEvent(DebugEvent event) {
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] {event});
	}
	
	/**
	 * Fires a <code>CREATE</code> event for this element.
	 */
	protected void fireCreationEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}	
	
	/**
	 * Fires a <code>RESUME</code> event for this element with
	 * the given detail.
	 * 
	 * @param detail event detail code
	 */
	public void fireResumeEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.RESUME, detail));
	}

	/**
	 * Fires a <code>SUSPEND</code> event for this element with
	 * the given detail.
	 * 
	 * @param detail event detail code
	 */
	public void fireSuspendEvent(int detail) {
		fireEvent(new DebugEvent(this, DebugEvent.SUSPEND, detail));
	}
	
	/**
	 * Fires a <code>TERMINATE</code> event for this element.
	 */
	protected void fireTerminateEvent() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
}
