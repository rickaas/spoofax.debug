package org.strategoxt.imp.debug.instrumentation.strategies;

import java.util.HashMap;
import java.util.Map;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Strategy;

/**
 * Register a Strategy with its name in the SELStrategyRegister
 * to make it available for dynamic calls using call-extract-strategy(|strategy-name)
 * 
 * Which extractor/generator strategies that have to be called depend on the SEL-specification, we have to use dynamic lookup at runtime.
 * @author rlindeman
 *
 */
public class SELStrategyRegister {
	

	public static SELStrategyRegister create()
	{
		return new SELStrategyRegister();
	}
	
	private Map<IStrategoTerm, Strategy> register = null;
	
	private SELStrategyRegister()
	{
		this.register = new HashMap<IStrategoTerm, Strategy>();
	}
	
	public void add(IStrategoTerm name, Strategy strategy)
	{
		this.register.put(name, strategy);
	}
	
	public Strategy get(IStrategoTerm name)
	{
		return this.register.get(name);
	}
	
	public boolean hasStrategy(IStrategoTerm name)
	{
		return this.register.containsKey(name);
	}
	
	public int size()
	{
		return this.register.size();
	}
}
