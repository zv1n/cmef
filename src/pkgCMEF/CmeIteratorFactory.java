package pkgCMEF;

public class CmeIteratorFactory {
	
	/** Handle to main application */
	private CmeApp m_App;
	
	public CmeIteratorFactory(CmeApp app) {
		m_App = app;
	}
	
	public CmeIterator createIterator(int flags) {
		if ((flags&CmeIterator.RANDOM) == CmeIterator.RANDOM) {
			return new CmeRandomIter();
		} else if ((flags&CmeIterator.SELECTIVE) == CmeIterator.SELECTIVE) {
			return new CmeSelectiveIter(m_App);
		} else if ((flags&CmeIterator.DIFFICULTY) == CmeIterator.DIFFICULTY) {
			return new CmeDifficultyIter(m_App);
		}
		return null;
	}

}
