package pkgCMEF;

public class CmeDifficultyIter implements CmeIterator {

	private CmeApp m_App = null;
	
	public CmeDifficultyIter(CmeApp app) {
		m_App = app;
	}
	
	@Override
	public boolean initIterator(int type, int lowerBound, int upperBound) {
		return false;
	}

	@Override
	public int getNext() {
		return 0;
	}

	@Override
	public boolean isComplete() {
		return false;
	}

}
