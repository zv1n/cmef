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
  /** Init time in milliseconds (used to set time elapsed) */
  private long m_lElapsedStart = 0;

  /** AudioHandler for handling audio components for audio enabled states. **/
  private CmeAudioHandler m_AudioHandler;
  
  /** The string list storing file locations. */
  private Vector<String> m_sSequence = new Vector<String>();
  /** Current position in sequence. */
  private int m_iSequenceIndex = 0;

  private CmeState m_sPrevState;
  
  private class stateEventHandler {
      private CmeEventResponse m_erResponse;
      private int m_iSequence;
      
      public stateEventHandler(CmeEventResponse handler, int index) {
          m_erResponse = handler;
          m_iSequence = index;
      }
      
      public CmeEventResponse getEvent() {
          return m_erResponse;
      }

      public int getSequenceLength() {
          return m_iSequence;
      }

      public boolean Respond(int seq) {
          if (m_iSequence >= 0 & seq == m_iSequence) {
        m_App.dmsg(CmeApp.DEBUG_RESPONSES | CmeApp.DEBUG_FILE_SEQUENCES,
          "stateEventHander::Respond(int:" + String.valueOf(seq) + "): Response Triggering.");
              m_erResponse.Respond();
        return true;
      }

      return false;
      }
      
  }
    
  private Vector<Vector<stateEventHandler>> m_erEvent = new Vector<Vector<stateEventHandler>>();
  /** HashSet used to store the properties for each state */
  private HashMap<String, Object> m_sProperties = new HashMap<String, Object>();
  /** The Properties that are used per sequence. */
  private Vector<HashMap<String, Object> > m_sSequenceProperties = new Vector<HashMap<String, Object> >();

  // -------- Defined Modes ----------
  /** Display instructions mode */
  public static final int STATE_INSTRUCTION = 1;
  /** Display feedback mode */
  public static final int STATE_INPUT = 2;
  /** Display prompt mode */
  public static final int STATE_PROMPT = 3;
  /** Display rating mode */
  public static final int STATE_MULTIPLE = 4;
  /** Display Audio Calibration panel */
  public static final int STATE_AUDIO_CAL = 5;
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
    private HashMap<Integer, Vector<CmeTimer> > m_hvTimer = new HashMap<Integer, Vector<CmeTimer>>();
    
    private boolean m_bStudy;
    
    private CmeTimer m_AudioPlayTimer = null;
    private CmeTimer m_AudioStopTimer = null;

  /** 
   * Default constructor 
   */
  public CmeState(CmeApp thisApp) {
    m_App = thisApp;

        for (int x=0; x<EVENT_MAX; x++)
            m_erEvent.add(new Vector<stateEventHandler>());
  }

    public Vector<String> getSequenceFiles() {
        return m_sSequence;
    }

  private Vector<CmeTimer> getSequenceTimers() {
    return getSequenceTimers(m_iSequenceIndex);
  }
  
  private Vector<CmeTimer> getSequenceTimers(Integer seq) {

    Vector<CmeTimer> timer = m_hvTimer.get(seq);

    if (timer == null) {
      timer = new Vector<CmeTimer>();
      m_hvTimer.put(seq, timer);
    }

    return m_hvTimer.get(seq);
  }
    
    /** 
     * Init global stuff...
     */
    public void init() {
    m_App.dmsg(CmeApp.DEBUG_STATES, "Global State Init.");
    }
    
    /**
     * Init at a per sequence level.
     */
    public void initSequence() {
    m_App.dmsg(CmeApp.DEBUG_STATES | CmeApp.DEBUG_FILE_SEQUENCES, "Sequence State Init.");

        Vector<CmeTimer> timers = getSequenceTimers();
    m_App.dmsg(CmeApp.DEBUG_TIMERS, "Init Sequence -- Timer Count: " + timers.size());
        for(int x=0; x<timers.size(); x++)
            timers.get(x).start();
    }
    
    /**
     * Clean up all stored variables so GC can pick them up
     */
    public void clean() {
        Vector<CmeTimer> timers = getSequenceTimers();

        for(int x=0; x<timers.size(); x++) {
      CmeTimer timer = timers.get(x);

      if (timer != null)
        timer.stop();

            timers.set(x, null);
    }

        timers.clear();
        
        m_Iterator = null;

        /*for (int x=0; x<m_erEvent.size(); x++)
            if (m_erEvent.get(x) != null) {
                for (int y=0; y<m_erEvent.get(x).size(); y++)
                    m_erEvent.get(x).set(y,null);
                m_erEvent.set(x, null);
            }
        m_erEvent = null;*/

        for (int x=0; x < m_sSequenceProperties.size(); x++) {
            HashMap<String, Object> seq = m_sSequenceProperties.get(x);
            if (seq != null)
                seq.clear();                
        }

        m_sSequenceProperties.clear();
        m_sSequenceProperties = null;

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
    public void addEventResponse(int eventId, CmeEventResponse event, int sequence) {
        if (m_erEvent.get(eventId) == null)
            m_erEvent.set(eventId, new Vector<stateEventHandler>());
        m_erEvent.get(eventId).add(new stateEventHandler(event, sequence));
    }
    
    /**
     * get event response 
     */
    public CmeEventResponse getEventResponse(int eventId, int index) {
        if (m_erEvent == null || m_erEvent.get(eventId) == null || index >= m_erEvent.get(eventId).size())
            return null;
        return m_erEvent.get(eventId).get(index).getEvent();
    }
    
    public int getEventResponseCount(int eventId) {
        if (m_erEvent == null)
            return 0;
        int count = 0;
        for (int x=0; x < m_erEvent.get(eventId).size(); x++) {
            if (m_erEvent.get(eventId).get(x).getSequenceLength() == m_iSequenceIndex)
                count++;
        }
        return count;
    }
    
    /**
     * Sets the timer object associated with this state.
     * @param timer - CmeTimer object
     * @return true if valid timer; false else
     * @throws Exception 
     */
    public boolean addEventTimer(CmeTimer timer, int seq) throws Exception {
        if (timer == null || !timer.isValid())
            return false;

        //System.out.print("Delay: ");
        //System.out.println(timer.getDelay());

        Vector<CmeTimer> timers = getSequenceTimers(seq);
        timers.add(timer);

        return true;
    }
    

    /**
     * Used to determine if the sequential are complete.
     * 
     * @return true if the number of iterations has been met; false else.
     */
    public boolean isDone() throws Exception {
        if (getState() != CmeState.STATE_MULTIPLE) {
            throw new Exception("Tested if a rating was done when NOT in a rating step!");
        }
        
        CmeIterator iterator = getIterator();
        if (iterator != null && iterator.isComplete())
            return true;

        int istep = getStep();
        int ismax = getStepMax();

        return (istep >= ismax);
    }
    
    /**
     * Resets any per seq components which need to reinit.
     */
    public void resetSeqState() {
        Vector<CmeTimer> timers = getSequenceTimers();
        for(int x=0; x<timers.size(); x++)
            timers.get(x).restart();
        
        resetSequencePosition();
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
      String sEvent = "None";

      if (CmeState.EVENT_CLICK_PRIMARY == event) {
        sEvent = "Primary Click";
      } else if (CmeState.EVENT_TIME == event) {
        sEvent = "Timer";
      }

      m_App.dmsg(CmeApp.DEBUG_RESPONSES, "Trigger Event: " + sEvent);

      if (event < 0 || event >= EVENT_MAX || m_erEvent == null || 
        m_erEvent.size() <= event || m_erEvent.get(event) == null) {
        m_App.dmsg(CmeApp.DEBUG_RESPONSES, "No event to trigger.");
        return;
      }
        //System.out.print("Event: ");
        //System.out.println(event);
        //System.out.println(m_erEvent.get(event).size());
        
          for (int x=0; x<m_erEvent.get(event).size(); x++) {
              stateEventHandler response = m_erEvent.get(event).get(x);
              if (response != null) {
          m_App.dmsg(CmeApp.DEBUG_RESPONSES, "Attempting Trigger Seq (" + String.valueOf(m_iSequenceIndex) + "): " + sEvent);
                  if (response.Respond(m_iSequenceIndex)) {
            m_App.dmsg(CmeApp.DEBUG_RESPONSES, "Triggered Seq (" + String.valueOf(m_iSequenceIndex) + "): " + sEvent);
            return;
          }
          m_App.dmsg(CmeApp.DEBUG_RESPONSES, "No Response Seq (" + String.valueOf(m_iSequenceIndex) + "): " + sEvent);
        }
          }
    }

    /** 
     * Set a PropertyValue
     */
    public void setProperty(String name, Object prop) {
        m_sProperties.put(name, prop);
    }

    /** 
     * Set a PropertyValue
     */
    public boolean setProperty(String name, Object prop, int seq) {
        if (seq == -1 || m_sSequenceProperties.size() <= seq) {
            setProperty(name, prop);
            return true;
        }

        m_sSequenceProperties.get(seq).put(name, prop);
        return true;
    }


    /** 
     * Set event response 
     */
    public Object getProperty(String name) {

      if (m_sSequenceProperties.size() > m_iSequenceIndex) {
        Object prop = m_sSequenceProperties.get(m_iSequenceIndex).get(name);
        if (prop != null) {
          m_App.dmsg(CmeApp.DEBUG_PROPERTIES, name + ":" + prop + " property from sequence: " + String.valueOf(m_iSequenceIndex));
          return prop;
        }
      }

      Object prop = m_sProperties.get(name);
      m_App.dmsg(CmeApp.DEBUG_PROPERTIES, name + ":" + prop + " property from state global");
      return prop;
    }

    /** 
     * Set event response 
     */
    public boolean getBooleanProperty(String name, boolean def) {
      Object prop = this.getProperty(name);
      
      if (prop != null && prop instanceof String) {
        String val = ((String) prop).toLowerCase();
        return (val.equals("yes") || val.equals("true"));
      }
      
      return def;
    }
    
    /** 
     * Set event response 
     */
    public String getStringProperty(String name) {
        return (String) this.getProperty(name);
    }

    public String getStringProperty(String name, String df) {
      String prop = getStringProperty(name);
      if (prop == null)
        return df;
      return prop;
    }

    /** 
     * Set event response 
     */
    public int getIntProperty(String name) {
        String str = (String) this.getProperty(name);

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
        // m_App.dmsg(11, "Regex: " + regex + "\nInput: |" + input + "|");
        return input.matches(regex);
    }
    
    private boolean incrementValue(String name, Integer inc) {
        int total = m_App.getIntProperty(name);
        if (total == -1)
            total = 0;
        
        m_App.setProperty(name, Integer.toString(total+inc));
        return true;
    }
    
    @SuppressWarnings("unused")
    private boolean incrementValue(String name, String value) {
        int points = getIntProperty("Pair1Value");
        if (points == -1)
            points = 1;
        
        return incrementValue(name, points);
    }
    
    /**
     * Validate the first three characters of any input.
     * 
     * @param range - the match to be evaluated.
     * @param input - the input string to validate.
     * @return true on match; false else
     */
    private boolean validateMatchAny(String dataset, String matchstring, String datafield)
        throws Exception
    {
        String input = matchstring.toLowerCase();
        
        /* Nasty Hack to get around Theif */
        input = input.replace("ie", "ee").replace("ei", "ee");

        int matchCount = getIntProperty("MatchCount");
        if (matchCount == -1)
            matchCount = 3;
        
        String trial = (String) getProperty("CurrentTrial");
        String name = (String) getProperty("RecallName");
    String seq = (String) getProperty("Pair1Sequence");

        if (name == null)
            name = "Recall_";
        else
            name = m_App.translateString(name);

        if (trial == null)
            trial = "";
        else
            trial = "_T" + trial;

        CmePair pairc = m_App.getPairFactory().getPairByValue(dataset, datafield, matchstring, matchCount);
        
        if (pairc != null) {
            String pair = Integer.toString(m_App.getPairFactory().getTrueIndex(pairc));
            String group = pairc.getPairGroup();

            System.out.println("Correct!");
      setProperty("Match", "Correct");

            m_App.addFeedback(name + "Correct" + trial + "_" + pair, "true");
            m_App.addFeedback(name + "Response_" + pair, matchstring);
      m_App.addFeedback(name + "Order_" + pair, seq);

            incrementValue(group + "Count" + trial, 1);
            incrementValue("TotalCount" + trial, 1);
            incrementValue("ExpTotalCount", 1);

            int points = getIntProperty("Pair1Value");
            if (points == -1)
                points = 1;

            incrementValue(group + "Points" + trial, points);
            incrementValue("TotalPoints" + trial, points);
            incrementValue("ExpTotalPoints", points);
        } else {    
            System.out.println("Incorrect!");
            setProperty("Match", "Incorrect");
        }
        
        // Ensure that the spot we're currently modifying is set to false,
        // if its not already true.
        String pair = (String) getProperty("Pair1DataOrder");
        if (pair != null) {
            String correct_name = name + "Correct" + trial + "_" + pair;
            String feedback = m_App.getFeedback(correct_name);
            if (feedback == null) {
                m_App.addFeedback(correct_name, "false");
      }

      String response_name = name + "Response_" + pair;
      feedback = m_App.getFeedback(response_name);
      if (feedback == null) {
        m_App.addFeedback(response_name, "");
      }

      String order_name = name + "Order_" + pair;
      feedback = m_App.getFeedback(order_name);
      if (feedback == null) {
        m_App.addFeedback(order_name, "");
      }
        }

        return true;
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
        
        if (matchon.trim().startsWith("<")) {
            matchon = matchon.replaceAll("<[^>]*>", "");
        }
        
        /* Nasty Hack to get around Theif */
        matchon = matchon.replace("ie", "ee").replace("ei", "ee");
        in = in.replace("ie", "ee").replace("ei", "ee");
        
        int matchCount = getIntProperty("MatchCount");
        if (matchCount == -1)
            matchCount = 3;
        
        if (matchon.length() > matchCount)
            match = matchon.substring(0, matchCount);
        else
            match = matchon;
        
        String trial = (String) getProperty("CurrentTrial");
        String pair = (String) getProperty("Pair1DataOrder");
        String group = (String) getProperty("Pair1Group");
        String name = (String) getProperty("RecallName");
        
        if (name == null)
            name = "Recall_";
        else
            name = m_App.translateString(name);
        
        if (trial == null)
            trial = "";
        else
            trial = "_T" + trial;
        
        if (group == null)
            group = "";
        
        if (pair == null)
            throw new Exception("Failed to process the current pair!");
            
        
        if (in.startsWith(match)) {
            System.out.println("Correct!");
            setProperty("Match", "Correct");
            
            m_App.addFeedback(name + "Correct" + trial + "_" + pair, "true");
            
            incrementValue(group + "Count" + trial, 1);
            incrementValue("TotalCount" + trial, 1);
            incrementValue("ExpTotalCount", 1);

            int points = getIntProperty("Pair1Value");
            if (points == -1)
                points = 1;

            incrementValue(group + "Points" + trial, points);
            incrementValue("TotalPoints" + trial, points);
            incrementValue("ExpTotalPoints", points);
        } else {    
            System.out.println("Incorrect!");
            setProperty("Match", "Incorrect");
            
            m_App.addFeedback(name + "Correct" + trial + "_" + pair, "false");
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

        // m_App.dmsg(10, Integer.toString(value));

        if (rangeData.length > 2 || rangeData.length < 1) {
            throw new Exception("To many numbers in the expected range!");
        } else if (rangeData.length == 2) {
            int lValue = Integer.parseInt(rangeData[0]);
            int hValue = Integer.parseInt(rangeData[1]);

            // m_App.dmsg(10, Integer.toString(lValue) + "<=" + Integer.toString(value) + "<=" + Integer.toString(hValue));

            return (value >= lValue && value <= hValue);
        } else if (rangeData.length == 1) {
            int expValue = Integer.parseInt(rangeData[0]);

            // m_App.dmsg(10, Integer.toString(expValue) + "==" + Integer.toString(value));

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

            if (rsp.getType() != CmeComponent.HIDDEN)
              if (!validateInput(rsp)) {
                System.out.println("Validate Input: Response Value Invalid!");
                return false;
              }

            //System.out.println(rsp.getName() + ":" + rsp.getValue());
            if (rsp.getName().matches("^Select[0-9]*$") && !rsp.getValue().equals("")) {
                selCount++;
            }
        }

        if (selReq != -1) {
          if (selCount != selReq) {
              //System.out.println("Selection Count Invalid: " + Integer.toString(selCount) + " of " + Integer.toString(selReq));
              return false;
          }
        }

        return true;
    }

    /**
     * Validate input string conforms to the State input specifications
     * @param response - the response from the user
     * @return boolean - true if valid string; false else
     */
    public boolean validateInput(CmeResponse response) throws Exception {
        return validateInput(response.getName(), response.getValue());
    }

    public String[] getConstraint(String property_name) {
      Object prop = getProperty("ItemizedConstraints");
      Vector<String> props = null;

      if (prop == null) {
        return null;
      } else if (prop instanceof String) {
        props = new Vector<String>();
        props.add((String) prop);
      } else {
        props = (Vector<String>) prop;
      }

      String[] info = null;

      for (String constraint : props) {
        info = constraint.split(":");
        String name = info[0];

        name = translateString(name);
        name = m_App.translateString(name).trim();

        if (name.equals(property_name)) {
          info[0] = name;
          return info;
        }
      }

      return null;
    }

    /**
     * Validate input string conforms to the State input specifications
     * @param name - the name of the input string to be validated
     * @param text - the input string to be validated
     * @return boolean - true if valid string or no constraint; false else
     */
    public boolean validateInput(String name, String text) throws Exception {
        if (text == null) {
            return false;
        }

        text = translateString(text);
        text = m_App.translateString(text).trim();

        String constraintType = null;
        String constraint = null;
        String param1 = null;

        if (name != null) {
          String[] cinfo = getConstraint(name);

          if (cinfo != null) {
            constraintType = cinfo[1];
            constraint = cinfo[2];
            if (cinfo.length > 3)
              param1 = cinfo[3];
          }
        }

        if (constraintType == null || constraint == null) {
          constraintType = (String) this.getProperty("ConstraintType");
          constraint = (String) this.getProperty("Constraint");
          param1 = (String) this.getProperty("ConstraintParam");
        }

        System.err.println(constraintType);
        System.err.println(constraint);
        
        if (constraintType == null || constraint == null) {
            return true;
        }


        constraintType = constraintType.toLowerCase();

        constraint = translateString(constraint);
        constraint = m_App.translateString(constraint).trim();

        // m_App.dmsg(10, "Type: |" + constraintType + "|\nConstraint: " + constraint);

        try {
            if (constraintType.equals("regex")) {
                // m_App.dmsg(10, "Regex");
                return validateRegex(constraint, text);
            } else if (constraintType.equals("range")) {
                // m_App.dmsg(10, "Range");
                return validateRange(constraint, text);
            } else if (constraintType.equals("value")) {
                // m_App.dmsg(10, "Value!");
                return validateValue(constraint, text);
            } else if (constraintType.equals("match")) {
                // m_App.dmsg(10, "Match!");
                validateMatch(constraint, text);
                return true;
            } else if (constraintType.equals("match_any")) {
                // m_App.dmsg(10, "Match Any!");
                validateMatchAny(constraint, text, param1);
                return true;
            } else {
                // m_App.dmsg(10, "None!");
            }
        } catch (Exception ex) {
            System.out.println("Caught (" + ex.getClass().getName() + "):" + ex.getMessage());
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

    /**
     * Translate the string in the standard order -- State then App
     */
    public String translate(String text) {
      text = translateString(text);
      return m_App.translateString(text).trim();
    }

    /** State Display Sequence Interface
     * Enables the CmeState class to handle multiple instruction display files in a single "State".
     * The primary reason for this is so that one can collect information on a Per Item basis form "Multiple" views.
     * A single file element is not enough to accomplish this.
     */
    
    /**
     * Get the number of files in the seqence specified by the Experiment configuration file.
     * 
     * @return Integer value length of the sequence, 0-N
     */
    public int getSequenceLength() {
        return m_sSequence.size();
    }
    
    /**
     * Get the current sequence position.
     * @return the current sequence index.
     */
    public int getSequencePosition() {
        return m_iSequenceIndex;
    }
    
    public boolean isSequenceValid() {
        int seqLength = getSequenceLength();
        boolean valid = (m_iSequenceIndex < seqLength && m_iSequenceIndex >= 0);
      m_App.dmsg(CmeApp.DEBUG_FILE_SEQUENCES, "Sequence Valid? " + String.valueOf(valid));
      return valid;
    }
    
    /**
     * Increment the sequence counter.
     * @return true if there is a sequence to progress to. false if not.
     */
    public boolean nextSequencePosition() {
      m_App.dmsg(CmeApp.DEBUG_FILE_SEQUENCES, "Next Sequence.");
      m_App.dmsg(CmeApp.DEBUG_FILE_SEQUENCES, "Cur Seq: " + String.valueOf(m_iSequenceIndex));
        m_iSequenceIndex++;
      m_App.dmsg(CmeApp.DEBUG_FILE_SEQUENCES, "Next Seq: " + String.valueOf(m_iSequenceIndex));

        boolean valid = isSequenceValid();

        if (!valid)
            return false;

        initSequence();
        return true;
    }
    
    public String getSequenceFile() {
        if (!isSequenceValid())
            return null;
        return m_sSequence.get(m_iSequenceIndex); 
    }

    /**
     * Reset sequnce counter.
     */
    public void resetSequencePosition() {
        m_iSequenceIndex = 0;
    }
    
    /**
     * Add file to sequence.
     * @param file - Name of the file to add to the sequence.
     */
    public void addInstructionFile(String file) {
        m_sSequence.add(file);
        m_sSequenceProperties.add(new HashMap<String, Object>());
    }
    
    public void printInstructionFiles() {
        System.out.println("Files for state:");
        for (int x=0; x<m_sSequence.size(); x++) {
            System.out.println(m_sSequence.get(x));
        }
    }

    /* Configures the AudioHandler for audio playback. */
    public boolean configureAudioPlayback() throws Exception {
      boolean audio_supported = getBooleanProperty("AudioSupport", false);

      if (!audio_supported)
        return false;

      m_AudioHandler = new CmeAudioHandler(this, m_App);
      return true;
    }

    public void setAudioVolumeProperty(String prop) throws Exception {
      if (m_AudioHandler != null)
        m_AudioHandler.setActualAudioProperty(prop);
      else
        setProperty(prop, "unset");
    }

    public void startAudioPlayback() throws Exception {
      if (m_AudioHandler == null)
        return;

      String play_when = getStringProperty("PlayAudio", "immediate");

      if (play_when.equals("immediate")) {
        m_AudioHandler.playAudio();
        return;
      } else {
        String[] opts = play_when.split(":");
        String action = opts[0].toLowerCase();
        if (action.equals("time")) {
          startAudioPlaybackAt(opts[1]);
          return;
        }
      }
    }

    public void startAudioPlaybackAt(String time) {
      m_AudioPlayTimer = new CmeTimer(m_App);

      final CmeAudioHandler handler = m_AudioHandler;

      m_AudioPlayTimer.setDelayByString(time);
      m_AudioPlayTimer.setResponse(new CmeEventResponse() {
        @Override
        public void Respond() {
          try {
            handler.playAudio();
          } catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      });
      m_AudioPlayTimer.start();
      
    }

    /* Reset the Time Elapsed */
    public void resetTimeElapsed() {
      m_lElapsedStart = System.currentTimeMillis();
    }

    /* Fetch the current time elapsed. */
    public long getTimeElapsed() {
      return System.currentTimeMillis() - m_lElapsedStart;
    }
}
