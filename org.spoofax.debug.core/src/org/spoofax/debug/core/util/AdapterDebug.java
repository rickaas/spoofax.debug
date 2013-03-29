package org.spoofax.debug.core.util;

/**
 * Idea taken from pydev
 * @author rlindeman
 *
 */
public class AdapterDebug {

	private static boolean log_adapter = false;
	
    public static void print(Object askedfor, Class<?> adapter) {
        if(log_adapter){
            System.out.println(askedfor.getClass().getName() + " requests "+adapter.toString());
        }
    }

    public static void printDontKnow(Object askedfor, Class<?> adapter) {
        if(log_adapter){
            System.out.println("DONT KNOW: "+askedfor.getClass().getName() + " requests "+adapter.toString());
        }
    }
}
