package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.spoofax.terms.attachments.OriginAttachment;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Returns the sort of the current term. This requires origin tracking.
 * @author rlindeman
 *
 */
public class java_get_sort_0_0 extends Strategy {
	
	public static java_get_sort_0_0 instance = new java_get_sort_0_0();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		
		String sortName = getSort(current);
		if (sortName != null)
		{
			LogHelper.vomit(context, current, "Found sort " + sortName);
    		return context.getFactory().makeString(sortName);
		} else {
			LogHelper.warn(context, current, "Could not get attachment for " + current);
			return null;
		}
	}
	
	public static String getSort(IStrategoTerm current)
	{
		
		// get term sort name
        ImploderAttachment attachment = current.getAttachment(ImploderAttachment.TYPE);
        if (attachment == null)
        {
        	// use the origin
        	OriginAttachment originAttachment = current.getAttachment(OriginAttachment.TYPE);
        	if (originAttachment != null)
        	{
        		attachment = originAttachment.getOrigin().getAttachment(ImploderAttachment.TYPE);
        	}
        }
        
        if (attachment != null)
        {
        	try {
        		String sortName = attachment.getSort();
        		//this.log(context, "Found sort " + sortName);
        		return sortName;
        	} catch(Exception e)
        	{
        		//this.log(context, "Exception while getting sort::: " + e.getMessage());
        	}
        }
        return null;
	}

}
