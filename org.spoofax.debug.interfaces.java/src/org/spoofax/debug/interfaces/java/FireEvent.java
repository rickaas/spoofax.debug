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
		IEventFactoryInstance.INSTANCE.createEnter(eventInfo).execute();
	}
	
	public static void exit(String eventInfo)
	{
		IEventFactoryInstance.INSTANCE.createExit(eventInfo).execute();
	}
	
	public static void var(String eventInfo)
	{
		IEventFactoryInstance.INSTANCE.createVar(eventInfo).execute();
	}
	
	public static void step(String eventInfo)
	{
		IEventFactoryInstance.INSTANCE.createStep(eventInfo).execute();
	}
}
