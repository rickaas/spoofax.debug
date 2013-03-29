package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * This strategy returns the name of the constructor of the given term.
 * If the current term does not have a constructor this strategy fails.
 * For example primitive values and lists do not have a constructor.
 * 
 * @author rlindeman
 *
 */
public class java_get_cons_0_0 extends Strategy {

	public static java_get_cons_0_0 instance = new java_get_cons_0_0();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		
		String consName = null;
		
		// get term constructor name
		if (current.getTermType() != IStrategoTerm.APPL)
		{
			// TODO: could also be a list or tuple wich does not require a constructor!
			//this.log(context, "Not able to get constructor for " + current);
			return null;
		} 
		else
		{
			IStrategoAppl appl = (IStrategoAppl) current;
			consName = appl.getConstructor().getName();
			//this.log(context, "Found constructor " + consName);
			return context.getFactory().makeString(consName);
		}
	}

}
