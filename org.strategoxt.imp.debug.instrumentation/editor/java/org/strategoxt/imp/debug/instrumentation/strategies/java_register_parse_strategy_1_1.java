package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

public class java_register_parse_strategy_1_1 extends RegisterContainerStrategy {

	public static java_register_parse_strategy_1_1 instance = new java_register_parse_strategy_1_1();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			Strategy strategy, IStrategoTerm name) {
		LogHelper.debug(context, current, "ADD TO REGISTER " + name);
		if (this.register == null)
		{
			LogHelper.debug(context, current, "REGISTER WITHOUT REGISTER");
			// TODO: initialize register
			return null;
		}
		this.register.add(name, strategy);
		LogHelper.debug(context, current, "REGISTERED");
		return name;
	}
}
