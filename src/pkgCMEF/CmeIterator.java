package pkgCMEF;

public interface CmeIterator {
	
	/** Exclusive - no repetition */
	public static final int RANDOM = 0x1;

	/** Exclusive (no possible repetition) */
	public static final int EXCLUSIVE = 0x2;
	
	/** Non exclusive (possible repetition) */
	public static final int NONEXCLUSIVE = 0x4;

	/** Selective */
	public static final int SELECTIVE = 0x8;

	/** Selective */
	public static final int DIFFICULTY = 0x10;
	
	/** Selective */
	public static final int REVERSE = 0x10;
	
	/**
	 * Initialize the iterator with upper and lower bounds.
	 *
	 * @param lowerBound - the lower range bound of numbers
	 * @param upperBound - the upper range bound of numbers
	 * 
	 * @return true on success; false else
	 */
	public boolean initIterator(int lowerBound, int upperBound);
	
	/**
	 * Get the next random number in the sequence.
	 * 
	 * @return next random integer; -1 if invalid.
	 */
	public int getNext();

	/** 
	 * This only tracks completeness for EXCLUSIVE sets
	 * 
	 * @return true if Exclusive and complete
	 */
	public boolean isComplete();
}
