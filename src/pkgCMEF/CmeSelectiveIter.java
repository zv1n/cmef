package pkgCMEF;

import java.util.Vector;

public class CmeSelectiveIter implements CmeIterator {
	
	/** Store the app so pull Selection Info from feedback */
	CmeApp m_App;

	/** Number pool for exclusive list */
	private Vector<Integer> m_iExclusiveList;
	
	public CmeSelectiveIter(CmeApp app) {
		m_App = app;
	}
	
	public int getNext() {
		return 0;
	}

	public boolean initIterator(int type, int lowerBound, int upperBound) {
		// populate exclusive list with entries from Selection*
		return false;
	}

	public boolean isComplete() {
		return false;
	}

}
