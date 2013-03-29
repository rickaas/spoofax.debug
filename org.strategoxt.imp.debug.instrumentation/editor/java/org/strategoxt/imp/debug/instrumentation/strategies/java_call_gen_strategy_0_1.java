package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Invoke a registered generate strategy via its name.
 * @author rlindeman
 *
 */
public class java_call_gen_strategy_0_1 extends RegisterContainerStrategy {

	public static java_call_gen_strategy_0_1 instance = new java_call_gen_strategy_0_1();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm name)
	{
		LogHelper.vomit(context, current, "Call to strategy '" + name + "'...");
		Strategy s = this.register.get(name);
		if (s == null)
		{
			LogHelper.warn(context, current, "Strategy not found: " + name.toString());
			LogHelper.debug(context, current, "Register size " + this.register.size());
		}
		
		IStrategoTerm result = s.invoke(context, current);
		return result;
	}
}
