package org.strategoxt.imp.debug.instrumentation.strategies;

import org.strategoxt.lang.Strategy;

/**
 * Subclass this class when you want your strategy to lookup a strategy in the register.
 * @author rlindeman
 *
 */
public class RegisterContainerStrategy extends Strategy {

	protected SELStrategyRegister register = null;

	protected void setRegister(SELStrategyRegister register)
	{
		this.register = register;
	}
}
