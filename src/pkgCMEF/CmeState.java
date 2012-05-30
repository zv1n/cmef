package pkgCMEF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
    /** Current rating step (if this is a STATE_RATING state). */
    private int m_iStep;
    /** Maximum rating step (if this is a STATE_RATING state). */
    private int m_iStepMax;
    /** Number of items to show simultaneously. */
    private int m_iCount;
	
    private CmeState m_sPrevState;
	
    private Vector<Vector<CmeEventResponse>> m_erEvent = new Vector<Vector<CmeEventResponse>>();
    /** HashSet used to store the properties for each state */
    private HashMap<String, Object> m_sProperties = new HashMap<String, Object>();
	
    // -------- Defined Modes ----------
    /** Display instructions mode */
    public static final int STATE_INSTRUCTION = 1;
    /** Display feedback mode */
    public static final int STATE_INPUT = 2;
    /** Display prompt mode */
    public static final int STATE_PROMPT = 3;
    /** Display rating mode */
    public static final int STATE_STUDY = 4;
    // ---------- Events -------
    /** Event for Clicking a Continue Button */
    public static final int EVENT_CLICK_PRIMARY = 0;
    /** Event for Time Elapsed */
    public static final int EVENT_TIME = 1;
    /** Number of events */
    public static final int EVENT_MAX = 2;
	
    /** Iterators */
    private CmeIterator m_Iterator;
	/** Timer */
	private Vector<CmeTimer> m_Timer = new Vector<CmeTimer>();
	
	private boolean m_bStudy;
	

    /** 
     * Default constructor 
     */
    public CmeState(CmeApp thisApp) {
        m_App = thisApp;
		for (int x=0; x<EVENT_MAX; x++)
			m_erEvent.add(new Vector<CmeEventResponse>());
    }
	
	/** 
	 * Kick-off the timer.
	 */
	public void init() {
		for(int x=0; x<m_Timer.size(); x++)
			m_Timer.get(x).start();
	}
	
	/**
	 * Clean up all stored variables so GC can pick them up
	 */
	public void clean() {
		System.out.println("Clean");
		for(int x=0; x<m_Timer.size(); x++)
			m_Timer.set(x, null);
		m_Timer.clear();
		
		m_Iterator = null;
		/*for (int x=0; x<m_erEvent.size(); x++)
			if (m_erEvent.get(x) != null) {
				for (int y=0; y<m_erEvent.get(x).size(); y++)
					m_erEvent.get(x).set(y,null);
				m_erEvent.set(x, null);
			}
		m_erEvent = null;*/
		
		m_sProperties.clear();
		m_sProperties = null;
	}

    /**
     * Sets the interface for the given iterator.
     * @return -1 is invalid; 0 else
     */
    public int setIterator(CmeIterator iterator) {
        if (iterator == null) {
            return -1;
        }
        m_Iterator = iterator;
        return 0;
    }
	
    /**
     * Set event response 
     */
    public void addEventResponse(int eventId, CmeEventResponse event) {
		if (m_erEvent.get(eventId) == null)
			m_erEvent.set(eventId, new Vector<CmeEventResponse>());
        m_erEvent.get(eventId).add(event);
    }
	
    /**
     * get event response 
     */
    public CmeEventResponse getEventResponse(int eventId, int index) {
		if (m_erEvent == null || m_erEvent.get(eventId) == null || index >= m_erEvent.get(eventId).size())
			return null;
        return m_erEvent.get(eventId).get(index);
    }
	
	public int getEventResponseCount(int eventId) {
		if (m_erEvent == null)
			return 0;
		return m_erEvent.get(eventId).size();
	}
	
	/**
	 * Sets the timer object associated with this state.
	 * @param timer - CmeTimer object
	 * @return true if valid timer; false else
	 */
	public boolean addEventTimer(CmeTimer timer) {
		if (timer == null && timer.isValid())
			return false;
		System.out.print("Delay: ");
		System.out.println(timer.getDelay());
		m_Timer.add(timer);
		return true;
	}
	
	/**
	 * Resets any per seq components which need to reinit.
	 */
	public void resetSeqState() {
		for(int x=0; x<m_Timer.size(); x++)
			m_Timer.get(x).restart();
	}

    /**
     * Returns the interface for the given iterator.
     * @return CmeIterator interface
     */
    public CmeIterator getIterator() {
        return m_Iterator;
    }

    /** 
     * Set the current state. 
     */
    public void setState(int m) {
        m_iState = m;
    }

    /** 
     * Get the current state. 
     */
    public int getState() {
        return m_iState;
    }
	
	/** Set whether this instruction can study */
	public void setStudyInstruction(boolean b) {
		m_bStudy = b;
	}
	
	/** Can this instruction state study? */
	public boolean canStudy() {
		return m_bStudy;
	}

    /**
     * Set the change by action 
     */
    public void TriggerEvent(int event) {
        if (event < 0 || event >= EVENT_MAX || m_erEvent == null || 
			m_erEvent.size() <= event || m_erEvent.get(event) == null) {
            return;
        }
		System.out.print("Event: ");
		System.out.println(event);
		System.out.println(m_erEvent.get(event).size());
		
		for (int x=0; x<m_erEvent.get(event).size(); x++) {
			CmeEventResponse response = m_erEvent.get(event).get(x);
			if (response != null)
				response.Respond();
		}
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
     * Set event response 
     */
    public String getStringProperty(String name) {
        return (String) m_sProperties.get(name);
    }

    /** 
     * Set event response 
     */
    public int getIntProperty(String name) {
        String str = (String) m_sProperties.get(name);
        int ret = 0;
        if (str == null) {
            return -1;
        }
        try {
            ret = Integer.parseInt(str);
        } catch (Exception x) {
            return -1;
        }
        return ret;
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

    /** Set the current rating step */
    public void setStep(int step) {
        m_iStep = step;
    }

    /** Set the current max rating step */
    public void setStepMax(int step) {
        m_iStepMax = step;
    }

    /** Get the current rating step */
    public int getStep() {
        return m_iStep;
    }

    /** Get the current max rating step */
    public int getStepMax() {
        return m_iStepMax;
    }
	
	public int getPerStepCount() {
		return m_iCount;
	}
	
	public void setPerStepCount(int count) {
		m_iCount = count;
	}

    /**
     * Validate a regular expression.
     * 
     * @param regex - regular expression to evaluate
     * @param input - the input string to validate
     * @return true on regex match; false else
     */
    private boolean validateRegex(String regex, String input) {
        m_App.dmsg(11, "Regex: " + regex + "\nInput: |" + input + "|");
        return input.matches(regex);
    }
	
    /**
     * Validate the first three characters of an input.
     * 
     * @param range - the match to be evaluated.
     * @param input - the input string to validate.
     * @return true on match; false else
     */
	private boolean validateMatch(String matchstring, String input) throws Exception {
		String in = input.toLowerCase();
		String matchon = matchstring.toLowerCase();
		String match = null;
		
		
		/* Nasty Hack to get around Theif */
		matchon = matchon.replace("ie", "ee");
		matchon = matchon.replace("ei", "ee");
		in = in.replace("ie", "ee");
		in = in.replace("ei", "ee");
		
		if (matchon.length() > 3)
			match = matchon.substring(0,3);
		else
			match = matchon;
		
		String trial = (String) getProperty("CurrentTrial");
		String pair = (String) getProperty("Pair1");
		String group = (String) getProperty("Pair1Group");
		String name = (String) getProperty("RecallName");
		if (name == null)
			name = "Recall";
			
		if (in.startsWith(match)) {
			System.out.println("Correct!");
			setProperty("Match", "correct");
			
			m_App.addFeedback(name + "Correct_T" + trial + "_" + pair, "true");
				
			int total = m_App.getIntProperty("TotalCorrect_T" + trial);
			if (total == -1)
				total = 0;
			
			int points = getIntProperty("Pair1Value");
			if (points == -1)
				points = 1;
			
			m_App.setProperty("TotalCorrect_T" + trial, Integer.toString(total+points));
			
			total = m_App.getIntProperty(group + "Total_T" + trial);
			if (total == -1)
				total = 0;
			m_App.setProperty(group + "Total_T" + trial, Integer.toString(total+points));
		} else {	
			System.out.println("Incorrect!");
			setProperty("Match", "incorrect");
			
			m_App.addFeedback(name + "Correct_T" + trial + "_" + pair, "false");
			
			if (m_App.getProperty("TotalCorrect_T" + trial) == null)
				m_App.setProperty("TotalCorrect_T" + trial, "0");
			if (getProperty("Pair1Value") == null)
				setProperty("Pair1Value", "1");
			
			if (group != null && m_App.getProperty(group + "Total_T" + trial) == null)
				m_App.setProperty(group + "Total_T" + trial, "0");
		}
		
		return true;
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

        m_App.dmsg(10, Integer.toString(value));

        if (rangeData.length > 2 || rangeData.length < 1) {
            throw new Exception("To many numbers in the expected range!");
        } else if (rangeData.length == 2) {
            int lValue = Integer.parseInt(rangeData[0]);
            int hValue = Integer.parseInt(rangeData[1]);

            m_App.dmsg(10, Integer.toString(lValue) + "<=" + Integer.toString(value) + "<=" + Integer.toString(hValue));

            return (value >= lValue && value <= hValue);
        } else if (rangeData.length == 1) {
            int expValue = Integer.parseInt(rangeData[0]);

            m_App.dmsg(10, Integer.toString(expValue) + "==" + Integer.toString(value));

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
     * Validate input set conforms to expectations
     * @param iter - CmeResponse iterator to validate inputs
     * @return boolean - true if valid set; false else
     */
    public boolean validateInput(Iterator<CmeResponse> responses) throws Exception {
        int selReq = getIntProperty("Select");
        int selCount = 0;

        while (responses.hasNext()) {
            CmeResponse rsp = responses.next();

            if (!validateInput(rsp)) {
                System.out.println("Validate Input: Response Value Invalid!");
                return false;
            }
            System.out.println(rsp.getName() + ":" + rsp.getValue());
            if (rsp.getName().matches("^Select[0-9]*$") && !rsp.getValue().equals("")) {
                selCount++;
            }
        }

        if (selCount != selReq && selReq != -1) {
            System.out.println("Selection Count Invalid: " + Integer.toString(selCount) + " of " + Integer.toString(selReq));
            return false;
        }
        return true;
    }

    /**
     * Validate input string conforms to the State input specifications
     * @param response - the response from the user
     * @return boolean - true if valid string; false else
     */
    public boolean validateInput(CmeResponse response) throws Exception {
        return validateInput(response.getValue());
    }

    /**
     * Validate input string conforms to the State input specifications
     * @param text - the input string to be validated
     * @return boolean - true if valid string or no constraint; false else
     */
    public boolean validateInput(String text) throws Exception {
        if (text == null) {
            return false;
        }

        text = translateString(text);
        text = m_App.translateString(text).trim();

        String constraintType = (String) m_sProperties.get("ConstraintType");
        String constraint = (String) m_sProperties.get("Constraint");

			if (constraintType == null || constraint == null) {
            return true;
        }

        constraintType = constraintType.toLowerCase();

        constraint = translateString(constraint);
        constraint = m_App.translateString(constraint).trim();

        m_App.dmsg(10, "Type: |" + constraintType + "|\nConstraint: " + constraint);

        try {
            if (constraintType.equals("regex")) {
                m_App.dmsg(10, "Regex");
                return validateRegex(constraint, text);
            } else if (constraintType.equals("range")) {
                m_App.dmsg(10, "Range");
                return validateRange(constraint, text);
            } else if (constraintType.equals("value")) {
                m_App.dmsg(10, "Value!");
                return validateValue(constraint, text);
            } else if (constraintType.equals("match")) {
                m_App.dmsg(10, "Match!");
                validateMatch(constraint, text);
				return true;
			} else {
                m_App.dmsg(10, "None!");
            }
        } catch (Exception ex) {
            System.out.println("Catch: " + ex.getMessage());
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
    public String translateString(String text) {
        return CmeApp.translateString(m_sProperties, text, true);
    }
}
