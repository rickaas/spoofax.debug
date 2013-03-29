package org.spoofax.debug.core.language.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugException;
import org.spoofax.debug.core.model.IProgramState;
import org.spoofax.debug.interfaces.info.IEventInfo;

public class StepController {

	private IProgramState programState;
	
	private enum DebugStepMode {
		NO_STEP, STEP_INTO, STEP_OVER, STEP_RETURN
	};
	
	private Map<String, DebugStepMode> threadStepModeMap = new HashMap<String, DebugStepMode>();
	
	/**
	 * When a step over or step into is requested then store the stack depth at which it was requested.
	 */
	private Map<String, Integer> stepStartDepthMap = new HashMap<String, Integer>();
	
	public StepController(IProgramState programState) {
		this.programState = programState;
	}

	public boolean isStepping(String threadName) {
		if (this.threadStepModeMap.containsKey(threadName)) {
			return this.threadStepModeMap.get(threadName) != DebugStepMode.NO_STEP;
		}
		return false;
	}

	public void stepInto(String threadName) throws DebugException {
		int depth = programState.getThread(threadName).getStackFrameDepth();
		this.threadStepModeMap.put(threadName, DebugStepMode.STEP_INTO);
		this.stepStartDepthMap.put(threadName, depth);
	}

	public void stepOver(String threadName) throws DebugException {
		int depth = programState.getThread(threadName).getStackFrameDepth();
		this.threadStepModeMap.put(threadName, DebugStepMode.STEP_OVER);
		this.stepStartDepthMap.put(threadName, depth);
	}

	public void stepReturn(String threadName) throws DebugException {
		int depth = programState.getThread(threadName).getStackFrameDepth();
		this.threadStepModeMap.put(threadName, DebugStepMode.STEP_RETURN);
		this.stepStartDepthMap.put(threadName, depth);
	}

	public void cancelStep(String threadName) {
		if (this.threadStepModeMap.containsKey(threadName)) {
			this.threadStepModeMap.put(threadName, DebugStepMode.NO_STEP);
		}
		if (this.stepStartDepthMap.containsKey(threadName)) {
			this.stepStartDepthMap.put(threadName, -1);
		}
	}
	
	/**
	 * Called after the IProgramState has been updated.
	 * @param threadName
	 * @return
	 */
	public boolean shouldSuspend(IEventInfo eventInfo) {
		String threadName = eventInfo.getThreadName();
		if (!eventInfo.getEventType().equals("step")) return false;
		if (!this.threadStepModeMap.containsKey(threadName)) return false;
		if (this.threadStepModeMap.get(threadName) == DebugStepMode.NO_STEP) return false;
		if (!this.stepStartDepthMap.containsKey(threadName)) throw new RuntimeException("Step was defined, but unable to locate step start location for thread " + threadName);
		
		DebugStepMode stepMode = this.threadStepModeMap.get(threadName);
		int currentDepth = this.programState.getThread(threadName).getStackFrameDepth();
		int stepStartDepth = this.stepStartDepthMap.get(threadName);
		if (DebugStepMode.STEP_INTO == stepMode) {
			// We want to suspend in a lower stackframe.
			// But if that is not possible just suspend at the next.
			// Conclusion: always suspend!
			return true;
		} else if (DebugStepMode.STEP_OVER == stepMode) {
			// Suspend at the next event that is at the same depth.
			// If the depth becomes less (the stack is popped), always suspend.
			if (currentDepth <= stepStartDepth) {
				return true;
			}
		} else if (DebugStepMode.STEP_RETURN == stepMode) {
			// We want to suspend in the caller of the previous stackframe
			if (currentDepth < stepStartDepth) {
				return true;
			}
		}
		return false;
	}
}
