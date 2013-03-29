package org.spoofax.debug.instrumentation.util;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class LibraryLocation {

	public static URL getLocation(String location) {
		Bundle bundle = org.spoofax.debug.instrumentation.Activator.getDefault().getBundle();
	    URL url = FileLocator.find(bundle, new Path(location), null);
	    try {
	        url = FileLocator.resolve(url);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return url;
	}
	
	/**
	 * Find the lib directory of this plugin
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static IPath getLibDirectory()
	{
		//find the jar library directory in the eclipse plugin
		//Bundle b = org.strategoxt.debug.core.Activator.getDefault().getBundle();
		Bundle b = org.spoofax.debug.instrumentation.Activator.getDefault().getBundle();
		
		IPath path = new Path("lib");
		Map override = null;
		URL url = FileLocator.find(b, path, override);
		URL fileURL = null;
		try {
			fileURL = FileLocator.toFileURL(url);
			//System.out.println("FILE URL:" + fileURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//abort("Could not find required directory \"lib\".", e);
		}
		//System.out.println("URL: " + fileURL)		
		
		String urlPath = fileURL.getPath();
		IPath directory = new Path(urlPath);
		return directory;
	}
	
	public static IPath URLToIPath(URL url) throws IOException {
		URL fileURL = FileLocator.toFileURL(url);
		
		String urlPath = fileURL.getPath();
		IPath path = new Path(urlPath);
		return path;
	}
}
