package org.spoofax.debug.instrumentation.util;

import java.net.URL;

public class LibDsldiJarLocations {

	public final static String LIB_LIBDSLDI_DIR = "lib/libdsldi";
	
	public final static String LIBDSLDI_JAR = LIB_LIBDSLDI_DIR + "/libdsldi.jar";
	
	public static URL getLibdsldi() {
		URL url = LibraryLocation.getLocation(LIBDSLDI_JAR);
		return url;
	}
	

}
