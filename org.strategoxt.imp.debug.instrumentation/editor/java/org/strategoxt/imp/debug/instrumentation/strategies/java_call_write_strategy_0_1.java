package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;
import org.strategoxt.stratego_lib.exit_0_0;

public class java_call_write_strategy_0_1 extends RegisterContainerStrategy {

	public static java_call_write_strategy_0_1 instance = new java_call_write_strategy_0_1();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm name)
	{
		LogHelper.vomit(context, current, "Call to strategy '" + name + "'...");
		Strategy s = this.register.get(name);
		if (s == null)
		{
			LogHelper.warn(context, current, "Strategy not found: " + name.toString());
			LogHelper.debug(context, current, "Register size " + this.register.size());
			return null;
		}
		IStrategoTerm result = null;


		exit_0_0 oldExit = exit_0_0.instance;
		// TODO: does this require a lock?
		exit_0_0.instance = new exit_0_0() {
			@Override
			public IStrategoTerm invoke(Context exitContext, IStrategoTerm exitCurrent) {
				LogHelper.error(exitContext, exitCurrent, "Called <exit>");
				// Why do you want to exit...
				return null; // just fail, we could save the exit code but that could lead to synchronization issues
				//return super.invoke(arg0, arg1);
			}
		};
		// result = context.invokeStrategyCLI(s, "Foo", args) // RL: don;t if this fixes the exit problem?
		result = s.invoke(context, current);
		exit_0_0.instance = oldExit;

		if (result == null) {
			LogHelper.error(context, current, "Failed to write ATerm in pretty-printed form to disk");
		}
		return result;
	}
}
