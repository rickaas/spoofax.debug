package org.spoofax.debug.core.language;

/**
 * Returns the name of attributes used by Launch configuration
 * @author rlindeman
 *
 */
public interface LIConstants {

	String getLanguage();
	
	String getProgram();
	
	String getDebugModel();
	
	String getLineBreakpointMarker();
	
	public static final String SpoofaxSocketAttachConnector = "org.spoofax.debug.core.SpoofaxSocketAttachConnector";
	
	public static final int INTERNAL_ERROR = 150;
	
	public static final int ERR_CONNECTOR_NOT_AVAILABLE = 200;
	
	public static final String SPOOFAX_DEBUG_ID = "org.spoofax.debug.core";
	
	public static final String ATTR_LANGUAGE = SPOOFAX_DEBUG_ID+".ATTR_LANGUAGE";
}
