package pkgCMEF;

import java.util.Random;
import java.util.Vector;

public class CmeRandomIter implements CmeIterator {

	/** Lower number limit */
	private int m_iLowerBound;
	
	/** Upper number limit */
	private int m_iUpperBound;
	
	/** Random generation type */
	private int m_iType;
	
	/** Number pool for exclusive list */
	private Vector<Integer> m_iExclusiveList;
	
	/** Random number gen */
	private static Random m_RandomGen;
	
	public boolean initIterator(int type, int lowerBound, int upperBound) {
		
		m_iUpperBound = upperBound;
		m_iLowerBound = lowerBound;
		m_iType = type;
		
		boolean ret = true;
		
		switch(m_iType) {
		case CmeRandomIter.EXCLUSIVE:
			ret = genExclusive();
			break;
		}
		
		return ret;
	}
	
	public CmeRandomIter() {
		m_iType = -1;
	}
	
	public static int getRange(int low, int high) {
		int range = high-low+1;

		if (m_RandomGen == null)
			m_RandomGen = new Random();
		
		if (range == 0)
			return low;

		return ((Math.abs(m_RandomGen.nextInt()) % range) + low);
	}

	private boolean genExclusive() {
		m_iExclusiveList = new Vector<Integer>(m_iUpperBound-m_iLowerBound);
		for(int x = m_iLowerBound; x <= m_iUpperBound; x++)
			m_iExclusiveList.add(x);

		return true;
	}
	
	private int getExclusive() {
		if (m_iExclusiveList == null)
			return -2;

		if (m_iExclusiveList.size() == 0)
			return -1;
		
		int idx = getRange(0, m_iExclusiveList.size()-1);
		int rand = m_iExclusiveList.get(idx);

		m_iExclusiveList.remove(idx);
		
		return rand;
	}
	
	private int getNonExclusive() {
		return getRange(m_iLowerBound, m_iUpperBound);
	}
	
	/**
	 * Get the next random number in the sequence.
	 * @return next random integer; -1 if invalid.
	 */
	public int getNext() {
		if (m_iType == CmeIterator.NONEXCLUSIVE) {
			return getNonExclusive();
		}
		return getExclusive();
	}

	/** 
	 * This only tracks completeness for EXCLUSIVE sets
	 * @return true if Exclusive and complete
	 */
	public boolean isComplete() {
		if (m_iExclusiveList != null && m_iExclusiveList.size() == 0)
			return true;
		return false;
	}
}
