org.spoofax.debug.java.library is the Java platform-level debug library.

All DSLs that compile to Java can use this runtime library.
The generated DSL program java code should call the debug-events in this library.
This library then takes care of serializing the debug event info.

This library acts as an interface between the DSL JVM and the debugger JVM.

The IDE which runs in another JVM attaches to the JVM that runs the DSL program
 and sets breakpoints in the Java platform-level debug library.
The Spoofax platform-level event handler deserializes the debug event info 
and uses it to create a representation of the program state. 


The org.spoofax.debug.java.library.jar should be on the classpath of the running DSL Application.
