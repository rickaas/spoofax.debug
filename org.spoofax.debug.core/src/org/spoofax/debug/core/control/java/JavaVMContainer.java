package org.spoofax.debug.core.control.java;

import org.eclipse.debug.core.DebugException;
import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.java.events.IJavaEventHandler;
import org.spoofax.debug.core.language.model.StepController;
import org.spoofax.debug.core.model.IProgramState;
import org.spoofax.debug.interfaces.info.IEventInfo;

import com.sun.jdi.VirtualMachine;

/**
 * This is just a normal Java virtual machine.
 * Use it to launch regular java programs.
 * @author rlindeman
 *
 */
public class JavaVMContainer extends VMContainer {

	public JavaVMContainer(VirtualMachine vm) {
		super(vm);
	}
	

	@Override
	public boolean canTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void terminate() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canResume() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSuspend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resume() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void suspend() throws DebugException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IJavaEventHandler getHandler(Class clazz) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IProgramState getProgramState() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean processDebugEvent(IEventInfo eventInfo) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void scheduleSuspend() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addBreakpoint(AbstractBreakPoint breakpoint) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeBreakpoint(AbstractBreakPoint breakpoint) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeAllBreakpoints() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public StepController getStepController() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean canDisconnect() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void disconnect() throws DebugException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isDisconnected() {
		// TODO Auto-generated method stub
		return false;
	}

}
