package org.spoofax.debug.core.control.java;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachineManager;

public class VMMFactory {

	/**
	 * returns the sun VirtualMachineManager via com.sun.jdi.BootStrap.virtualMachineManager().
	 * If this method is called with jdi.jar, the eclipse-version, on the classpath instead of tools.jar, the sun-version, 
	 * then this method will return null.
	 */
	public static VirtualMachineManager getSunVMM()
	{
		VirtualMachineManager vmm = Bootstrap.virtualMachineManager(); // com.sun.jdi.BootStrap
		return vmm;
	}
	
	public static VirtualMachineManager getEclipseVMM()
	{
		VirtualMachineManager vmm = org.eclipse.jdi.Bootstrap.virtualMachineManager();
		return vmm;
	}
	
	/**
	 * Try to get the VirtualMachineManager from the eclipse plugin implementation via org.eclipse.jdi.Bootstrap.virtualMachineManager().
	 * If it is null, try to use the Sun implementation via com.sun.jdi.BootStrap.virtualMachineManager(). 
	 * @return
	 */
	public static VirtualMachineManager getVirtualMachineManager()
	{
		VirtualMachineManager vmm = getEclipseVMM();
		if (vmm != null)
		{
			return vmm;
		}
		else
		{
			return getSunVMM();
		}
	}

}
