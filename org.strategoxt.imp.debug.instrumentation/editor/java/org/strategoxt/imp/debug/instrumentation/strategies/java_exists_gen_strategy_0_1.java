package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;

/**
 * Checks if the given strategy name is registed as generate strategy.
 * @author rlindeman
 *
 */
public class java_exists_gen_strategy_0_1 extends RegisterContainerStrategy {

	public static java_exists_gen_strategy_0_1 instance = new java_exists_gen_strategy_0_1();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm name)
	{
		if (this.register.hasStrategy(name))
		{
			return current;
		} else {
			return null;
		}
	}
}
