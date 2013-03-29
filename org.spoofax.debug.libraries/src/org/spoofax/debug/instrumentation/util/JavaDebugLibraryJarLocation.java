package org.spoofax.debug.instrumentation.util;

import java.net.URL;

public class JavaDebugLibraryJarLocation {

	public final static String LIB_RUNTIME_DIR = "lib/runtime";
	
	public final static String DEBUG_INTERFACES_JAVA = LIB_RUNTIME_DIR + "/org.spoofax.debug.interfaces.java.jar";
	
	public final static String DEBUG_INTERFACES = LIB_RUNTIME_DIR + "/org.spoofax.debug.interfaces.jar";
	
	public static URL getJavaInterfacesPath() {
		URL url = LibraryLocation.getLocation(DEBUG_INTERFACES_JAVA);
		return url;
	}
	
	public static URL getInterfacesPath() {
		URL url = LibraryLocation.getLocation(DEBUG_INTERFACES);
		return url;
	}
}
