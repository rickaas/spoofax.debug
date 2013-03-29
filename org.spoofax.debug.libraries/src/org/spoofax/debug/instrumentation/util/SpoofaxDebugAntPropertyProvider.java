package org.spoofax.debug.instrumentation.util;

import java.net.URL;

import org.eclipse.ant.core.IAntPropertyValueProvider;

public class SpoofaxDebugAntPropertyProvider implements IAntPropertyValueProvider {

	public static final String SPOOFAX_DEBUG_INTERFACES_JAVA_JAR = "eclipse.spoofax.debug.interfaces.java.jar";
	public static final String SPOOFAX_DEBUG_INTERFACES_JAR = "eclipse.spoofax.debug.interfaces.jar";
	public static final String DSLDI_JAVA_JAR = "eclipse.dsldi.java.jar";
	public static final String DSLDI_JAR = "eclipse.dsldi.jar";
	public static final String LIBDSLDI_JAR = "eclipse.libdsldi.jar";
	
	@Override
	public String getAntPropertyValue(String antPropertyName) {

		if (SPOOFAX_DEBUG_INTERFACES_JAR.equals(antPropertyName)) {
			URL url = JavaDebugLibraryJarLocation.getInterfacesPath();
			return url.getFile();
		} else if (SPOOFAX_DEBUG_INTERFACES_JAVA_JAR.equals(antPropertyName)) {
			URL url = JavaDebugLibraryJarLocation.getJavaInterfacesPath();
			return url.getFile();
		} else if (DSLDI_JAVA_JAR.equals(antPropertyName)) {
			URL url = LibDsldiJarLocations.getDsldiJava();
			return url.getFile();
		} else if (DSLDI_JAR.equals(antPropertyName)) {
			URL url = LibDsldiJarLocations.getDsldi();
			return url.getFile();
		} else if (LIBDSLDI_JAR.equals(antPropertyName)) {
			URL url = LibDsldiJarLocations.getLibdsldi();
			return url.getFile();
		}
		return null;
	}

//	public static String getStrategoJarPath() {
//		String result = org.strategoxt.stratego_lib.Main.class.getProtectionDomain().getCodeSource().getLocation().getFile();
//		if (Platform.getOS().equals(Platform.OS_WIN32)) {
//			// FIXME: proper paths on Windows
//			result = result.substring(1);
//		}
//		if (!result.endsWith(".jar")) { // ensure correct jar at development time
//			String result2 = result + "/../strategoxt.jar";
//			if (new File(result2).exists()) return result2;
//			result2 = result + "/java/strategoxt.jar";
//			if (new File(result2).exists()) return result2;
//		}
//		return result;
//	}
}
