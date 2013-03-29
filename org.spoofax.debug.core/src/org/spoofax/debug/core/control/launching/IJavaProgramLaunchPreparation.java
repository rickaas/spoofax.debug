package org.spoofax.debug.core.control.launching;

import org.eclipse.core.runtime.IPath;

public interface IJavaProgramLaunchPreparation {

	/**
	 * The program we want to run/debug first needs to be prepared. For instance we need to instrument it and then compile it.
	 * @param mode
	 */
	IJavaProgramPrepareResult prepare(String projectName, IPath alngProgramPath, String mode) throws LaunchPreparationException;
}
