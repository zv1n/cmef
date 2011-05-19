package pkgCMEF;

import javax.swing.JOptionPane;

//====================================================================
/**
 * CmeState
 * <P>
 * Purpose: This class defines the different states in which an experiment can
 * exist in.
 * 
 * @author Dr. Rick Coleman, Terry Meacham
 * @version 1.1 Date: February, 2009
 */
// ===================================================================

public class CmeState {
	/** State for this state */
	private int m_iState;

	// ----- Vars for show instructions mode -----
	private String m_sInsFileName;

	// ----- Vars for show condtion validation -----
	private String m_sValidConditions;

	private boolean m_bRecordState;
	
	private int[] m_iEvent;
	
	/** Specifies 

	// ----- Vars for all modes -----
	private int m_iChangeStateBy;

	// -------- Defined Modes ----------
	/** Display instructions mode */
	public static final int STATE_INSTRUCTION = 1;
	/** Learning mode */
	public static final int STATE_LEARNING = 2;
	/** Testing mode */
	public static final int STATE_TEST = 3;

	// ---------- Events -------
	/** Event for Clicking a Continue Button */
	public static final int EVENT_CLICK_CONTINUE = 0x01;
	
	// ---------- Event Groups ---------
	/** Groups for Events */ 
	public static final int GROUP_NEXT_STATE = 0;
	public static final int GROUP_MAX = 1;		

	// ---------------------------------------------------------
	/** Default constructor */
	// ---------------------------------------------------------
	public CmeState() {
		m_iEvent = new int[GROUP_MAX];
	}

	// ---------------------------------------------------------
	/** Set the mode */
	// ---------------------------------------------------------
	public void setState(int m) {
		m_iState = m;
	}

	// ---------------------------------------------------------
	/** Set the mode */
	// ---------------------------------------------------------
	public int getState() {
		return m_iState;
	}

	// ---------------------------------------------------------
	/** Set the change by action */
	// ---------------------------------------------------------
	public void TriggerEvent(int event) {
		if (!m_iEvent)
			return;
		
		if(m_iEvent[CmeState.GROUP_NEXT_STATE] & event) {
			
		}
	}
	
	// ---------------------------------------------------------
	/** Set event response */
	// ---------------------------------------------------------
	public void setEventResponse(int group, int event) {
		if (group >= 0 && group < CmeState.GROUP_MAX)
			m_iEvent[group] |= event;
	}

	// ---------------------------------------------------------
	/** Set the instruction file name */
	// ---------------------------------------------------------
	public void setInstructionFile(String s) {
		m_sInsFileName = s;
	}

	// ---------------------------------------------------------
	/** Set the mode */
	// ---------------------------------------------------------
	public String getInstructionFile() {
		return m_sInsFileName;
	}

	// ---------------------------------------------------------
	/** Is this state a Record state? */
	// ---------------------------------------------------------
	public boolean isRecordState() {
		return m_bRecordState;
	}

	// ---------------------------------------------------------
	/** Set a Record State */
	// ---------------------------------------------------------
	public void setRecordState() {
		m_bRecordState = true;
	}

	// ---------------------------------------------------------
	/** Set the mode */
	// ---------------------------------------------------------
	public void setValidConditions(String conditions) {
		m_sValidConditions = conditions;
	}

	// ---------------------------------------------------------
	/** validate the condition we are currently in */
	// ---------------------------------------------------------
	public boolean validCondition(String condition) {
		return m_sValidConditions.contains(condition);
	}

}
