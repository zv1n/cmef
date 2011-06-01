package pkgCMEF;

import java.util.HashMap;


//====================================================================
/**
 * CmeState
 * <P>
 * Purpose: This class defines the different states in which an experiment can
 * exist in.
 * 
 * @author Terry Meacham
 * @version 2.0, June 2011
 */
// ===================================================================

public class CmeState {
	
	/** Effectively the parent application instance */
	private CmeApp m_App;
	
	/** State for this state */
	private int m_iState;
	
	private CmeState m_sPrevState;
	
	private CmeEventResponse[] m_erEvent = new CmeEventResponse[EVENT_MAX];

	/** HashSet used to store the properties for each state */
	private HashMap<String, Object> m_sProperties = new HashMap<String, Object>();

	// -------- Defined Modes ----------
	/** Display instructions mode */
	public static final int STATE_INSTRUCTION = 1;
	/** Display feedback mode */
	public static final int STATE_FEEDBACK = 2;
	/** Display prompt mode */
	public static final int STATE_PROMPT = 3;
	/** Learning mode */
	public static final int STATE_STUDY = 4;
	/** Testing mode */
	public static final int STATE_TEST = 5;

	// ---------- Events -------
	/** Event for Clicking a Continue Button */
	public static final int EVENT_CLICK_PRIMARY = 0;
	/** Number of events */
	public static final int EVENT_MAX = 1;

	/** 
	 * Default constructor 
	 */
	public CmeState(CmeApp thisApp) {
		m_App = thisApp;
	}

	/** 
	 * Set the mode 
	 */
	public void setState(int m) {
		m_iState = m;
	}

	/** 
	 * Set the mode 
	 */
	public int getState() {
		return m_iState;
	}

	/**
	 * Set the change by action 
	 */
	public void TriggerEvent(int event) {
		if(event < 0 || event >= EVENT_MAX || m_erEvent[event] == null)
			return;
		m_erEvent[event].Respond();
	}
	
	/**
	 * Set event response 
	 */
	public void setEventResponse(int eventId, CmeEventResponse event) {
			m_erEvent[eventId] = event;
	}
	
	/** 
	 * Set a PropertyValue
	 */
	public void setProperty(String name, Object prop) {
		m_sProperties.put(name, prop);
	}
	
	/** 
	 * Set event response 
	 */
	public Object getProperty(String name) {
		return m_sProperties.get(name);
	}
	
	/** 
	 * Set a PropertyValue
	 */
	public void setPreviousState(CmeState state) {
		m_sPrevState = state;
	}
	
	/** 
	 * Set event response 
	 */
	public Object getPreviousState() {
		return m_sPrevState;
	}

	/**
	 * Validate a regular expression.
	 * 
	 * @param regex - regular expression to evaluate
	 * @param input - the input string to validate
	 * @return true on regex match; false else
	 */
	private boolean validateRegex(String regex, String input) {
		return input.matches(regex);
	}
	
	/**
	 * Validate the range of an input.
	 * 
	 * @param range - the range to be evaluate.
	 * @param input - the input string to validate.
	 * @return true on range; false else
	 */
	private boolean validateRange(String range, String input) throws Exception {
		range = range.replace(" ", "").replace("\t", "");
		String[] rangeData = range.split("-");
		int value = Integer.parseInt(input);
		
		if (rangeData.length > 2 || rangeData.length < 1) { 
			throw new Exception("To many numbers in the expected range!");
		} else if (rangeData.length == 2) {
			int lValue = Integer.parseInt(rangeData[0]);
			int hValue = Integer.parseInt(rangeData[1]);
			return (value <= lValue && value >= hValue);
		} else if (rangeData.length == 1) {
			int expValue = Integer.parseInt(rangeData[0]);
			return (value == expValue);
		}
		
		return false;
	}

	/**
	 * Validate the value of an input.
	 * 
	 * @param value - the value to be evaluate.
	 * @param input - the input string to validate.
	 * @return true on valid value; false else
	 */
	private boolean validateValue(String value, String input) {
		return (value == input);
	}
	
	/**
	 * Validate input string conforms to the State input specifications
	 * @param text - the input string to be validated
	 * @return boolean - true if valid string; false else
	 */
	public boolean validateInput(String text) throws Exception
	{
		if (text == null) {
			throw new Exception("State::ValidateInput: Null text!");
		}
		text = translateString(text);
		text = m_App.translateString(text).trim();

		String constraintType = m_sProperties.get("ContraintType").toString().toLowerCase();
		String constraint = (String)m_sProperties.get("Contraint");
		
		if (constraintType == null || constraint == null) {
			throw new Exception("State::ValidateInput: Null constraint type or value!");
		}

		constraint = translateString(constraint);
		constraint = m_App.translateString(constraint).trim();
		
		try {
			if (constraintType == "regex") {
				return validateRegex(constraint, text);
			} else if (constraintType == "range") {
				return validateRange(constraint, text);
			} else if (constraintType == "value") {
				return validateValue(constraint, text);
			}
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
		return false;
	}
	
	/**
	 * Translate the string -- any variables that exist as properties are replaced...
	 * Any variables that don't are replaced with ""
	 * @param text - text containing the elements to be replaced
	 * @return String - string with all the variables replaced
	 */
	public String translateString(String text)
	{
		return CmeApp.translateString(m_sProperties,text);
	}
}
