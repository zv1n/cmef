package pkgCMEF;

import java.util.Random;
import java.util.Vector;

public class CmeRandom implements CmeIterator {
	
	/** Exclusive - no repetition */
	public static final int EXCLUSIVE = 0;
	
	/** Non exclusive (possible repetition) */
	public static final int NONEXCLUSIVE = 1;

	/** Lower number limit */
	private int m_iLowerBound;
	
	/** Upper number limit */
	private int m_iUpperBound;
	
	/** Random generation type */
	private int m_iType;
	
	/** Number pool for exclusive list */
	private Vector<Integer> m_iExclusiveList;
	
	/** Random number gen */
	private Random m_RandomGen;
	
	public CmeRandom(int type, int lower, int upper) {
		m_iUpperBound = upper;
		m_iLowerBound = lower;
		m_iType = type;
		
		switch(m_iType) {
		case CmeRandom.EXCLUSIVE:
			genExclusive();
			break;
		}
	}
	
	private int getRange(int low, int high) {
		int range = high-low;

		if (m_RandomGen == null)
			m_RandomGen = new Random();
		
		if (range == 0)
			return low;

		return ((Math.abs(m_RandomGen.nextInt()) % range) + low);
	}

	private void genExclusive() {
		m_iExclusiveList = new Vector<Integer>(m_iUpperBound-m_iLowerBound);
		for(int x = m_iLowerBound; x < m_iUpperBound; x++)
			m_iExclusiveList.add(x);
	}
	
	private int getExclusive() {
		if (m_iExclusiveList == null || m_iExclusiveList.size() == 0)
			return -1;
		
		int idx = getRange(0, m_iExclusiveList.size()-1);
		int rand = m_iExclusiveList.get(idx);
		
		m_iExclusiveList.remove(idx);
		
		System.out.println("idx:" + Integer.toString(idx));
		
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
		switch(m_iType) {
		case CmeRandom.EXCLUSIVE:
			return getExclusive();
		case CmeRandom.NONEXCLUSIVE:
			return getNonExclusive();
		}
		return -1;
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
