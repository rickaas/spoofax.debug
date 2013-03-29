package org.spoofax.debug.core.language.model;

import java.util.StringTokenizer;

/**
 * 
 * This class is used to specify a selection using the start linenumber, start token position and the end linenumber and end token position.
 * 
 * The linenumbers and positions are all 1-based.
 * 
 * The character at position (end_token_pos-1) is the last token in the selection.
 * 
 * @author rlindeman
 *
 */
public class LocationInfo {

	private int start_line_num = -1;
	private int start_token_pos = -1;
	private int end_line_num = -1;
	private int end_token_pos = -1;
	
	public LocationInfo(int startLineNum, int startTokenPos, int endLineNum,
			int endTokenPos) {
		start_line_num = startLineNum;
		start_token_pos = startTokenPos;
		end_line_num = endLineNum;
		end_token_pos = endTokenPos;
	}
	
	public LocationInfo(String[] locationInfo) {
		// string array should have length 4
		start_line_num = Integer.parseInt(locationInfo[0]);
		start_token_pos = Integer.parseInt(locationInfo[1]);
		end_line_num = Integer.parseInt(locationInfo[2]);
		end_token_pos = Integer.parseInt(locationInfo[3]);
	}
	
	public LocationInfo(String startLineNum, String startTokenPos, String endLineNum,
			String endTokenPos) {
		this(new String[] {startLineNum, startTokenPos, endLineNum, endTokenPos});
	}
	
	public LocationInfo() {
		
	}

	/**
	 * Gets the 1-based start linenumber of the selection.
	 * @return
	 */
	public int getStart_line_num() {
		return start_line_num;
	}

	public void setStart_line_num(int startLineNum) {
		start_line_num = startLineNum;
	}

	/**
	 * Gets the 1-based start token position of the selection.
	 * The position is relative to the start linenumber.
	 * The character at position start_token_pos is included in the selection.
	 * @return
	 */
	public int getStart_token_pos() {
		return start_token_pos;
	}

	public void setStart_token_pos(int startTokenPos) {
		start_token_pos = startTokenPos;
	}

	/**
	 * Gets the 1-based end linenumber of the selection.
	 * @return
	 */
	public int getEnd_line_num() {
		return end_line_num;
	}

	public void setEnd_line_num(int endLineNum) {
		end_line_num = endLineNum;
	}

	/**
	 * Gets the 1-based end token position
	 * The position is relative to the end linenumber.
	 * The charachter at position (end_token_pos-1) is the last token in the selection.
	 * @return
	 */
	public int getEnd_token_pos() {
		return end_token_pos;
	}

	public void setEnd_token_pos(int endTokenPos) {
		end_token_pos = endTokenPos;
	}

	@Override
	public String toString() {
		return "LocationInfo ["
				+ "start_line_num="	+ start_line_num
				+ ", "
				+ "start_token_pos=" + start_token_pos 
				+ ", "
				+ "end_line_num=" + end_line_num
				+ ", " 
				+ "end_token_pos=" + end_token_pos
				+ "]";
	}
	
	/**
	 * Returns a short version of the toString()
	 * Example: "(48,8,48,30)"
	 * @return
	 */
	public String toShortString() {
		return "("+start_line_num+","+start_token_pos+","+end_line_num+","+end_token_pos+")";
	}
	
	public boolean surrounds(int linenumber, int token_position) {
		// check if the start_line_num <= linenumber <= end_line_num
		if (start_line_num <= linenumber && linenumber <= end_line_num)
		{
			// if start_line_num == linenumber
			// token could still be outside the selection
			// when token_position < start_token_pos
			if (start_line_num == linenumber && token_position < start_token_pos)
			{
				return false;
			}

		
			// if end_line_num == linenumber
			// token could still be outside the selectoin
			// when token_position >= end_token_pos
			if (end_line_num == linenumber && token_position >= end_token_pos)
			{
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LocationInfo))
			return false;
		LocationInfo other = (LocationInfo) obj;
		boolean c1 = other.start_line_num == this.start_line_num;
		boolean c2 = other.start_token_pos == this.start_token_pos;
		boolean c3 = other.end_line_num == this.end_line_num;
		boolean c4 = other.end_token_pos == this.end_token_pos;
		if (c1 && c2 && c3 && c4)
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + start_line_num;
		result = prime * result + start_token_pos;
		result = prime * result + end_line_num;
		result = prime * result + end_token_pos;

		return result;
	}

	public static LocationInfo parse(String locationInfoString) {
		// "5,0,8,0"
		String delim = ",";
		StringTokenizer t = new StringTokenizer(locationInfoString, delim);
		if (!t.hasMoreTokens()) return new UnknownLocationInfo();
		String startLineNum = t.nextToken();
		if (!t.hasMoreTokens()) return new UnknownLocationInfo();
		String startTokenPos = t.nextToken();
		if (!t.hasMoreTokens()) return new UnknownLocationInfo();
		String endLineNum = t.nextToken();
		if (!t.hasMoreTokens()) return new UnknownLocationInfo();
		String endTokenPos = t.nextToken();
		LocationInfo parsedInfo = new LocationInfo(startLineNum, startTokenPos, endLineNum, endTokenPos);
		return parsedInfo;
	}
	
}
