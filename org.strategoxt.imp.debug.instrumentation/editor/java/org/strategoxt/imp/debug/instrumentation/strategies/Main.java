package org.strategoxt.imp.debug.instrumentation.strategies;

import org.strategoxt.lang.Context;

public class Main {

	public static void init(Context context) {
		// Called when the editor is being initialized
		
		// the genRegister will keep a HashMap of strategy name mapped to a Strategy
		// each Strategy in genRegister is used by the debug-instrumentation tool as an extract-transformation
		// Calling java-register-gen-strategy(s|t) will add a new Strategy s
		// Calling java-call-gen-strategy(|t) will execute the strategy
		// we use this Register to dynamically call strategies by names
		SELStrategyRegister genRegister = SELStrategyRegister.create();
		
		// the java-register-gen-strategy(s|t) and java-call-gen-strategy(|t) both operate on the same register
		java_register_gen_strategy_1_1.instance.setRegister(genRegister);
		java_call_gen_strategy_0_1.instance.setRegister(genRegister);
		java_exists_gen_strategy_0_1.instance.setRegister(genRegister);
		
		// the genRegister will keep a HashMap of strategy name mapped to a Strategy
		// each Strategy in extractRegister is used by the debug-instrumentation tool as an generate-transformation
		SELStrategyRegister extractRegister = SELStrategyRegister.create();
		java_register_extract_strategy_1_1.instance.setRegister(extractRegister);
		java_call_extract_strategy_0_1.instance.setRegister(extractRegister);
		java_exists_extract_strategy_0_1.instance.setRegister(extractRegister);
		
		SELStrategyRegister parseRegister = SELStrategyRegister.create();
		java_register_parse_strategy_1_1.instance.setRegister(parseRegister);
		java_call_parse_strategy_0_1.instance.setRegister(parseRegister);
	
		SELStrategyRegister writeRegister = SELStrategyRegister.create();
		java_register_write_strategy_1_1.instance.setRegister(writeRegister);
		java_call_write_strategy_0_1.instance.setRegister(writeRegister);

		SELStrategyRegister postRegister = SELStrategyRegister.create();
		java_register_post_instrumentation_strategy_1_1.instance.setRegister(postRegister);
		java_call_post_instrumentation_strategy_0_1.instance.setRegister(postRegister);

	}
	

}
