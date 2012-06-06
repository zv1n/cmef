package pkgCMEF;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class CmeDifficultyIter implements CmeIterator {

	private CmeApp m_App = null;
	
	private Vector<Integer> m_idxList = new Vector<Integer>();
	private int m_iPos = 0;
	
	public CmeDifficultyIter(CmeApp app) {
		m_App = app;
		m_iPos = 0;
	}
	
	@Override
	public boolean initIterator(int type, int lowerBound, int upperBound) {
		boolean descending = ((type&CmeIterator.REVERSE) == CmeIterator.REVERSE);
		CmePairFactory pf = m_App.getPairFactory();
		
		m_idxList.clear();
		m_iPos = 0;
		
		if ((type&(~CmeIterator.REVERSE)) != 0)
			return false;
		
		HashMap<Integer, Vector<Integer>> pointmap = new HashMap<Integer, Vector<Integer>>();
		Vector<Integer> plist = new Vector<Integer>();
		
		for (int x=0; x<pf.getCount(); x++) {
			int value = pf.getIntValue(x);
			Vector<Integer> pm = pointmap.get(value);
			
			if (pm == null) {
				plist.add(value);
				pm = new Vector<Integer>();
				pointmap.put(value, pm);
			}
			
			pm.add(x);
		}
		
		Collections.sort(plist);
		
		if (descending) {
			System.err.println(plist.get(0) + ":" + plist.get(1));
			Collections.reverse(plist);
		}
		
		int perpoint = 0;
		for (int x=0; x<plist.size(); x++) {
			int value = plist.get(x);
			Vector<Integer> pm = pointmap.get(value);
				
			if (pm == null) {
				System.err.println("Error: Value " + String.valueOf(value) + " returned null list!");
				return false;
			}
			
			if (perpoint == 0)
				perpoint = pm.size();
			else if (perpoint != pm.size()) {
				System.err.println("Error: There is not an equal number of point values for each element!");
				return false;
			}
		}
		
		int done = 0;
		while (done < plist.size()) {
			for (int x=0; x<plist.size(); x++) {
				int value = plist.get(x);
				Vector<Integer> vlist = pointmap.get(value);
				int size = vlist.size();
				
				if (size == 0) {
					done++;
					break;
				}	
			
				int rand = CmeRandomIter.getRange(0, size-1);
				m_idxList.add(vlist.get(rand));
				vlist.remove(rand);
			}
		}
		
		return true;
	}

	@Override
	public int getNext() {
		if (m_iPos >= m_idxList.size())
			return -1;
		
		return m_idxList.get(m_iPos++);
	}

	@Override
	public boolean isComplete() {
		return (m_iPos == m_idxList.size());
	}

}
