package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Calculates the java hashcode of the current term.
 * @author rlindeman
 *
 */
public class java_term_hashcode_0_0 extends Strategy{

	public static java_term_hashcode_0_0 instance = new java_term_hashcode_0_0();
	
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		return context.getFactory().makeString(""+current.hashCode());
	}
}
