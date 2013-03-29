package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.core.control.java.VMContainer;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.ThreadDeathEvent;

public class ThreadDeathHandler implements IJavaEventHandler {
	
	protected VMContainer vmContainer;
	
	public ThreadDeathHandler(VMContainer vmContainer) {
		this.vmContainer = vmContainer;
	}
	
	public void handleThreadDeath(ThreadDeathEvent event) {
		System.out.println("Thread " + event.thread().name() + " died...");
		// TODO: remove the thread from the program state or mark it as dead...
//		extractor.reset();
//		IEventInfo eventInfo = extractor.extract(event);
//		
//		this.vmContainer.processDebugEvent(eventInfo);
	}
	
	@Override
	public boolean handle(Event event) {
		handleThreadDeath((ThreadDeathEvent) event);
		return !IJavaEventHandler.SHOULD_SUSPEND;
	}
}
