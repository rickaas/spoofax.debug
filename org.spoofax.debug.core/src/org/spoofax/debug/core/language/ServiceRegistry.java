package org.spoofax.debug.core.language;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;

/**
 * Register a Language with the Spoofax Debugger
 * @author rlindeman
 *
 */
public class ServiceRegistry {

	// TODO: use synchronized map?
	private Map<String, IDebugServiceFactory> services = new HashMap<String, IDebugServiceFactory>();
	
	public void f()
	{
		Language language = null;
		for(Language l : LanguageRegistry.getLanguages())
		{
			
		}
	}
	
	public IDebugServiceFactory getServiceFactory(String languageID)
	{
		if (this.services.containsKey(languageID))
		{
			return this.services.get(languageID);
		} 
		else 
		{
			// language not registered.
			return null;
		}
	}
	
	public void register(String languageID, IDebugServiceFactory debugServiceFactory)
	{
		if (this.services.containsKey(languageID))
		{
			// language already registered
		} else {
			this.services.put(languageID, debugServiceFactory);
		}
	}
}
