package org.spoofax.debug.core.language.model;

public class Value {

	private String stringValue;
	
	private Object value;
	
	public Value(Object value) {
		this.value = value;
	}
	
	public Value(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public String getStringValue() {
		if (this.stringValue == null) {
			// TODO: use a language specific Object to Value converter
			if (this.value != null) {
				this.stringValue = this.value.toString();
			}
		}
		return this.stringValue;
	}
}
