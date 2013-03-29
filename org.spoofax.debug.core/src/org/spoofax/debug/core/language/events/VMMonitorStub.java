package org.spoofax.debug.core.language.events;

import org.spoofax.debug.core.control.events.AbstractBreakPoint;
import org.spoofax.debug.interfaces.info.IEventInfo;

public class VMMonitorStub implements IVMMonitor {

	@Override
	public void started() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resumed(int detail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void suspended(IEventInfo eventInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakpointHit(IEventInfo eventInfo,
			AbstractBreakPoint breakpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepped(IEventInfo eventInfo) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disconnected() {
		
	}

}
