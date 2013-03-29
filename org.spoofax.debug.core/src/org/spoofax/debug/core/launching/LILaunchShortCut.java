package org.spoofax.debug.core.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public abstract class LILaunchShortCut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		System.out.println("Launch selection");
        if (selection instanceof IStructuredSelection) {
        	searchAndLaunch(((IStructuredSelection)selection).toArray(), mode);
        }
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		System.out.println("Launch from editor");
        IEditorInput input = editor.getEditorInput();
        IFile file = (IFile) input.getAdapter(IFile.class);
        if (file != null) {
        	System.out.println("File: " + file.getFullPath());
        	// /stratego-resources/src-str/test/localvar/localvar.str
        	ILaunchConfiguration launchConfig = findLaunchConfiguration(file, mode);
    		if (launchConfig == null) {
    			launchConfig = createConfiguration(file);
    		}
    		if (launchConfig != null) {
    			//DebugUITools.launch(config, mode); // TODO use this helper method?
        		try {
        			launchConfig.launch(mode, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        }
        // lookup an existing launch configuration
        
		/*
        IEditorInput input = editor.getEditorInput();
        IJavaElement javaElement = 
            (IJavaElement) input.getAdapter(IJavaElement.class);
        if (javaElement != null) {
         searchAndLaunch(new Object[] {javaElement}, mode);
        } 
        */
	}
	
	/**
	 * Returns a configuration from the given collection of configurations that should be launched,
	 * or <code>null</code> to cancel. Default implementation opens a selection dialog that allows
	 * the user to choose one of the specified launch configurations.  Returns the chosen configuration,
	 * or <code>null</code> if the user cancels.
	 * 
	 * Copied from org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut.
	 * 
	 * @param configList list of configurations to choose from
	 * @return configuration to launch or <code>null</code> to cancel
	 */
	protected ILaunchConfiguration chooseConfiguration(List<ILaunchConfiguration> configList) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(getTypeSelectionTitle());  
		dialog.setMessage("&Select existing configuration:"/*LauncherMessages.JavaLaunchShortcut_2*/);
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

    /**
     * Returns the LaunchConfigurations that are ActionLanguage launch configs and match the actionlanguage program path.
     * 
     * @param alngProgramPath alngProgramPath points to a alng program, the path should start with "/{project-name}"
     */
    public ILaunchConfiguration findLaunchConfiguration(IFile alngProgramPath, String mode) {
    	List<ILaunchConfiguration> candidateConfigs = this.getLaunchConfigurations(alngProgramPath, mode);
		int candidateCount = candidateConfigs.size();
		if (candidateCount == 1) {
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else if (candidateCount > 1) {
			return chooseConfiguration(candidateConfigs);
		}
		return null;
	}

	public abstract void searchAndLaunch(Object[] search, String mode);

	protected abstract String getTypeSelectionTitle();
	
	/**
	 * Returns the type of configuration this shortcut is applicable to.
	 * 
	 * Copied from org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut.
	 * 
	 * @return the type of configuration this shortcut is applicable to
	 */
	public ILaunchConfigurationType getLaunchConfigurationType()
	{
    	ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();

    	// org.languages.alng.debug.launchConfigurationType
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(this.getLaunchConfigurationTypeID()); 
		return type;
	}
	
	public abstract String getLaunchConfigurationTypeID();
	
	public abstract String getProgramAttribute();
	
	/**
	 * Creates and returns a new configuration based on the specified type.
	 * 
	 * Copied from org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#createConfiguration(org.eclipse.jdt.core.IType)
	 * 
	 * @param file the alng file to create a launch configuration for
	 * @return launch configuration configured to launch the specified type
	 */
	public ILaunchConfiguration createConfiguration(IFile file) {
		ILaunchConfiguration config = null;
		try {
			config = this.createLaunchConfiguration(file);
		} catch (CoreException ce) {
			MessageDialog.openError(getShell(), "Error"/*LauncherMessages.JavaLaunchShortcut_3*/, ce.getStatus().getMessage());	
		}
		return config;
	}
	
	/**
	 * Creates and returns a new configuration based on the specified type.
	 * 
	 * Copied from org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut#createConfiguration(org.eclipse.jdt.core.IType)
	 * 
	 * TODO: generateUniqueLaunchConfigurationNameFrom is deprecated, since 3.6 clients should use the generateLaunchConfigurationName(String)
	 * @param file the actionlanguage file to create a launch configuration for
	 * @return launch configuration configured to launch the specified type
	 * @throws CoreException 
	 */
	@SuppressWarnings("deprecation")
	public ILaunchConfiguration createLaunchConfiguration(IFile file) throws CoreException {
		ILaunchConfiguration config = null;

		ILaunchConfigurationType configType = getLaunchConfigurationType();
		String namePrefix = file.getName();
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(namePrefix)); 
		

		// for now an actionlanguage program launch config only contains the program location
		String program = file.getFullPath().toOSString();
		wc.setAttribute(this.getProgramAttribute(), program);

		IResource[] rs = new IResource[] {file};
		wc.setMappedResources(rs);
		config = wc.doSave();

		return config;
	}
	
	/**
	 * Get the existing launch configurations for the given alng file.
	 * 
	 * @param file
	 * @return
	 */
    public List<ILaunchConfiguration> getLaunchConfigurations(IFile file, String mode)
    {
    	// fetch the stratego config type
    	ILaunchConfigurationType type = getLaunchConfigurationType();
		List<ILaunchConfiguration> candidateConfigs = new ArrayList<ILaunchConfiguration>();
		if (type != null)
		{
			// stratego launch configuration type found!
			ILaunchConfiguration[] configs = null;
			try {
				configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(type);
			} catch (CoreException e) {
				// Could not get Launch configurations for the stratego launch type.
				e.printStackTrace();
			}		
			if (configs != null)
			{
				for(ILaunchConfiguration config : configs)
				{
					// find a config that matches the program name
					String program;
					try {
						program = config.getAttribute(this.getProgramAttribute(), "");
						// TODO: should we equal on IPath objects instead of Strings?
						// TODO: should we also check if the mode is supported for this launch config?
						if (program.equals(file.getFullPath().toOSString())) 
						{
							candidateConfigs.add(config);
						}
					} catch (CoreException e) {
						// Could not get program name for the stratego launch
						e.printStackTrace();
					}
				}
			}

		}
		return candidateConfigs;
    }

	/**
	 * Convenience method to return the active workbench window shell.
	 * 
	 * Copied from org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchShortcut.
	 * 
	 * @return active workbench window shell
	 */
	public static Shell getShell() {
		return org.spoofax.debug.core.Activator.getActiveWorkbenchShell();
	}
}
