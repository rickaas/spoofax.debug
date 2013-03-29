package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Match the sel pattern (sort, constructor) against a term.
 * If the term matches the pattern, continue, else fail.
 * @author rlindeman
 *
 */
public class java_match_term_against_syntax_pattern_0_0 extends Strategy {

	public static java_match_term_against_syntax_pattern_0_0 instance = new java_match_term_against_syntax_pattern_0_0();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		// current is a Tuple: (term, syntax-pattern)
		if (current.getTermType() != IStrategoTerm.TUPLE)
		{
			// not a tuple! ignore
			return null;
		}
		
		IStrategoTerm term = current.getSubterm(0);
		IStrategoTerm syntaxPattern = current.getSubterm(1);
		
		String consName = null;
		String sortName = null;
		
		// get term constructor name
		if (term.getTermType() != IStrategoTerm.APPL)
		{
			// TODO: could also be a list or tuple wich does not require a constructor!
			//this.log(context, "Not able to get constructor for " + term);
			return null;
		} 
		else
		{
			IStrategoAppl appl = (IStrategoAppl) term;
			consName = appl.getConstructor().getName();
			//this.log(context, "Found constructor " + consName);
		}

		// get term sort name
		sortName = java_get_sort_0_0.getSort(term);
        
        boolean result = isMatch(syntaxPattern, sortName, consName);
        //String m = "sp: " + syntaxPattern.toString() + " s:" + sortName + " c:" + consName;
        //System.out.println(""+ System.currentTimeMillis() + ": " + result + " " + m);
        //this.log(context, m);
        if (result)
        {
        	//return current;
        	return term; // the matched term
        } else
        {
        	// not equal, so we fail!
        	return null;
        }
	}
	
	public static boolean isMatch(IStrategoTerm syntaxPattern, String sort, String cons)
	{
		// syntax pattern can be one of the following structures
		//	SortAndConstructor(Empty(), Constructor("Fail"))
		//	SortAndConstructor(Sort("Strategy"), Constructor("Fail"))
		//	SortAndConstructor(Sort("StrategyDef"),Empty())
		// get sort from pattern
		if (syntaxPattern.getTermType() != IStrategoTerm.APPL)
		{
			return false;
		}
		IStrategoAppl appl = (IStrategoAppl) syntaxPattern;
		String sac = appl.getConstructor().getName();
		if (!SortAndConstructor.equals(sac))
		{
			// syntaxPattern is invalid
			return false;
		}
		IStrategoTerm sortPart = appl.getSubterm(0);
		IStrategoTerm consPart = appl.getSubterm(1);
		if (sortPart.getTermType() != IStrategoTerm.APPL)
		{
			return false;
		} else if (consPart.getTermType() != IStrategoTerm.APPL)
		{
			return false;
		}
		
		IStrategoAppl sortPattern = (IStrategoAppl) sortPart;
		IStrategoAppl consPattern = (IStrategoAppl) consPart;
		boolean sortIsMatch = false;
		boolean consIsMatch = false;
		//String n = sortPattern.getName();
		//System.out.println("SORT NAME: " + n);
		//n = consPattern.getName();
		//System.out.println("CONS NAME: " + n);
		
		// match sort
		if (sortPattern.getConstructor().getName().equals("Empty"))
		{
			// wildcard match
			sortIsMatch = true;
		} else 
		{
			String sortNamePattern = sortPattern.getSubterm(0).toString();
			// trim quotes
			sortNamePattern = sortNamePattern.substring(1, sortNamePattern.length() - 1);
			sortIsMatch = sortNamePattern.equals(sort);
			//System.out.println("" + sortNamePattern + " == " + sort);
		}
		
		// match constructor
		if (consPattern.getConstructor().getName().equals("Empty"))
		{
			// wildcard match
			consIsMatch = true;
		} else 
		{
			String consNamePattern = consPattern.getSubterm(0).toString();
			// trim quotes
			consNamePattern = consNamePattern.substring(1, consNamePattern.length() - 1);
			consIsMatch = consNamePattern.equals(cons);
			//System.out.println("" + consNamePattern + " == " + cons);

		}
		//System.out.println("" + sort + "." +  cons + ": " + (sortIsMatch && consIsMatch) + " P:"+syntaxPattern.toString());
		return sortIsMatch && consIsMatch;
	}
	
	public static String SortAndConstructor = "SortAndConstructor";
	
}
