package pkgCMEF;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

//====================================================================
/**
 * Custom Memory Experiment Framework
 * <P>
 * Purpose: This application was designed and written for use as the primary
 * experimental software for an experiment in learning conducted by Jodi Price.
 * 
 * @author Terry Meacham
 * @version 2.0 Date: May 2011
 */
// ===================================================================
@SuppressWarnings("serial")
public class CmeApp extends JFrame implements AncestorListener {

	/** Main panel */
	private JPanel m_MainPanel;
	private Dimension m_dOldDims;
	/** Instructions Handler */
	private CmeView m_ViewHandler;
	/** Experiment Handler */
	private CmeExperiment m_ExperimentHandler;
	/** HashMap of all experiment properties */
	private HashMap<String, Object> m_eProperties;
	/** Variables used to hold the feedback values */
	private HashMap<String, Object> m_fbHashmap = new HashMap<String, Object>();
	/** Variables used to hold the timer feedback values */
	private Vector<String> m_fbVectString = new Vector<String>();
	/** Debug Level */
	private int m_iDebugLevel;

	private int m_iOptions;
	/** Store time in milliseconds */
	private long m_lStartTimeMillis;
	/** Name of the experiment definition file */
	private String m_sExpFileName;

	/** The minimum version this API is compatible with. */
	final static private double m_MinVer = 2.1;
	final static private double m_Version = 2.5;

	public static final int CME_ENABLE_REFRESH = 0x1;
	public static final int CME_TEXT_ONLY = 0x2;

  public static final int DEBUG_TIMERS = 1;
  public static final int DEBUG_RESPONSES = 2;
  public static final int DEBUG_STATES = 4;
  public static final int DEBUG_FILE_SEQUENCES = 8;
  public static final int DEBUG_HTML_VIEW = 16;
  public static final int DEBUG_STUDY_PHASES = 32;
  public static final int DEBUG_PROPERTIES = 64;
	// ------------------------------------------------------------------
	// State variables for storing state information loaded from file.
	// ------------------------------------------------------------------
	/** Vector of states defining the experiment */
	private Vector<CmeState> m_vStates;
	/** Index of the current state */
	private Iterator<CmeState> m_cIterator;
	/** Reference to the current state */
	private CmeState m_CurState;
	/** Reference to the image factory */
	private CmePairFactory m_PairFactory;
	/** Iterator for exp objects */
	private CmeIteratorFactory m_IteratorFactory;
	/** MS Timer for Feedback */
	private Timer m_tTimer;
	/** MS Counter for Feedback Timer */
	private int m_iTickCounter;

	private CmeEventResponse m_EndResponse;
	private CmeEventResponse m_NextResponse;
	private CmeEventResponse m_BlankResponse;

	private HashMap<String, CmeSelectiveIter> m_vrPools;
	// ------------------------------------------------------------------
	// Data storage components
	// ------------------------------------------------------------------
	/** File to write report to */
	private File m_fileReport;
	/** The file writer encapsulating the report file */
	private FileWriter m_fileWriter;
	/** The buffered writer encapsulating the file writer */
	private BufferedWriter m_bufWriter;
	/** Title font used for large labels */
	public static final Font SysTitleFontB = new Font("SansSerif", Font.BOLD,
			28);
	/** Font used in the lists */
	public static final Font SysButtonFontB = new Font("SansSerif", Font.BOLD,
			17);
	/** Label font used for most labels */
	public static final Font SysLabelFontB = new Font("SansSerif", Font.BOLD,
			21);
	/** Label font used for most labels */
	public static final Font SysInstructionFont = new Font("Courier",
			Font.PLAIN, 18);
	/** Label font used for most labels */
	public static final Font SysLabelFontP = new Font("SansSerif", Font.PLAIN,
			20);
	/** Small font used for instructions and labels on text widgets */
	public static final Font SysSmallFont = new Font("SansSerif", Font.PLAIN,
			18);

	static public class cmePropertyDescription {
		public cmePropertyDescription(String desc, String def) {
			m_sDescription = desc;
			m_sDefaultValue = def;
		}

		public String m_sDescription;
		public String m_sDefaultValue;
	};

	private static HashMap<String, cmePropertyDescription> m_vrPropertyListing;

	/**
	 * Add a property to the property listing.
	 * 
	 * @param name
	 *            Name of the property.
	 * @param description
	 *            Property description to display.
	 * @param defaultval
	 *            Default value assigned to the property when it is not
	 *            externally defined.
	 * 
	 * @return True if the property of Name is not in the list, false otherwise.
	 */
	public static boolean addPropertyListing(String name, String description,
			String defaultval) {
		if (m_vrPropertyListing.containsKey(name))
			return false;

		m_vrPropertyListing.put(name, new CmeApp.cmePropertyDescription(
				description, defaultval));
		return m_vrPropertyListing.containsKey(name);
	}

	/**
	 * Return the cmePropertyDescription associated with the name.
	 * 
	 * @param name
	 *            Name of the property to retrieve
	 * @return
	 */
	public static cmePropertyDescription getPropertyDescription(String name) {
		return m_vrPropertyListing.get(name);
	}

	/**
	 * Return constant map for the PropertyListing.
	 * 
	 * @return Iterator associated with the listing.
	 */
	public Map<String, cmePropertyDescription> getPropertyListing() {
		return java.util.Collections.unmodifiableMap(m_vrPropertyListing);
	}

	/**
	 * Pop-up a message box to be used for debug messages.
	 * 
	 * @param level
	 *            - minimum debug level to show the MsgBox
	 * @param msg
	 *            - the message to be displayed
	 */
	public void dmsg(int level, String msg) {
    if ((level & m_iDebugLevel) != 0) {
			System.err.println("(" + String.valueOf(level) + "): " + msg);
		}
	}

	public CmePairFactory getPairFactory() {
		return m_PairFactory;
	}

	public String getImagePrefix() {
		return "";
	}

	@Override
	public void ancestorRemoved(AncestorEvent arg0) {
	}

	@Override
	public void ancestorAdded(AncestorEvent arg0) {
	}

