package org.spoofax.debug.instrumentation.util;

import java.net.URL;

public class LibDsldiJarLocations {

	public final static String LIB_LIBDSLDI_DIR = "lib/libdsldi";
	
	public final static String DSLDI_JAVA_JAR = LIB_LIBDSLDI_DIR + "/dsldi-java.jar";
	public final static String DSLDI_JAR = LIB_LIBDSLDI_DIR + "/dsldi.jar";
	public final static String LIBDSLDI_JAR = LIB_LIBDSLDI_DIR + "/libdsldi.jar";
	
	public static URL getDsldiJava() {
		URL url = LibraryLocation.getLocation(DSLDI_JAVA_JAR);
		return url;
	}
	
	public static URL getDsldi() {
		URL url = LibraryLocation.getLocation(DSLDI_JAR);
		return url;
	}
	
	public static URL getLibdsldi() {
		URL url = LibraryLocation.getLocation(LIBDSLDI_JAR);
		return url;
	}
	

}
