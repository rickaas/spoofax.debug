package org.strategoxt.imp.debug.instrumentation.strategies;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Write all recorded term to a text file.
 * @author rlindeman
 *
 */
public class java_write_recorded_terms_0_1 extends Strategy {

	public static java_write_recorded_terms_0_1 instance = new java_write_recorded_terms_0_1();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm filename)
	{
		try {
			//System.out.println("Writing recorded terms " + filename.toString());
			java_record_term_0_0.instance.writeRecordedTermsToFile((IStrategoString) filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return current;
	}
}
