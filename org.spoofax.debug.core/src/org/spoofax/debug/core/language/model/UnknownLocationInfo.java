package org.spoofax.debug.core.language.model;

public class UnknownLocationInfo extends LocationInfo {

	/**
	 * Gets the 1-based start linenumber of the selection.
	 * @return
	 */
	public int getStart_line_num() {
		return -1;
	}
	
	/**
	 * Gets the 1-based start token position of the selection.
	 * The position is relative to the start linenumber.
	 * The character at position start_token_pos is included in the selection.
	 * @return
	 */
	public int getStart_token_pos() {
		return -1;
	}
	
	/**
	 * Gets the 1-based end linenumber of the selection.
	 * @return
	 */
	public int getEnd_line_num() {
		return -1;
	}
	
	/**
	 * Gets the 1-based end token position
	 * The position is relative to the end linenumber.
	 * The charachter at position (end_token_pos-1) is the last token in the selection.
	 * @return
	 */
	public int getEnd_token_pos() {
		return -1;
	}
}
