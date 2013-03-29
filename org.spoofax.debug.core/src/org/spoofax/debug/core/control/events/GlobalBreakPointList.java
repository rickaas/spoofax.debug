package org.spoofax.debug.core.control.events;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * All breakpoints for a specific dsl
 * @author rlindeman
 *
 */
public class GlobalBreakPointList {

	private List<AbstractBreakPoint> breakPoints = null;
	//private Collection<BreakPoint> bp = null;
	
	/**
	 * Creates a new GlobalBreakPointList, it contains the active breakpoints.
	 */
	public GlobalBreakPointList()
	{
		// Make a thread safe collection, after each add make a new copy.
		this.breakPoints = new CopyOnWriteArrayList<AbstractBreakPoint>();
	}

	public void add(AbstractBreakPoint b)
	{
		this.breakPoints.add(b);
	}
	
	public boolean remove(AbstractBreakPoint b)
	{
		boolean ret = true;
		if (this.breakPoints.contains(b))
		{
			this.breakPoints.remove(b);
		} else
		{
			AbstractBreakPoint pbRemove = null;
			for(AbstractBreakPoint bp : this.breakPoints)
			{
				boolean sameFile = bp.getFilename().equals(b.getFilename());
				boolean sameLine = bp.getLineNumber() == b.getLineNumber();
				if (sameFile && sameLine)
				{
					pbRemove = bp;
				}
			}
			boolean removed = this.breakPoints.remove(pbRemove);
			if (!removed)
			{
				System.out.println("Second remove also failed!");
			}
		}
		return ret;
	}
	
	public boolean contains(AbstractBreakPoint b) {
		return this.breakPoints.contains(b);
	}
	
	public Collection<AbstractBreakPoint> getBreakPoints()
	{
		return this.breakPoints;
	}
	
	public void clear()
	{
		this.breakPoints.clear();
	}
}
