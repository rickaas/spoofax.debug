package org.strategoxt.imp.debug.instrumentation.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Print the attachment for the current term
 * @author rlindeman
 *
 */
public class java_debug_attachments_0_0 extends Strategy {

	public static java_debug_attachments_0_0 instance = new java_debug_attachments_0_0();

	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current)
	{
		this.printAttachments(context, current);
		return current;
	}
	
	private void printAttachments(Context context, IStrategoTerm source)
	{
		TermAttachmentType<?>[] types = TermAttachmentType.getKnownTypes();
		for(TermAttachmentType<?> t : types)
		{
			ITermAttachment att = source.getAttachment(t);
			if (att != null)
			{
				LogHelper.debug(context, source, "===================");
				LogHelper.debug(context, source, "Attachment Type   : " + t.toString());
				LogHelper.debug(context, source, "Attachment        : " + att.toString());
				/*
				try {
					destination.putAttachment((ITermAttachment) att.clone());
				} catch (CloneNotSupportedException e) {
					this.log(context, "Cloning not supported");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.log(context, "Done with put attachment");
				*/
			}
		}
		
	}


}
