package org.strategoxt.imp.debug.instrumentation.strategies;

import java.util.ArrayList;
import java.util.List;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

/**
 * Helper class for {@link java_strategy_0_0}.
 */
public class InteropRegisterer extends JavaInteropRegisterer {

	public InteropRegisterer() {
		//super(new Strategy[] { java_strategy_0_0.instance });
		super(createStrategyArray());
	}

	/**
	 * Create a Strategy[] of all the array defined in this library.
	 * @return
	 */
	public static Strategy[] createStrategyArray() {
		List<Strategy> list = new ArrayList<Strategy>();
		list.add(java_strategy_0_0.instance);
		list.add(java_match_term_against_syntax_pattern_0_0.instance);
		
		list.add(java_call_gen_strategy_0_1.instance);
		list.add(java_call_extract_strategy_0_1.instance);
		
		list.add(java_register_gen_strategy_1_1.instance);
		list.add(java_register_extract_strategy_1_1.instance);
		
		list.add(java_exists_extract_strategy_0_1.instance);
		list.add(java_exists_gen_strategy_0_1.instance);
		
		list.add(java_copy_attachment_0_2.instance);

		// print the attachments to the log
		list.add(java_debug_attachments_0_0.instance);
		
		list.add(java_get_cons_0_0.instance);
		list.add(java_get_sort_0_0.instance);

		list.add(java_record_term_0_0.instance);
		list.add(java_write_recorded_terms_0_1.instance);
		
		list.add(java_term_hashcode_0_0.instance);
		return list.toArray(new Strategy[0]);
	}
}
