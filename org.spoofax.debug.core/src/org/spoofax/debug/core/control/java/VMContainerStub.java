package org.spoofax.debug.core.control.java;

import org.eclipse.debug.core.DebugException;
import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.core.control.java.events.IJavaEventHandler;
import org.spoofax.debug.core.language.model.StepController;
import org.spoofax.debug.core.model.IProgramState;
import org.spoofax.debug.interfaces.info.IEventInfo;

/**
 * When the real VMContainer isn't initialized this stub can be used.
 * @author rlindeman
 *
 */
public class VMContainerStub extends VMContainer {

	public VMContainerStub() {
		super(null); // no VirtualMachine
	}

	@Override
	public boolean canTerminate() {
		return false;
	}

	@Override
	public boolean isTerminated() {
		return false;
	}

	@Override
	public void terminate() throws DebugException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canResume() {
		return false;
	}

	@Override
	public boolean canSuspend() {
		return false;
	}

	@Override
	public boolean isSuspended() {
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
