package org.strategoxt.imp.debug.instrumentation.strategies;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class java_list_runtime_properties_0_0 extends Strategy {

	public static java_list_runtime_properties_0_0 instance = new java_list_runtime_properties_0_0();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = RuntimemxBean.getInputArguments();
	
		List<IStrategoTerm> terms = new ArrayList<IStrategoTerm>();
		for(String arg : arguments) {
			terms.add(context.getFactory().makeString(arg));
		}
		
		Properties p = System.getProperties();
		@SuppressWarnings("rawtypes")
		Enumeration keys = p.keys();
		while (keys.hasMoreElements()) {
		  String key = (String)keys.nextElement();
		  String value = (String)p.get(key);
		  terms.add(context.getFactory().makeString(key + ": " + value));
		}
		
		return context.getFactory().makeList(terms);
	}
}
