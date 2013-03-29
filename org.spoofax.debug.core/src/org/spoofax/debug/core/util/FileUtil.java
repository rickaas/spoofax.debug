package org.spoofax.debug.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

@SuppressWarnings("unused")
public class FileUtil {

	/**
	 * Given a directory, delete the directory and all its contents in one go.
	 * @param sFilePath
	 * @return
	 */
	public static boolean deleteFile(String sFilePath)
	{
	  File oFile = new File(sFilePath);
	  if(oFile.isDirectory())
	  {
	    File[] aFiles = oFile.listFiles();
	    for(File oFileCur: aFiles)
	    {
	       deleteFile(oFileCur.getAbsolutePath());
	    }
	  }
	  return oFile.delete();
	}

	/**
	 * Returns the filenames (excluding the path) of the files that are in the given directory and have the given extension.
	 * Subdirectories will not be traversed.
	 * @param basePath
	 * @param extension
	 * @return
	 */
	public static List<String> getFilesWithExtension(IPath basePath, final String extension) {
		return getFilesWithExtension(basePath.toFile(), extension);
	}
	
	/**
	 * Returns the filenames (excluding the path) of the files that are in the given directory and have the given extension.
	 * Subdirectories will not be traversed.
	 * @param basePath
	 * @param extension
	 * @return
	 */
	public static List<String> getFilesWithExtension(File basePath, final String extension) {
		
		List<String> matches = new ArrayList<String>();

		File oFile = basePath; //new File(basePath);
		if (oFile.isDirectory()) {
			// get all files with the matching extension
			FileFilter filter = new FileFilter() {
				
				public boolean accept(File pathname) {
					String suffix = "." + extension;
					if (pathname.isFile() && pathname.getName().endsWith(suffix)){
						return true;
					}
					return false;
				}
			};
			File[] fs = oFile.listFiles(filter);
			for(File f : fs)
			{
				matches.add(f.getName());
			}
		}
		return matches;
	}
	
	/**
	 * Converts to list of IPath's to a single String separated by OS-dependant pathSeparatorChar.
	 * This String can be used as a classpath.
	 * @param list
	 * @return
	 */
	public static String convertIPathToClasspath(List<IPath> list)
	{
		boolean first = true;
		StringBuilder builder = new StringBuilder();
		for(IPath path : list)
		{
			if (!first)
			{
				builder.append(java.io.File.pathSeparatorChar);
			}
			else
			{
				first = false;
			}
			builder.append(path);
		}
		return builder.toString();
	}
	
	/**
	 * Converts the given List<IPath> to a String[]. the toOSString-method is used to generate a String from the IPath.
	 * @param list
	 * @return
	 */
	public static String[] convertIPathToStringArray(List<IPath> list)
	{
		String[] cp = new String[list.size()];
		for(int i = 0; i < list.size(); i++)
		{
			cp[i] = list.get(i).toOSString();
		}
		return cp;
	}
	

}
