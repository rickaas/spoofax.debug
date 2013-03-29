package org.spoofax.debug.core.language.model;

import org.spoofax.debug.interfaces.info.IEventInfo;

public class SourceLocation {
	
	private String filename;
	private String location;
	
	private LocationInfo locationInfo = null;
	
	public SourceLocation(IEventInfo eventInfo) {
		this.filename = eventInfo.getFilename();
		this.location = eventInfo.getLocation();
		this.locationInfo = LocationInfo.parse(this.location);
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public LocationInfo getLocationInfo() {
		return this.locationInfo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SourceLocation)) return false;
		if (obj == this) return true;
		
		SourceLocation other = (SourceLocation) obj;
		if (!this.getLocation().equals(other.getLocation()));
		if (!this.getFilename().equals(other.getFilename()));
		if (!this.getLocationInfo().equals(other.getLocationInfo()));
		
		return true;
	}
}
