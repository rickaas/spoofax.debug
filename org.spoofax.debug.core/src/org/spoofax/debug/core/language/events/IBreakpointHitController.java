package org.spoofax.debug.core.language.events;

import org.spoofax.debug.core.model.IProgramState;

/**
 * Checks if the current Program state corresponds to a breakpoint
 * @author rlindeman
 *
 */
public interface IBreakpointHitController {

	boolean shouldSuspend(IProgramState programState);
}
