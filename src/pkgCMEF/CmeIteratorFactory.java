package pkgCMEF;

public class CmeIteratorFactory {
	
	/** Handle to main application */
	private CmeApp m_App;
	
	public CmeIteratorFactory(CmeApp app) {
		m_App = app;
	}
	
	public CmeIterator createIterator(int flags) {
		if ((flags&CmeIterator.RANDOM) == CmeIterator.RANDOM) {
			return new CmeRandom();
		}
		return null;
	}

}
