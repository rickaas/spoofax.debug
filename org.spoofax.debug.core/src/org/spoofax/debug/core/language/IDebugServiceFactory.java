package org.spoofax.debug.core.language;

import org.eclipse.debug.core.ILaunch;
import org.spoofax.debug.core.control.java.VMContainer;
import org.spoofax.debug.core.eclipse.LIDebugTarget;

import com.sun.jdi.VirtualMachine;

public interface IDebugServiceFactory {

	VMContainer createVMContainer(VirtualMachine vm);
	
	LIDebugTarget createDebugTarget(ILaunch launch, String port);
	
	LIDebugTarget createDebugTarget(String languageID, ILaunch launch, VirtualMachine vm);
	
	LIConstants getLIConstants();
	
}
