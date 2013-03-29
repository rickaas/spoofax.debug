package org.spoofax.debug.interfaces.extractor;

import org.spoofax.debug.interfaces.info.IEventInfo;

/**
 * The IDeserializeEventInfo deserializes eventinfo that is encoded within a String to an IEventInfo object.
 * For fast retrievel from another VM this event info data is serialized to a String.
 * @author rlindeman
 *
 */
public interface IDeserializeEventInfo {

	public IEventInfo deserialize(String eventType, String eventInfoData);
}