	@Override
	public void ancestorMoved(AncestorEvent e) {
		JPanel pane = (JPanel) e.getComponent();
		if (pane.getSize() != m_dOldDims) {
			adjustAllLayouts();
		}
	}

	/**
	 * The CmeApp class constructor.
	 * 
	 * @param debugLevel
	 *            - initial application debug level
	 */
	public CmeApp(int debugLevel, int opts, String experimentFile) {
		m_iDebugLevel = debugLevel;

		// Set up the property HashMap
		m_eProperties = new HashMap<String, Object>();
		// Set up the property HashMap
		m_vrPools = new HashMap<String, CmeSelectiveIter>();

		// Set up the experiment states
		m_vStates = new Vector<CmeState>();

		// INit the iterator Factory for the app
		m_IteratorFactory = new CmeIteratorFactory(this);

		m_sExpFileName = experimentFile;
		m_iOptions = opts;

		try {
			initTickCounter();
			initMainWindow();
			initExperiment();
			initStateHandlers();
			initParticipantData();

			this.setTitle(m_eProperties.get("Title").toString());
			// dmsg(5, "Title Set");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error!",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		// Show the window
		this.setVisible(true);
		this.adjustAllLayouts();

	}

	public CmeSelectiveIter getPool(String pool) {
		return m_vrPools.get(pool);
	}

	public void run() {
		// Set the initial state
		this.setNextState();
	}

	public CmeState getCurrentState() {
		return m_CurState;
	}

	/**
	 * Set a PropertyValue
	 */
	public void setProperty(String name, Object prop) {
		m_eProperties.put(name, prop);
	}

	/**
	 * Set event response
	 */
	public Object getProperty(String name) {
		return m_eProperties.get(name);
	}

	/**
	 * Set event response
	 */
	public int getIntProperty(String name) {
		String str = (String) m_eProperties.get(name);
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

	private void initTickCounter() {
		final CmeApp cInst = this;
		m_iTickCounter = 0;
		ActionListener aListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				m_iTickCounter++;
			}
		};
		m_tTimer = new Timer(0x1, aListener);
		m_tTimer.setRepeats(true);
		m_tTimer.start();
	}

	private void initDataSet(String datafile) {
		// Create the image factory
		m_PairFactory = new CmePairFactory(this, datafile);
	}

