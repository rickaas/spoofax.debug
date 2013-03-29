package org.spoofax.debug.interfaces.java;


/**
 * A helper class to fire events. 
 * 
 * Future notes:
 * For language that support multiple threads we may have to revise this.
 * 
 * @author rlindeman
 *
 */
public class FireEvent {

	public static void enter(String eventInfo)
	{
		(new EnterEvent(eventInfo)).execute();
	}
	
	public static void exit(String eventInfo)
	{
		(new ExitEvent(eventInfo)).execute();
	}
	
	public static void var(String eventInfo)
	{
		(new VarEvent(eventInfo)).execute();
	}
	
	public static void step(String eventInfo)
	{
		(new StepEvent(eventInfo)).execute();
	}
}
