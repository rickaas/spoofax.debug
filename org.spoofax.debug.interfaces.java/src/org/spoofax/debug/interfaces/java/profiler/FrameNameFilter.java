package org.spoofax.debug.interfaces.java.profiler;

import java.util.ArrayList;
import java.util.List;

public class FrameNameFilter {

	private List<String> filters = new ArrayList<String>();
	
	public void addFilter(String filter) {
		if (!filters.contains(filter)) {
			this.filters.add(filter);
		}
	}
	
	public boolean exists(String filter) {
		return filters.contains(filter);
	}
	
	private static FrameNameFilter instance = null;
	
	public static void reset() {
		instance = null;
	}
	
	public static FrameNameFilter instance() {
		if (instance == null) {
			instance = new FrameNameFilter();
		}
		return instance;
	}
}
