package pkgCMEF;

import java.util.Random;
import java.util.Vector;

public class CmeSelectiveIter implements CmeIterator {
	
	/** Store the app so pull Selection Info from feedback */
	CmeApp m_App;

	/** Number pool for exclusive list */
	private Vector<Integer> m_iExclusiveList = null;
	private Vector<Integer> m_iExclusiveBase = null;
	
	/** Random number gen */
	private Random m_RandomGen;
	
	/** Lower Bound */
	private int m_LowerBound;
	/** Upper Bound */
	private int m_UpperBound;
	
	public CmeSelectiveIter(CmeApp app) {
		m_App = app;
	}
	
	public boolean setList(String str) {
		m_iExclusiveBase = new Vector<Integer>();
		
		System.out.println("Setting list: '" + str + "'");
		String[] items = str.split(",");
		for (int x=0; x<items.length; x++) {
			m_iExclusiveBase.add(Integer.valueOf(items[x]));
		}
		System.out.println("ExclusiveCount: " + String.valueOf(m_iExclusiveBase.size()));
		return true;
	}
	
	/** Get the random value within range */
	private int getRange(int low, int high) {
		int range = high-low+1;

		if (m_RandomGen == null)
			m_RandomGen = new Random();
		
		if (range == 0)
			return low;

		return ((Math.abs(m_RandomGen.nextInt()) % range) + low);
	}
	
	public int getNext() {
		if (m_iExclusiveList == null) {
			initSecondary();
		}

		if (m_iExclusiveList.size() == 0)
			return -1;
		
		int idx = getRange(0, m_iExclusiveList.size()-1);
		int rand = m_iExclusiveList.get(idx);
		
		System.out.println(m_iExclusiveList.size());

		m_iExclusiveList.remove(idx);
		
		return rand;
	}

	private boolean initSecondary() {
		
		// populate exclusive list with entries from Select*
		//* Always exclusive
		
		if (m_iExclusiveBase != null) {
			m_iExclusiveList = (Vector<Integer>) m_iExclusiveBase.clone();
			return true;
		}
		
		m_iExclusiveList = new Vector<Integer>();
		
		/* We don't care about the Lower and Upper bounds... */
		for(int x=m_LowerBound; x<=m_UpperBound; x++) {
			String selString = "Select";
			selString += Integer.toString(x + 1);
			
			String value = m_App.getFeedback(selString);
			if (value == null)	continue;
			
			if (!value.equals("")) {
				try {
					int sel = Integer.parseInt(value);
					m_iExclusiveList.add(sel);
				} catch (Exception ex) {
					System.out.println("Failed to aprse the selected value...");
					continue;
				}
			}
		}
		
		return (m_iExclusiveList.size() > 0);
	}
	
	public boolean initIterator(int lowerBound, int upperBound) {
		m_LowerBound = lowerBound;
		m_UpperBound = upperBound;
		return true;
	}

	public boolean isComplete() {
		if (m_iExclusiveList == null)
			return true;
		return (m_iExclusiveList.size() == 0);
	}
	
	public void reset() {
		m_iExclusiveList = null;
	}

	public int count() {
		return m_iExclusiveList.size();
	}
}
