package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.stratego_lib.dbg_0_1;
import org.strategoxt.stratego_lib.err_msg_0_1;
import org.strategoxt.stratego_lib.log_0_3;
import org.strategoxt.stratego_lib.notice_msg_0_1;
import org.strategoxt.stratego_lib.warn_msg_0_1;

public class LogHelper {

	public static void vomit(Context context, IStrategoTerm current, String message) {
		IStrategoTerm m = context.getFactory().makeString(message);
		IStrategoConstructor vomit = context.getFactory().makeConstructor("Vomit", 0);
		IStrategoAppl appl = context.getFactory().makeAppl(vomit);
		log_0_3.instance.invoke(context, current, appl, m, current);
		//dbg_0_1.instance.invoke(context, current, m);
	}

	public static void debug(Context context, IStrategoTerm current, String message) {
		IStrategoTerm m = context.getFactory().makeString(message);
		dbg_0_1.instance.invoke(context, current, m);
	}

	public static void notice(Context context, IStrategoTerm current, String message) {
		IStrategoTerm m = context.getFactory().makeString(message);
		notice_msg_0_1.instance.invoke(context, current, m);
	}

	
	public static void warn(Context context, IStrategoTerm current, String message) {
		IStrategoTerm m = context.getFactory().makeString(message);
		warn_msg_0_1.instance.invoke(context, current, m);
	}

	
	public static void error(Context context, IStrategoTerm current, String message) {
		IStrategoTerm m = context.getFactory().makeString(message);
		err_msg_0_1.instance.invoke(context, current, m);
	}

}
