package org.spoofax.debug.core.eclipse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class LISourceOffsetConvertor {

	private Map<String, IDocument> documents = new HashMap<String, IDocument>();
	
	private String projectName;
	
	private IProject project;
	
	public LISourceOffsetConvertor(String projectName) {
		this.projectName = projectName;
		this.init();
	}
	
	private void init(){
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		this.project = myWorkspaceRoot.getProject(projectName);
	}
	
	public String convertStreamToString(IFile file) throws IOException, CoreException {
		InputStream is = file.getContents();
		String charSet = file.getCharset();
		//
		// To convert the InputStream to String we use the
		// Reader.read(char[] buffer) method. We iterate until the
		// Reader return -1 which means there's no more data to
		// read. We use the StringWriter class to produce the string.
		//
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, charSet));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
	private IDocument createDocument(String filelocation) {
		// get the buffer manager  org.eclipse.core.filebuffers
		IFile file = project.getFile(filelocation);
		String contents = "";
		try {
			contents = convertStreamToString(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IDocument doc = new Document(contents);
		
		return doc;
	}
	
	private IDocument getDocument(String filelocation) {
		if (!documents.containsKey(filelocation)) {
			IDocument doc = this.createDocument(filelocation);
			documents.put(filelocation, doc);
		}
		return documents.get(filelocation);
	}
	
	public int getLineOffset(String filename, int linenumber) {
		// lienumber is 1-based
		IDocument doc = getDocument(filename);
		try {
			// doc want 0-based linenumber
			IRegion region = doc.getLineInformation(linenumber-1);
			String s = doc.get(region.getOffset(), region.getLength());
			int offset = region.getOffset();
			//int lineOffset = doc.getLineOffset(linenumber);
			
			return offset;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
