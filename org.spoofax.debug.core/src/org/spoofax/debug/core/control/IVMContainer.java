package org.spoofax.debug.core.control;

import org.eclipse.debug.core.model.IDisconnect;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;

/**
 * This is a wrapper around the program that is being debugged. 
 * In most programming languages programs are run within a VM, use this class to interact with the running program.
 * @author rlindeman
 *
 */
public interface IVMContainer extends ITerminate, ISuspendResume, IDisconnect {

}
