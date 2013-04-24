package org.spoofax.debug.interfaces.java.profiler;

import java.io.IOException;

import org.spoofax.debug.interfaces.java.DebugEventBase;
import org.spoofax.debug.interfaces.java.IEventFactory;

public class EventFactoryProfiler implements IEventFactory {

	public static final boolean TrackEventHandlingDuration = true;
	
	@Override
	public DebugEventBase createEnter(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.profiler.EnterEvent(eventInfo);
	}

	@Override
	public DebugEventBase createExit(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.profiler.ExitEvent(eventInfo);
	}

	@Override
	public DebugEventBase createStep(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.profiler.StepEvent(eventInfo);
	}

	@Override
	public DebugEventBase createVar(String eventInfo) {
		return new org.spoofax.debug.interfaces.java.profiler.VarEvent(eventInfo);
	}
	
	public static void start(String id) {
		//long t = System.currentTimeMillis();
		FrameDuration.instance(id);
		EventHandlingDuration.instance(id);
	}
	
	public static void stop() {
		try {
			FrameDuration.instance().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FrameDuration.reset();
		try {
			EventHandlingDuration.instance().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EventHandlingDuration.reset();
	}
	
	public static void flush() {
		try {
			FrameDuration.instance().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			EventHandlingDuration.instance().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
