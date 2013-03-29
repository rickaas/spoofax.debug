package org.spoofax.debug.core.control.java.events;

import org.spoofax.debug.interfaces.extractor.IDeserializeEventInfo;
import org.spoofax.debug.interfaces.info.IEventInfo;
import org.spoofax.debug.interfaces.java.DebugEventBase;

import com.sun.jdi.Field;
import com.sun.jdi.Value;
import com.sun.jdi.event.BreakpointEvent;

/**
 * Extract DSL EventInfo from the other vm.
 * The DSL EventInfo is contained in a field in the thisObject of the suspended thread.
 * The EventInfo is serialized as a string for performance reasons.
 * (Reconstructing an object from the other VM in this VM using reflection is painfully slow...)
 * @author rlindeman
 *
 */
public class BreakpointEventInfoExtractor extends InfoExtractorBase implements IJavaEventInfoExtractor<BreakpointEvent> {

	protected IDeserializeEventInfo deserializer = null;

	public BreakpointEventInfoExtractor(IDeserializeEventInfo deserializer) {
		this.deserializer = deserializer;
	}

	
	@Override
	public IEventInfo extract(BreakpointEvent event) {
		this.event = event;
		String eventType = (String) event.request().getProperty("event-type");
		
		// TODO: do some time profiling
		getStackFrame();
		getThisObject();
		getEventInfoField();
		getEventInfoValue();
		getEventInfoString();
		
		IEventInfo eventInfo = deserializer.deserialize(eventType, getEventInfoString());
		
		return eventInfo;
	}

	/**
	 * The object instance field that contains the string-serialized debug event info. 
	 */
	private Field eventInfoField = null;
	
	private Field getEventInfoField()
	{
		if (eventInfoField == null && this.thisObject != null)
		{
			eventInfoField = this.getThisObject().referenceType().fieldByName(DebugEventBase.EVENTINFO_FIELD);
		}
		return eventInfoField;
	}
	
	/**
	 * A string representation of the IEventInfo, this maybe faster to copy from the debug vm instead of each one separately.
	 */
	private Value eventInfoValue = null;
	
	/**
	 * This value is a String representation of the Event Info tuple (filename, name, LocationInfo)
	 * @return
	 */
	private Value getEventInfoValue()
	{
		if (this.eventInfoValue == null && this.getThisObject() != null)
		{
			Value val = this.getThisObject().getValue(getEventInfoField());
			this.eventInfoValue = val;
		}
		return this.eventInfoValue;
	}
	
	private String eventInfoString;
	
	/**
	 * The given Value should be a String object. The method returns the string contents.
	 * @param value
	 * @return
	 */
	public String getEventInfoString()
	{
		if (eventInfoString == null)
		{
			// value should be a string
			String s = getEventInfoValue().toString();
			// strip surrounding quotes
			s = s.substring(1, s.length()-1);
			
			this.eventInfoString = s;
		}
		return eventInfoString;
	}
	
	public void reset() {
		super.reset();
		this.thisObject = null;
		this.eventInfoField = null;
		this.eventInfoValue = null;
		this.eventInfoString = null;
	}
	

}
