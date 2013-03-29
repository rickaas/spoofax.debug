package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Manually copy the Imploder attachment from on term to another. 
 * This is only needed when origin-tracking is not available.
 * @author rlindeman
 *
 */
public class java_copy_attachment_0_2 extends Strategy {

	public static java_copy_attachment_0_2 instance = new java_copy_attachment_0_2();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			// arguments
			IStrategoTerm source
			,IStrategoTerm destination) {
		//source.getAttachment(TermA)
		LogHelper.vomit(context, current, "java_copy_attachment_0_2");
		// copy attachments from source to destination
		// attachments keep track of the sort
		
		ImploderAttachment attachment = extractImploderAttachment(context, source);
		if (attachment == null) return null;
		
		//this.log(context, "Found imp-att. : " + attachment);

		setImploderAttachment(context, attachment, destination);
		//this.log(context, "PRINT DEST IMP ATT");
		//IStrategoAppl destImp = this.extractImploderAttachmentAppl(context, destination);
		//this.log(context, "PRINT:    " + destImp);

		//this.log(context, "DONE");
		return current;
	}
	
	protected void debugImpAtt(Context context, IStrategoTerm term)
	{
        ImploderAttachment attachment = term.getAttachment(ImploderAttachment.TYPE);
        if (attachment != null)
        {
        	String sort = attachment.getSort();
        	LogHelper.vomit(context, term , "SORT              " + sort);
        	String leftToken = attachment.getLeftToken().toString();
        	LogHelper.vomit(context, term, "LEFT              " + leftToken);
        }		
	}
	
	private void setImploderAttachment(Context context, ImploderAttachment sourceAttachment, IStrategoTerm destination)
	{
		String sort = sourceAttachment.getSort();
		org.spoofax.jsglr.client.imploder.IToken leftToken = sourceAttachment.getLeftToken();
		org.spoofax.jsglr.client.imploder.IToken rightToken = sourceAttachment.getRightToken();
		
		ImploderAttachment.putImploderAttachment(destination, false, sort, leftToken, rightToken);
		
	}

	/*
	// TODO: Do not use!
	protected void setImploderAttachment(Context context, IStrategoAppl appl, IStrategoTerm destination)
	{
		// convert a term back to an attachment
		this.log(context, " SSSSSSSSSSSS" + appl.getAllSubterms()[5]);
		this.log(context, " 00000000000000 000 " + appl.getAllSubterms()[0]);
		String jj = org.spoofax.terms.Term.asJavaString(appl.getAllSubterms()[0]);
		this.log(context, "AS_JAVA_STINF:    " + jj);
		// next will drop the sort? WHY o why? :(
		ImploderAttachment attachment = ImploderAttachment.TYPE.fromTerm(appl);
		this.log(context, "ATT BEFORE SETTING: " + attachment);
		destination.putAttachment(attachment);
		
		
	}*/
	
	private ImploderAttachment extractImploderAttachment(Context context, IStrategoTerm term)
	{
		return ImploderAttachment.get(term);
	}
//	// no used anymore
//	private IStrategoAppl extractImploderAttachmentAppl(Context context, IStrategoTerm term)
//	{
//		//ImploderAttachment.get(term); // alternative
//        ImploderAttachment attachment = term.getAttachment(ImploderAttachment.TYPE);
//        IStrategoAppl appl = null;
//        if (attachment != null)
//        {
//        	//ITermFactory factory = null;
//        	ITermFactory factory = context.getFactory();
//        	appl = ImploderAttachment.TYPE.toTerm(factory, attachment);
//        }
//        return appl;
//	}
}
