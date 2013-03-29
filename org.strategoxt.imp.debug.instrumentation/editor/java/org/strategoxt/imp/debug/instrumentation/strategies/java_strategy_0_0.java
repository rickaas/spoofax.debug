package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Example Java strategy implementation.
 * 
 * This strategy can be used by editor services and can be called in Stratego
 * modules by declaring it as an external strategy as follows:
 * 
 * <code>
 *  external java-strategy(|)
 * </code>
 * 
 * @see InteropRegisterer This class registers java_strategy_0_0 for use.
 */
public class java_strategy_0_0 extends Strategy {

	public static java_strategy_0_0 instance = new java_strategy_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		// AstSortInspector i = new AstSortInspector(current);
		
		//context.getIOAgent().printError("Input for java-strategy: " + current);
		// context.getIOAgent().internalGetOutputStream(IOAgent.CONST_STDOUT);

		LogHelper.vomit(context, current, "term: " + current);
		ImploderAttachment attachment = current
				.getAttachment(ImploderAttachment.TYPE);
		LogHelper.vomit(context, current, "got attachment");
		IStrategoTerm result = null;
		if (attachment != null) {
			String sort = attachment.getSort();
			LogHelper.vomit(context, current, "got sort from attachment");
			ITermFactory factory = context.getFactory();
			LogHelper.vomit(context, current, "got factory from context");
			result = factory.makeString(sort);
			LogHelper.vomit(context, current, "create result string");
		} else {
			LogHelper.vomit(context, current, "create NO RESULT string");
			result = context.getFactory().makeString("NO RESULT");
		}
		LogHelper.vomit(context, current, "finish java_strategy_0_0");
		return result;
	}


}