	private void initMainWindow() {
		this.setSize(1024, 768);
		this.setLocation(50, 50);
		this.setTitle("CME Main Panel");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create the main panel
		m_MainPanel = new JPanel();
		m_MainPanel.setSize(1024, 768);
		m_MainPanel.setLocation(0, 0);
		m_MainPanel.setLayout(new BoxLayout(m_MainPanel, BoxLayout.PAGE_AXIS));
		m_MainPanel.setBackground(Color.LIGHT_GRAY);
		m_MainPanel.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED));
		m_MainPanel.addAncestorListener(this);
		this.getContentPane().add(m_MainPanel);
	}

	/**
	 * Initialization function for preparing all State handlers
	 * (CmeExperiment,CmeInstructions,CmeStudy)
	 */
	private void initStateHandlers() throws Exception {

		m_ViewHandler = new CmeView(this, m_iOptions);
		m_ViewHandler.setPairFactory(m_PairFactory);
		m_ViewHandler.addAncestorListener(this);
		m_ViewHandler.setVisible(false);
		m_MainPanel.add(m_ViewHandler);

		m_ExperimentHandler = new CmeExperiment(this);
		m_ExperimentHandler.setPairFactory(m_PairFactory);
		m_ExperimentHandler.addAncestorListener(this);
		m_ExperimentHandler.setVisible(false);
		m_MainPanel.add(m_ExperimentHandler);

		// dmsg(5, "State Handler Init Successful!");
	}

	/**
	 * Used to initialize the SubjectID and Experiment Condition.
	 */
	private void initParticipantData() {
		String sCondition;
		String sSubjectId;

		if ((m_iDebugLevel & 0x100) == 0x0) {

			do {
				sSubjectId = JOptionPane
						.showInputDialog("Please enter your subject ID:");
				if (sSubjectId == null) {
					System.exit(0);
				}
			} while (sSubjectId == "");

			m_eProperties.put("SubjectID", sSubjectId);
			// dmsg(5, "Subject Complete!");
			
			if (multipleConditions()) {
				do {
					sCondition = JOptionPane
							.showInputDialog("Please enter the experimental Condition\n"
									+ "(This should be given by the experimenter).");
					if (sCondition == null) {
						System.exit(0);
					}
				} while (!setCondition(sCondition.toUpperCase()));
				// dmsg(5, "Conditions Complete!");
			}
		} else {
			m_eProperties.put("SubjectID", "TESTID");
			m_eProperties.put("ExpCondition", "B");
		}
	}

	private void configureTrialGlobals(String id) {
		id = id.trim();
		Vector<String> typeList = m_PairFactory.getTypeList();

		for (int x = 0; x < typeList.size(); x++) {
			m_eProperties.put(typeList.get(x) + "Count_T" + id, "0");
			m_eProperties.put(typeList.get(x) + "Points_T" + id, "0");
		}

		m_eProperties.put("TotalCount_T" + id, "0");
		m_eProperties.put("TotalPoints_T" + id, "0");
	}

	private void configureStateGlobals(CmeState state, String trialId,
			String dataset, int seq) {
		if (trialId != null)
			state.setProperty("CurrentTrial", trialId, seq);
		if (dataset != null)
			state.setProperty("CurrentDataset", dataset, seq);

		state.setProperty("MatchCount", "3", seq);
	}

	private void configureGlobals() {
		m_eProperties.put("ExpTotalCount", "0");
		m_eProperties.put("ExpTotalPoints", "0");
	}
	
	private String variableQuery(String name) {
		String ret = "^";
		ret += name + "\\s*=.*$";
		return ret;
	}

	private CmeIterator getIterator(String iterator, String[] type, CmeState state) {
		String validIterators = "RANDOM:SELECTIVE:DIFFICULTY";
		String validTypes = "NONEXCLUSIVE:REVERSE:DESCENDING:ASCENDING";
		int itype = 0;
		int iiter = 0;

		if (!validIterators.contains(iterator)) {
			return null;
		}

		if (iterator.equals("RANDOM")) {
			iiter = CmeIterator.RANDOM;
		}

		if (iterator.equals("SELECTIVE")) {
			iiter = CmeIterator.SELECTIVE;
		}

		if (iterator.equals("DIFFICULTY")) {
			iiter = CmeIterator.DIFFICULTY;
		}

		for (int x = 0; x < type.length; x++) {
			if (!validTypes.contains(type[x])) {
				return null;
			}
			//System.out.println("Iterator Type: " + type[x]);

			if (type[x].equals("EXCLUSIVE")) {
				itype |= CmeIterator.EXCLUSIVE;
			} else if (type[x].equals("NONEXCLUSIVE")) {
				itype |= CmeIterator.NONEXCLUSIVE;
			} else if (type[x].equals("REVERSE")
					|| type[x].equals("DESCENDING")) {
				itype |= CmeIterator.REVERSE;
			} else if (type[x].equals("ASCENDING")) {
				itype &= ~CmeIterator.REVERSE;
			}
		}

		//// dmsg(0xFF,
		//		"Set Iterator: "
		//			+ Integer.toString(this.m_PairFactory.getCount()));

		return m_IteratorFactory.createIterator(iiter, itype);
	}

	private boolean setStudyLimit(String action, String lhs, String rhs,
			CmeState state, int seq) throws Exception {
		if (state == null)
			return false;

		if (lhs.toUpperCase().equals("TIME")) {
			int limit = CmeTimer.getDelayStringValue(rhs);

			//System.out.println("Limit: " + limit);
			state.setProperty("Limit", String.valueOf(limit), seq);
			return true;
		}

		return false;
	}

	private boolean setEvent(String action, String lhs, String rhs,
			CmeState state, int seq) throws Exception {
		final CmeApp thisApp = this;
		int type = CmeState.EVENT_MAX;
		CmeEventResponse response = null;

		if (action == null) {
			return false;
		}

		action = action.toUpperCase();
		lhs = lhs.toUpperCase();

		final String _lhs = lhs;
		final String _rhs = rhs;
		final String _action = action;

		if (m_EndResponse == null)
			m_EndResponse = new CmeEventResponse() {

				public void Respond() {
          dmsg(CmeApp.DEBUG_RESPONSES, "End Response Triggered");
					thisApp.setNextState();
				}
			};

		if (m_NextResponse == null)
			m_NextResponse = new CmeEventResponse() {
				public void Respond() {
          dmsg(CmeApp.DEBUG_RESPONSES | CmeApp.DEBUG_FILE_SEQUENCES, "Next Response Triggered");
					try {
						if (!m_ViewHandler.setNextInSequence()) {
							thisApp.setNextState();
						}
					} catch (Exception ex) {
						// thisApp.dmsg(0, ex.getMessage());
					}
				}
			};

		if (m_BlankResponse == null)
			m_BlankResponse = new CmeEventResponse() {
				public void Respond() {
          dmsg(CmeApp.DEBUG_RESPONSES, "Blank Response Triggered");
					try {
						m_ViewHandler.blankInstructions();
					} catch (Exception ex) {
						// thisApp.dmsg(0, ex.getMessage());
					}
				}
			};

		if (action.equals("END")) {
			response = m_EndResponse;
		} else if (action.equals("NEXT")) {
			response = m_NextResponse;
		} else if (action.equals("BLANK")) {
			response = m_BlankResponse;
		} else
			throw new Exception(
					"Unknown Action Type (Wanted END, NEXT, or BLANK, got '"
							+ action + "').");

		if (lhs.equals("CLICK")) {
			type = CmeState.EVENT_CLICK_PRIMARY;

			if (rhs != null && rhs.length() > 0) {
				state.setProperty("PrimaryButtonText", rhs, seq);
			}
		} else if (lhs.equals("TIME")) {
			type = CmeState.EVENT_TIME;

      dmsg(CmeApp.DEBUG_TIMERS, "Timer action: " + action);
			CmeTimer timer = new CmeTimer(this);

			if (!timer.setDelayByString(rhs)) {
				return false;
			}
			if (!timer.setResponse(response)) {
				return false;
			}
			if (!state.addEventTimer(timer, seq)) {
				return false;
			}
		} else
			throw new Exception(
					"Unknown Event Type (Wanted Click or Time, got '" + lhs
							+ "').");

		state.addEventResponse(type, response, seq);
		return true;
	}

	private void compoundProperty(CmeState state, String name, String value) {
		if (name == null || value == null)
			return;

		Vector<String> newProp = null;
		Object property = null;
		if (state == null)
			property = m_eProperties.get(name);
		else
			property = state.getProperty(name);

		if (property == null) {
			newProp = new Vector<String>();
		} else if (property instanceof String) {
			newProp = new Vector<String>();
			newProp.add((String) property);
		} else {
			newProp = (Vector<String>) property;
		}

		newProp.add(value);

		if (state == null)
			m_eProperties.put(name, newProp);
		else
			state.setProperty(name, newProp);
	}

	/** Used to safely retrieve a string. */
	public String getQuotedText(String line) throws Exception {
    try {
  		int idx = line.indexOf("\"") + 1;

  		if (idx <= 0)
  			return "";

  		int idx1 = line.lastIndexOf("\"");

  		if (idx1 < 0)
  			return line.substring(idx);

  		if (idx == idx1)
  			return "";

  		return line.substring(idx, idx1);
    } catch (Exception ex) {
      throw new Exception("Missing quotes!");
    }
	}

	private void requirePair(String[] value, String text) throws Exception {
		if (value.length < 2)
			throw new Exception(text);
	}

	public String getValue(String line, String var) {
		int idx = line.indexOf(var);
		if (idx < 0)
			return "";

		String value = line.substring(idx + var.length());
		int fquote = value.indexOf("\"");
		int lquote = value.indexOf("\"", fquote + 1);

		return value.substring(fquote + 1, lquote).trim();
	}

	/**
	 * Initialization function for preparing all States Reads in experiment
	 * configuration file and generates all configured state information.
	 * 
	 * @throws Exception
	 * */
	public void initExperiment() throws Exception {
		final CmeApp thisApp = this;

		// Open the experiment file
		String line;
		String event = null;
		FileReader instFile;
		int lc = 0;

		String trialId = null;
		String dataset = null;
		BufferedReader bufReader = null;
		CmeState thisState = null;

		String validConstraints = "REGEX|RANGE|MATCH|MATCH_ANY";

		// Open the file
		try {
			instFile = new FileReader(m_sExpFileName);
		} catch (FileNotFoundException e1) {
			throw new Exception("Unable to open: " + m_sExpFileName);
		}

		String value = "";
		String[] splitValue;
		String lhs;
		String rhs;

		int stateSequence = -1;

		// Read the text strings and add them to the text area
		try {
			bufReader = new BufferedReader(instFile);
			while ((line = bufReader.readLine()) != null) {
				lc++;
				line = line.trim();

				if (line.length() == 0 || line.charAt(0) == '#')
					continue;

				if (line.contains("=") && line.charAt(0) != '<') {
					event = line.substring(0, line.indexOf('='));
				}

				value = getQuotedText(line);

				if (value.contains(":")) {
					splitValue = value.split(":");
					lhs = splitValue[0];
					rhs = splitValue[1];
				} else {
					splitValue = new String[] { value };
					lhs = value;
					rhs = "";
				}
				
				// See if we need to create a new State
				if (line.startsWith("<EXPERIMENT")) {
					if (line.contains("CMEF_VERSION")) {
						double vdub = Double.valueOf(value);
						if (vdub < m_MinVer)
							throw new Exception(
									"The selected Experiment file cannot be used with this version of CMEF.\n"
											+ "Please selected an experiment file which is written for CMEF v"
											+ m_MinVer
											+ " or greater, or view documentation for format changes between this version"
											+ " and version " + value + ".");
						if (vdub > m_Version)
							throw new Exception(
									"The selected Experiment file cannot be used with this version of CMEF.\n"
											+ "Please selected an experiment file which is written for CMEF v"
											+ m_Version
											+ " or earlier, or view documentation for format changes between this version"
											+ " and version " + value + ".");
					} else
						throw new Exception(
								"The Selected Experiment file does not contain version \n information "
										+ "and cannot be used with this version of CMEF.");

					configureGlobals();
				} else if (line.startsWith("<TRIAL")) {
					if (m_PairFactory == null)
						throw new Exception(
								"The data file has not been specified!\n"
										+ m_sExpFileName);
					if (line.contains("ID")) {
						value = getValue(line, "ID");
						trialId = value;
					}
					if (line.contains("DATASET")) {
						dataset = getValue(line, "DATASET");
					} else
						dataset = null;

					configureTrialGlobals(value);

				} else if (line.startsWith("</TRIAL")) {
					trialId = null;

				} else if (line.startsWith("<STATE>")) {
					if (thisState != null) {
						//thisState.printInstructionFiles();
					}

					thisState = new CmeState(this);
					stateSequence = -1;
					configureStateGlobals(thisState, trialId, dataset, stateSequence);

				} else if (line.contains("TITLE")) {
					if (thisState == null)
						m_eProperties.put("Title", value);
					else 
						thisState.setProperty("Title", value, stateSequence);

				} else if (line.contains("STUDY_NAME")) {
					m_eProperties.put("StudyName", value);

				} else if (line.matches(variableQuery("RANDOM_POOL"))) {
					requirePair(splitValue,
							"RANDOM_POOL must contain a name:value pair!");

					CmeSelectiveIter seliter = new CmeSelectiveIter(this);
					seliter.setList(rhs);
					m_vrPools.put(lhs, seliter);

				} else if (line.matches(variableQuery("FORMAT_FILE"))) {
					m_eProperties.put("FormatFile", value);
					validateFile(value);

				} else if (line.matches(variableQuery("DATA_FILE"))) {
					initDataSet(value);

				} else if (line.matches(variableQuery("ESET"))) {
					requirePair(splitValue, event
							+ " tag must contain the form 'property:value'");
					m_eProperties.put(lhs, rhs);

				} else if (line.matches(variableQuery("LIST_ESET"))) {
					requirePair(splitValue, event
							+ " tag must contain the form 'property:value'");
					compoundProperty(null, lhs, rhs);

				} else if (line.matches(variableQuery("CONDITIONS"))) {
					String valConditions = value.toUpperCase()
							.replace(',', ':');

					if (thisState == null)
						m_eProperties.put("ValidConditions", valConditions + ":");
					else
						thisState.setProperty("ValidConditions", valConditions + ":", -1);

					// EXCLUDE=
					// Excludes a property name from the long feedback list.
					// * Stores in Experiment when placed globally
					// * Stores in State when placed local to a <state></state>
				} else if (line.matches(variableQuery("EXCLUDE"))) {
					if (thisState == null)
						compoundProperty(null, "Exclude", value);
					else
						compoundProperty(thisState, "Exclude", value);

				} else if (thisState == null) {
					continue;

					// FILE=
					// File to be used for the instruction page.
					// File MUST exist!
				} else if (line.matches(variableQuery("FILE"))
						|| line.matches(variableQuery("FILE_SEQUENCE"))) {
					validateFile(value);
					thisState.addInstructionFile(value);
					stateSequence++;

					// PROMPT/POST_PROMPT/POST_STATE_PROMPT
					// Sets various prompt strings that occur at various times
					// during an experiment.
				} else if (line.matches(variableQuery("PROMPT"))) {
					thisState.setProperty("PromptText", value, stateSequence);

				} else if (line.matches(variableQuery("POST_PROMPT"))) {
					compoundProperty(thisState, "PostPromptText", value);

				} else if (line.matches(variableQuery("POST_STATE_PROMPT"))) {
					compoundProperty(thisState, "PostStatePromptText", value);

					// SCALE =
					// Sets the scale factor for a given image.
				} else if (line.matches(variableQuery("SCALE"))) {
					thisState.setProperty("Scale", value, stateSequence);

					// END =
					// Sets the action required to end a state.
					// Standard: "Click:Next" or "Click:Continue"
					// Value (Next,Continue) is placed on the button.
				} else if (line.matches(variableQuery("END"))) {
					requirePair(splitValue, event
							+ " must contain a name:value pair!");

					if (!setEvent(event, lhs, rhs, thisState, stateSequence))
						throw new Exception("Invalid State Interaction: "
								+ line);

				} else if (line.matches(variableQuery("BLANK"))) {
					requirePair(splitValue, event
							+ " must contain a name:value pair!");

					if (!setEvent(event, lhs, rhs, thisState, stateSequence))
						throw new Exception("Invalid State Interaction: "
								+ line);

				} else if (line.matches(variableQuery("NEXT"))) {
					if (thisState.getState() != CmeState.STATE_MULTIPLE)
						throw new Exception(
								"NEXT should only be used with a mode of Multiple, Recall, or Study");

					requirePair(splitValue, event
							+ " must contain a name:value pair!");

					if (!setEvent(event, lhs, rhs, thisState, stateSequence))
						throw new Exception("Invalid State Interaction: "
								+ line);

				} else if (line.matches(variableQuery("STUDY_LIMIT"))) {
					requirePair(splitValue, event
							+ " must contain a name:value pair!");

					if (rhs.toLowerCase().equals("unlimited"))
						rhs = String.valueOf(Integer.MAX_VALUE) + "ms";

					if (!setStudyLimit(event, lhs, rhs, thisState, stateSequence))
						throw new Exception("Invalid State Interaction: "
								+ line);

				} else if (line.matches(variableQuery("POOL"))
						|| line.matches(variableQuery("SET_POOL"))) {
					compoundProperty(thisState, "SetPool", value);

				} else if (line.matches(variableQuery("STATE_POOL"))) {
					compoundProperty(thisState, "StatePool", value);

				} else if (line.matches(variableQuery("SET"))) {
					requirePair(splitValue, event
							+ " must contain a name:value pair!");
          dmsg(CmeApp.DEBUG_PROPERTIES, lhs + " set to " + rhs + " for sequence " + String.valueOf(stateSequence));
					thisState.setProperty(lhs, rhs, stateSequence);

				} else if (line.matches(variableQuery("STUDY_FILE"))) {
					thisState.setProperty("StudyFile", value, stateSequence);
					thisState.setStudyInstruction(true);

				} else if (line.matches(variableQuery("DISPLAY_TIMER"))) {
					thisState.setProperty("DisplayTimer", value, stateSequence);

				} else if (line.matches(variableQuery("MODE"))) {
					String item = lhs.toUpperCase();

					if (item.contains("INSTRUCTION")) {
						thisState.setState(CmeState.STATE_INSTRUCTION);
					} else if (item.contains("FEEDBACK")) {
						thisState.setState(CmeState.STATE_INPUT);
					} else if (item.contains("INPUT")) {
						thisState.setState(CmeState.STATE_INPUT);
					} else if (item.contains("PROMPT")) {
						thisState.setState(CmeState.STATE_PROMPT);
					} else if (item.contains("STUDY")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else if (item.contains("RECALL")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else if (item.contains("MULTIPLE")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else
						throw new Exception("Invalid State Mode: '"
								+ value.toLowerCase() + "'");

				} else if (line.contains("</STATE>")) {
					// Add this one to the vector
					m_vStates.add(thisState);
				} else if (line.matches(variableQuery("NAME"))) {
					thisState.setProperty("FeedbackName", value, stateSequence);
				} else if (line.matches(variableQuery("SELECT"))) {
					thisState.setProperty("Select", value, stateSequence);
        } else if (line.matches(variableQuery("ITEMS")) || line.matches(variableQuery("COUNT"))) {
          thisState.setProperty("Count", value, stateSequence);
				} else if (line.matches(variableQuery("SETS")) || line.matches(variableQuery("GRIDS"))) {
					thisState.setProperty("Sets", value, stateSequence);

				} else if (line.matches(variableQuery("ITERATOR"))) {
					requirePair(splitValue, event + " must contain a name:value pair!");

					lhs = lhs.toUpperCase();
					rhs = rhs.toUpperCase();

					String[] types = null;
					if (rhs.contains("|"))
						types = rhs.split("|");
					else
						types = new String[] { rhs };

          CmeIterator iter = getIterator(lhs, types, thisState);
          String group = thisState.getStringProperty("CurrentDataset");

          if (group == null)
            m_PairFactory.clearDataSet();
          else {
	          String[] groups = group.split(",");
            m_PairFactory.setDataSets(groups);
          }

          iter.initIterator(0, m_PairFactory.getCount() - 1);

          if (thisState.setIterator(iter) != 0)
            throw new Exception("Invalid Iterate: " + lhs + ":" + rhs);

				} else if (line.matches(variableQuery("CONSTRAINTS"))) {
					requirePair(splitValue, event
							+ " must contain a name:value pair!");

					if (validConstraints.contains(lhs.toUpperCase())) {
						thisState.setProperty("ConstraintType", lhs, stateSequence);
						thisState.setProperty("Constraint", rhs, stateSequence);
						if (splitValue.length > 2)
							thisState.setProperty("ConstraintParam", splitValue[2], stateSequence);
					} else
						throw new Exception("Invalid constraint type: " + lhs);
				} else if (line.contains("</EXPERIMENT>")) {
					break;
				} else {
					throw new Exception("Unhandled Line: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage() + ": " + m_sExpFileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Configuration File Parsing Failure (line:"
					+ Integer.toString(lc) + "): \n" + e.getMessage());
		}

		// dmsg(5, "Experiment Init Successful!");
	}

	private void validateFile(String insFile) throws Exception {
		// Open the file
		try {
			FileReader file = new FileReader(insFile);
		} catch (FileNotFoundException e1) {
			throw new Exception("Unable to open: " + insFile);
		}
	}

	private void adjustAllLayouts() {

		m_ViewHandler.adjustLayout();

		/* Ensure adjustLayout gets called next time! */
		m_dOldDims = (Dimension) this.getSize().clone();
	}

	public boolean handleCompoundProperty(CmeState state, String property,
			CmeStringHandler handler) {
		if (property == null || handler == null)
			return false;

		Object obj;
		if (state != null)
			obj = state.getProperty(property);
		else
			obj = m_eProperties.get(property);

		if (obj == null) {
			return false;
		}

		if (obj instanceof String)
			return handler.handleString((String) obj);

		Vector<String> str = (Vector<String>) obj;
		boolean ret = true;

		for (int x = 0; x < str.size(); x++) {
			ret = (ret && handler.handleString(str.get(x)));
		}

		return ret;
	}

	private boolean isExcluded(String exc) {
		final String fexc = exc;
		boolean ret = handleCompoundProperty(m_CurState, "Exclude",
				new CmeStringHandler() {
					public boolean handleString(String regex) {
						regex = m_CurState.translateString(regex);
						return fexc.matches(regex);
					}
				});
		ret = ret
				|| handleCompoundProperty(null, "Exclude",
						new CmeStringHandler() {
							public boolean handleString(String regex) {
								regex = translateString(regex);
								return fexc.matches(regex);
							}
						});
		return ret;
	}

	public String translateString(String text) {
		String ret = CmeApp.translateString(m_eProperties, text, true);
		return CmeApp.translateString(m_fbHashmap, ret, true);
	}

	public static String translateString(HashMap<String, Object> Properties,
			String text, boolean ignoreUndefined) {
		text = text.replace("$$", "$ ");

		Vector<String> propList = getVariableList(text);
		Comparator<String> comparator = Collections.reverseOrder();
		Collections.sort(propList, comparator);

		Iterator<String> viter = propList.iterator();
		while (viter.hasNext()) {
			String variable = viter.next();

			Object value = Properties.get(variable);
			if (value == null) {
				if (!ignoreUndefined) {
					text = text.replace("$" + variable, "");
					text = text.replace("${" + variable + "}", "");
				}
				continue;
			}

			if (value instanceof String) {
				text = text.replace("$" + variable, (String) value);
				text = text.replace("${" + variable + "}", (String) value);
			}
		}

		viter = propList.iterator();
		while (viter.hasNext()) {
			String variable = viter.next();

			Object value = Properties.get(variable);
			if (value == null) {
				if (!ignoreUndefined) {
					text = text.replace("$" + variable + "[.*]", "");
					text = text.replace("${" + variable + "}[.*]", "");
				}
				continue;
			}

			if (!(value instanceof String)) {
				Vector<String> strArray = (Vector<String>) value;
				for (int x = 0; x < strArray.size(); x++) {
					String str = strArray.get(x);
					String idx = Integer.toString(x);
					text = text.replace("$" + variable + "[" + idx + "]",
							strArray.get(x));
					text = text.replace("${" + variable + "}[" + idx + "]",
							strArray.get(x));
				}

				text = text.replace("$" + variable + "[.*]",
						"index out of bounds");
				text = text.replace("${" + variable + "}[.*]",
						"index out of bounds");
				text = text.replace("$" + variable, strArray.toString());
				text = text.replace("${" + variable + "}", strArray.toString());
			}
		}

		text = text.replace("$ ", "$");

		return text;
	}

	private static Vector<String> getVariableList(String text) {
		Vector<String> propertyList = new Vector<String>();
		text = text.replace("${", "$");
		int lastOccurance = text.indexOf('$', 0);
		int endCharacter = 0;

		while (lastOccurance != -1 && lastOccurance < text.length()) {

			for (int x = lastOccurance + 1; x < text.length(); x++) {
				if (!Character.isDigit(text.charAt(x))
						&& !Character.isLetter(text.charAt(x))
						&& text.charAt(x) != '_') {

					endCharacter = x;
					break;
				} else if (x == text.length() - 1) {
					endCharacter = x + 1;
					break;
				}
			}

			if (lastOccurance + 1 >= text.length()) {
				break;
			}

			/* is the variable name valid? */
			if (Character.isDigit(text.charAt(lastOccurance + 1))
					|| Character.isLetter(text.charAt(lastOccurance + 1))
					|| text.charAt(lastOccurance + 1) == '_') {
				propertyList.add(text
						.substring(lastOccurance + 1, endCharacter));
			}

			if (endCharacter >= text.length()
					|| text.charAt(endCharacter) == '$') {
				lastOccurance = endCharacter;
			} else {
				lastOccurance = text.indexOf('$', endCharacter);
			}
		}

		return propertyList;
	}

	public boolean conditionInSet(String set) {
		if (set == null)
			return false;
		set = ":"
				+ set.replace(" ", "").replace("\t", "").replace(",", ":")
						.replace("|", ":").toLowerCase() + ":";
		String expCondition = (String) m_eProperties.get("ExpCondition");
		if (expCondition == null)
			return false;
		return set.contains(":" + expCondition.toLowerCase() + ":");
	}

	/**
	 * Validates that a given condition is valid.
	 * 
	 * @param condition
	 *            - the condition to be validated
	 * @return true if condition is valid, else false;
	 * 
	 * @see property ValidConditions is populated with a string of the form:
	 *      ":cond1:cond2:cond3:"
	 */
	public boolean validateCondition(String condition) {
		String validConditions = m_eProperties.get("ValidConditions")
				.toString();
		if (validConditions == null) {
			return false;
		}
		return validConditions.contains(condition + ":");
	}

	/**
	 * Set the condition property for the current experiment run.
	 * 
	 * @param condition
	 *            - condition to be set
	 * @return true if condition is valid, else false
	 */
	public boolean setCondition(String condition) {
		if (validateCondition(condition)) {
			//System.out.println("Set Condition!");
			m_eProperties.put("ExpCondition", condition);
			return true;
		}
		return false;
	}

	/**
	 * @return true if there is more than possible condition.
	 */
	public boolean multipleConditions() {
		String validConditions = m_eProperties.get("ValidConditions")
				.toString();
		if (validConditions == null) {
			return false;
		}

		String conds[] = validConditions.split(":");
		if (conds.length > 1)
			return true;
		
		if (conds.length == 1)
			m_eProperties.put("ExpCondition", conds[0]);
		
		return false;
	}

	// --------------------------------------------------------------------
	/**
	 * Post a message to the report file
	 * 
	 * @param msg
	 *            - String to write out to file
	 * @param giveTime
	 *            - Boolean indicating if time should be output also
	 */
	// --------------------------------------------------------------------
	public void postStatusMessage(String msg, boolean giveTime) {
		String line;
		try {
			if (giveTime) {
				// Add time hack in front of string
				line = timeToString() + msg + "\n";
			} else {
				line = msg + "\n";
			}
			m_bufWriter.write(line, 0, line.length());
			m_bufWriter.flush();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this,
					"Error: Unable to write to report file.",
					"Error Writing Report File", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * @param filePath
	 *            the name of the file to open.
	 */
	public static String readFile(String filePath) throws java.io.IOException {
		StringBuilder fileData = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = new String();

		while ((line = reader.readLine()) != null) {
			fileData.append(line);
		}

		reader.close();
		return fileData.toString();
	}

	public String translatePrompt(String text) {
		if (text == null)
			return null;

		int prompt = 0;

		int col = text.indexOf(":");
		if (col >= 0) {
			if (!conditionInSet(text.substring(0, col))) {
				//System.out.println("Condition invalid!"
				//		+ text.substring(0, col));
				return null;
			}
			prompt = col + 1;
		}

		text = text.substring(prompt, text.length());
		text = m_CurState.translateString(text);
		text = this.translateString(text);

		return text;
	}

	public void displayPrompt(String post) {
		displayPrompt(post, null);
	}

	public int getJOptionFromString(String buttons) {
		int opt = JOptionPane.NO_OPTION;

		if (buttons != null) {
			Object buttonProp = m_CurState.getProperty(buttons);
			if (buttons instanceof String) {
				String buttonString = (String) buttonProp;
				if (buttonString == null)
					return JOptionPane.NO_OPTION;

				buttonString.toLowerCase();

				if (buttonString.equals("yesno"))
					opt = JOptionPane.YES_NO_OPTION;
				else if (buttonString.equals("okcancel"))
					opt = JOptionPane.OK_CANCEL_OPTION;
			}
		}

		return opt;
	}

	/** Prompt the pre prompt */
	public boolean displayPrompt(String post, String buttons) {
		if (m_CurState == null) {
			//System.out.println("No State!");
			return true;
		}

		int opt = getJOptionFromString(buttons);
		int ret = 0;

		Object property = m_CurState.getProperty(post);
		if (property == null) {
			//System.out.println("No Property (" + post + ")!");
			return true;
		}

		if (property instanceof String) {
			String text = translatePrompt((String) property);

			if (text == null)
				return true;

			if (opt == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(this, text, "Info", opt);
			} else
				ret = JOptionPane.showConfirmDialog(this, text, "Info", opt);
		} else {
			Vector<String> texts = (Vector<String>) property;
			for (int x = 0; x < texts.size(); x++) {
				String text = translatePrompt(texts.get(x));
				if (text == null)
					continue;

				if (opt == JOptionPane.NO_OPTION) {
					JOptionPane.showMessageDialog(this, text, "Info", opt);
				} else
					ret = JOptionPane
							.showConfirmDialog(this, text, "Info", opt);
			}
		}

		switch (opt) {
		case JOptionPane.OK_CANCEL_OPTION:
			return (ret == JOptionPane.OK_OPTION);
		case JOptionPane.YES_NO_OPTION:
			return (ret == JOptionPane.YES_OPTION);
		}

		return true;
	}

	public void skipInvalidConditions() {
		String sCondition = ((String) m_eProperties.get("ExpCondition"));

		while (m_CurState != null) {
			String stateConditions = (String) m_CurState
					.getProperty("ValidConditions");

			if (stateConditions == null)
				break;

			if (stateConditions.contains(sCondition + ":"))
				break;

			// dmsg(5, "Skipping State: Condition: :" + sCondition
			//		+ ":, Condition Expected: " + stateConditions);

			m_CurState = m_cIterator.next();
		}
	}

	// -----------------------------------------------
	/** Set the next state */
	// -----------------------------------------------
	public void setNextState() {
    dmsg(CmeApp.DEBUG_STATES, "setNextState()");

		if (m_cIterator == null) {
			m_cIterator = m_vStates.iterator();
		} else if (!m_ViewHandler.isProvidedFeedbackValid()) {
			String obj = m_ViewHandler.getObjective();

			if (obj == null)
				return;

			JOptionPane.showMessageDialog(this, obj, "Input Error!",
					JOptionPane.ERROR_MESSAGE);
			return;
		} else if (!m_ViewHandler.allowNextState())
			return;

		displayPrompt("PostStatePromptText");
		if (m_cIterator.hasNext()) {

			if (m_CurState != null)
				m_CurState.clean();
			m_CurState = m_cIterator.next();

			skipInvalidConditions();

			displayPrompt("PreStatePromptText");
		} else {
			System.exit(0);
		}

		// dmsg(5, "Next State!");

		try {
			m_ViewHandler.setState(m_CurState);
			// m_StudyHandler.setState(m_CurState);
			// m_ExperimentHandler.setState(m_CurState);
			m_CurState.init();
      m_CurState.initSequence();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"FATAL Error:\n" + ex.toString() + "\n" + ex.getMessage(),
					"Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	// ----------------------------------------------------------
	/**
	 * Convert a time in milliseconds to a string giving the time since the
	 * start of the experiment
	 */
	// ----------------------------------------------------------
	private String timeToString() {
		long currentTimeMillis = System.currentTimeMillis();
		double tDiffSec = (double) (currentTimeMillis - m_lStartTimeMillis) / 1000.0;
		double hours = tDiffSec / 3600.0;
		double minutes = (hours - Math.floor(hours)) * 60.0;
		double seconds = (minutes - Math.floor(minutes)) * 60.0;

		int h, m, s;
		h = (int) (Math.floor(hours));
		m = (int) (Math.floor(minutes));
		s = (int) (Math.round(seconds));

		String tStr = new String("");
		if (h < 10) {
			tStr = tStr.concat("0" + String.valueOf(h) + ":");
		} else {
			tStr = tStr.concat(String.valueOf(h) + ":");
		}
		if (m < 10) {
			tStr = tStr.concat("0" + String.valueOf(m) + ":");
		} else {
			tStr = tStr.concat(String.valueOf(m) + ":");
		}
		if (s < 10) {
			tStr = tStr.concat("0" + String.valueOf(s));
		} else {
			tStr = tStr.concat(String.valueOf(s));
		}

		return tStr;
	}

	/** Everything states in main */
	public static void main(String[] args) {
		int len = args.length;
		int options = 0;
		int debug = 0x0;
		String expFile = "Instructions/Experiment.txt";
		for (int x = 0; x < len; x++) {
			if (args[x].equals("-d") || args[x].equals("--debug")) {
				try {
					debug = Integer.parseInt(args[x + 1]);
				} catch (Exception ex) {
					debug = 0;
				}
        System.err.println("Debug Level: " + String.valueOf(debug));
			}
			if (args[x].equals("-x") || args[x].equals("--experiment")) {
				if (++x >= len)
					break;
				expFile = args[x];
			}
			if (args[x].equals("-r") || args[x].equals("--refresh")) {
				options |= CmeApp.CME_ENABLE_REFRESH;
			}
			if (args[x].equals("-t") || args[x].equals("--text")) {
				options |= CmeApp.CME_TEXT_ONLY;
			}
		}

		@SuppressWarnings("unused")
		final CmeApp theApp = new CmeApp(debug, options, expFile);

		Runnable localRunnable = new Runnable() {
			public void run() {
				try {
					theApp.printResults();
				} catch (Exception ex) {
					System.out.println("Failed to properly save data!");
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(new Thread(localRunnable));

		theApp.run();
	}

	public void addFeedback(CmeResponse response) throws Exception {
		if (response == null) {
			throw new Exception("Invalid response!");
		}
		//System.err.println(response.getName());
		addFeedback(response.getName(), response.getValue());
	}

	public void addFeedback(String name, String value) throws Exception {
		m_fbHashmap.put(name, value);
		if (isExcluded(name)) {
			//System.out.println("excluded? " + name);
			return;
		}
		m_fbHashmap.put(name + "Time",
				Double.toString(((double) m_iTickCounter) / 1000.0));
		for (int x = 0; x < m_fbVectString.size(); x++) {
			if (m_fbVectString.get(x).equals(name)) {
				m_fbVectString.remove(x);
				break;
			}
		}
		m_fbVectString.add(name);
		/* // dmsg(0xFF, "Current Feedback(nv): " + m_fbHashmap.toString()); */
	}

	public String getFeedback(String name) {
		// dmsg(0xFF, "getting feedback: " + name);
		return (String) m_fbHashmap.get(name);
	}

	private void printFormattedOutput() {
		String formatFile = (String) m_eProperties.get("FormatFile");
		if (formatFile == null)
			return;

		// Open the experiment file
		String line;
		FileReader instFile;
		BufferedReader bufReader = null;

		// Open the file
		try {
			instFile = new FileReader(formatFile);
		} catch (FileNotFoundException e1) {
			System.out.println("File Not Found!");
			return;
		}

		postStatusMessage(
				"----------------Start Formatted Output-----------------\n\n",
				false);

		// Read the text strings and add them to the text area
		try {
			bufReader = new BufferedReader(instFile);
			while ((line = bufReader.readLine()) != null) {
				line = CmeApp.translateString(m_fbHashmap, line, true);
				line = CmeApp.translateString(m_eProperties, line,
						(m_iDebugLevel > 0));
				postStatusMessage(line, false);
			}
		} catch (Exception ex) {
			System.out.println("Failed to parse the OutputFormat file!");
			return;
		}

		postStatusMessage(
				"-----------------End Formatted Output------------------\n\n",
				false);
	}

	public void printResults() throws Exception {
		Calendar expCalendar = Calendar.getInstance();

		int day = expCalendar.get(Calendar.DAY_OF_MONTH);
		int month = expCalendar.get(Calendar.MONTH) + 1; // Calendar months are
															// numbered from 0
		int year = expCalendar.get(Calendar.YEAR);

		String fileName = new String(m_eProperties.get("StudyName").toString()
				+ "_"
				+ m_eProperties.get("ExpCondition").toString()
				+ "_SID"
				+ m_eProperties.get("SubjectID").toString()
				+ "_"
				+ ((day < 10) ? ("0" + String.valueOf(day))
						: String.valueOf(day))
				+ ((month < 10) ? ("0" + String.valueOf(month))
						: String.valueOf(month)) + year + ".txt");

		try {

			// Open the report file
			m_fileReport = new File(fileName);
			// Place it in the FileWriter
			m_fileWriter = new FileWriter(m_fileReport);
			// Place that in the BufferedWriter
			m_bufWriter = new BufferedWriter(m_fileWriter);

			postStatusMessage("Subject Test Time: "
					+ expCalendar.getTime().toString() + "\n", false);

			printFormattedOutput();

			String line = new String();
			for (int x = 0; x < m_fbVectString.size(); x++) {
				String name = m_fbVectString.get(x);
				String value = (String) m_fbHashmap.get(name);
				String time = (String) m_fbHashmap.get(name + "Time");
				line = time + "," + name + "," + value;
				postStatusMessage(line, false);
			}
		} catch (Exception e) {
			throw new Exception("Unable to write to report file: "
					+ e.getMessage());
		}
	}
}
