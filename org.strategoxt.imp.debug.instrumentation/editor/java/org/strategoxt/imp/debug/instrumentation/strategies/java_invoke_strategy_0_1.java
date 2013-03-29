package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.core.InterpreterErrorExit;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.InterpreterExit;
import org.spoofax.interpreter.core.UndefinedStrategyException;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.HybridInterpreter;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Invoke a stratego strategy via its name. The strategy cannot have any arguments.
 * @author rlindeman
 *
 */
public class java_invoke_strategy_0_1 extends Strategy {

	public static java_invoke_strategy_0_1 instance = new java_invoke_strategy_0_1();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm name)
	{
		//context.getIOAgent().printError("INVOKE VIA STRING: " + name.toString());
		String strategyName = name.toString();
		if (name instanceof IStrategoString){
			strategyName = ((IStrategoString)name).stringValue();
			//context.getIOAgent().printError("INVOKE VIA STRING: " + strategyName);
		}
		// create a new HybridInterpreter from the current content.
		HybridInterpreter interpreter = HybridInterpreter.getInterpreter(context);
		interpreter.setCurrent(current);
		boolean succeeded = false;
		try {
			succeeded = interpreter.invoke(strategyName);
			// TODO: clone Context: invoke failure will reset the Context stackdepth
		} catch (InterpreterErrorExit e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterpreterExit e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UndefinedStrategyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterpreterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//interpreter.uninit(); // RL: does this also destroy our Context?
		//context.getIOAgent().printError("DID THE INVOKE SUCCEED? " + succeeded);
		if (succeeded) {
			return interpreter.getContext().current();
		} else {
			return null;
		}
		
	}
}

