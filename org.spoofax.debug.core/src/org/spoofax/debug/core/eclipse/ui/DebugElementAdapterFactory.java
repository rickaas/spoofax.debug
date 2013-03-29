package org.spoofax.debug.core.eclipse.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;

/**
 * Taken from com.google.dart.tools.debug.ui.internal.presentation.DebugElementAdapterFactory
 * @author rlindeman
 *
 */
@SuppressWarnings("restriction")
public class DebugElementAdapterFactory implements IAdapterFactory {

	public static void init() {
		IAdapterManager manager = Platform.getAdapterManager();

		DebugElementAdapterFactory factory = new DebugElementAdapterFactory();

		//manager.registerAdapters(factory, DartiumDebugVariable.class);
		//manager.registerAdapters(factory, ServerDebugVariable.class);

		//if (!DartCore.isPluginsBuild()) {
			manager.registerAdapters(factory, Launch.class);
		//}
	}

	//private IAdapterFactory defaultAdapter = new org.eclipse.debug.internal.ui.views.launch.DebugElementAdapterFactory();
	
	private static IElementContentProvider launchContentProvider = new LILaunchContentProvider();
	private static IElementLabelProvider debugElementLabelProvider = new LILaunchElementLabelProvider();
	
	public DebugElementAdapterFactory() {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType.equals(IElementContentProvider.class)) {
			if (adaptableObject instanceof ILaunch) {
				return launchContentProvider;
			}

			// If we don't return the default debug adapter we won't be able to
			// expand any variables.
			//return defaultAdapter.getAdapter(adaptableObject, adapterType);
		}


		if (adapterType.equals(IElementLabelProvider.class)) {
			if (adaptableObject instanceof ILaunch) {
				return debugElementLabelProvider;
			} else {
				System.out.println(adaptableObject.getClass().toString());
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		// if (DartCore.isPluginsBuild()) {
		// return new Class[] {IElementLabelProvider.class};
		// } else {
		// return new Class[] {IElementLabelProvider.class,
		// IElementContentProvider.class};
		// }
		return new Class[] { IElementLabelProvider.class,
				IElementContentProvider.class };
		//
	}
}
