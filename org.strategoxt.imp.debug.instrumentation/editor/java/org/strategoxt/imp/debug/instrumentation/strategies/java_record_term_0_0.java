package org.strategoxt.imp.debug.instrumentation.strategies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Saves a reference of the term is a list.
 * @author rlindeman
 *
 */
public class java_record_term_0_0 extends Strategy {

	public static java_record_term_0_0 instance = new java_record_term_0_0();
	
	private List<IStrategoTerm> recorded = new ArrayList<IStrategoTerm>();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
		recorded.add(current);
		return current;
	}
	
	public void writeRecordedTermsToFile(IStrategoString filename) throws IOException {
		File f = new File(filename.stringValue());

		//System.out.println("FILE: " + f.getAbsolutePath());
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new FileWriter(f));
			for(IStrategoTerm term : this.recorded) {
				w.write("==================" + term.hashCode());
				w.newLine();
				w.write(term.toString());
				w.newLine();
				ITermAttachment att = term.getAttachment((TermAttachmentType<ITermAttachment>)null);
				while(att != null)
				{
					w.write("ATT TYPE:"  + att.getAttachmentType());
					w.newLine();
					w.write(att.toString());
					w.newLine();
					att = att.getNext();
				}
				w.write("==================");
				w.newLine();
			}
			w.flush();
		} finally {
			if (w != null) {
				w.close();
			}
		}
	}
}
