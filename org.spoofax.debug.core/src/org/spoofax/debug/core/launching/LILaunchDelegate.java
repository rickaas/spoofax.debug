package org.spoofax.debug.core.launching;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.spoofax.debug.core.control.launching.IJavaProgramLaunchPreparation;
import org.spoofax.debug.core.control.launching.IJavaProgramPrepareResult;
import org.spoofax.debug.core.control.launching.LaunchPreparationException;
import org.spoofax.debug.core.language.LIConstants;

public abstract class LILaunchDelegate extends AbstractJavaLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	private IProject project;
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
        if (monitor == null){
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Launching "+this.getLanguageName()+" program", IProgressMonitor.UNKNOWN);
        
        
		// program name
		String program = configuration.getAttribute(this.getLIConstants().getProgram(), (String)null);
		if (program == null) {
			abort(this.getLanguageName() + " program unspecified.", null);
			return;
		}
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(program));
		if (!file.exists()) {
			abort(MessageFormat.format(this.getLanguageName() + " program {0} does not exist.", new Object[] {file.getFullPath().toString()}), null);
			return;
		}
		this.project = file.getProject();
		String project = file.getProject().getName();
		
		// TODO: support program arguments when running an ActionLanguage program
		IPath programPath = new Path(program);
		IPath sourceBasedir = ResourcesPlugin.getWorkspace().getRoot().getProject(project).getLocation();
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getProject(project).getFile(programPath); // path to the stratego file
		
		programPath =  f.getProjectRelativePath();
		IPath projectPath = new Path(project);
		programPath = f.getProjectRelativePath().makeRelativeTo(projectPath); // make the stratego file path relative to the project path
		System.out.println("PROJECT...:" + project);
		System.out.println("BASEDIR...: " + sourceBasedir);
		System.out.println("COMPILING...: " + programPath);
		
		
		// compile the ActionLanguage program
		//LaunchProgram l = new LaunchProgram(new AlngHybridInterpreterProvider());
		IJavaProgramLaunchPreparation prepare = this.getLaunchPreparation();
		monitor.subTask("Compiling "+this.getLanguageName()+" program...");
		IJavaProgramPrepareResult result = null;
		try {
			result = prepare.prepare(project, programPath, mode);
		} catch (LaunchPreparationException e) {
			this.abort("Could not prepare program.", e);
		}
		
		// TODO: start the VM with the correct java-args
		if (result != null)
		{
			monitor.subTask("Starting "+this.getLanguageName()+" VM");
			
			//String classname = "org.languages.alng.programs.assigninblock.Output";
			String classname = result.getClassname();
			
			String[] classpath = this.getClasspaths(result);
			
			launchVM(monitor, classname, classpath, launch, mode);
		}

		monitor.done();
	}
	
	public abstract String getLanguageName();

	public abstract LIConstants getLIConstants();
	
	public abstract IJavaProgramLaunchPreparation getLaunchPreparation();
	
	public abstract String[] getClasspaths(IJavaProgramPrepareResult result);
	
	public abstract void launchVM(IProgressMonitor monitor, String classname, String[] classpath, ILaunch launch, String mode) throws CoreException;
	
	/**
	 * Throws an exception with a new status containing the given
	 * message and optional exception.
	 * 
	 * @param message error message
	 * @param e underlying exception
	 * @throws CoreException
	 */
	protected void abort(String message, Throwable e) throws CoreException {
		// TODO: the plug-in code should be the example plug-in, not Stratego debug model id
		IStatus status = new Status(IStatus.ERROR, this.getLIConstants().getDebugModel(), 0, message, e);
		//throw new CoreException(status);
		
		IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
		
		if (handler != null) {
			Object result = handler.handleStatus(status, null);
			System.out.println(result);
		}
	}
	
	public IProject getProject() {
		return this.project;
	}
}
