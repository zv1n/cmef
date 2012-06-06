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
/** Custom Memory Experiment Framework
 *  <P>Purpose: This application was designed and written for use
 *  as the primary experimental software for an experiment in 
 *  learning conducted by Jodi Price.
 *  @author Terry Meacham
 *  @version 2.0
 *  Date: May 2011
 */
//===================================================================
@SuppressWarnings("serial")
public class CmeApp extends JFrame implements AncestorListener
{

	/** Main panel */
	private JPanel m_MainPanel;
	private Dimension m_dOldDims;
	/** Instructions Handler */
	private CmeInstructions m_InstructionsHandler;
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
	final static private double m_MinVer = 2.0;
	
	public static final int CME_ENABLE_REFRESH = 0x1;
	public static final int CME_TEXT_ONLY = 0x2;
	//------------------------------------------------------------------
	// State variables for storing state information loaded from file.
	//------------------------------------------------------------------
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
	//------------------------------------------------------------------
	// Data storage components
	//------------------------------------------------------------------
	/** File to write report to */
	private File m_fileReport;
	/** The file writer encapsulating the report file */
	private FileWriter m_fileWriter;
	/** The buffered writer encapsulating the file writer */
	private BufferedWriter m_bufWriter;
	/** Title font used for large labels */
	public static final Font SysTitleFontB = new Font("SansSerif", Font.BOLD, 28);
	/** Font used in the lists */
	public static final Font SysButtonFontB = new Font("SansSerif", Font.BOLD, 17);
	/** Label font used for most labels */
	public static final Font SysLabelFontB = new Font("SansSerif", Font.BOLD, 21);
	/** Label font used for most labels */
	public static final Font SysInstructionFont = new Font("Courier", Font.PLAIN, 18);
	/** Label font used for most labels */
	public static final Font SysLabelFontP = new Font("SansSerif", Font.PLAIN, 20);
	/** Small font used for instructions and labels on text widgets */
	public static final Font SysSmallFont = new Font("SansSerif", Font.PLAIN, 18);

	/**
	 * Pop-up a message box to be used for debug messages.
	 * @param level - minimum debug level to show the MsgBox
	 * @param msg - the msg to be displayed
	 */
	public void dmsg(int level, String msg) {
		if (level >= (m_iDebugLevel & 0xFF)) {
			System.out.println("Dmsg: " + msg);
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
	 * @param debugLevel - initial application debug level
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
			dmsg(5, "Title Set");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
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
		m_MainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_MainPanel.addAncestorListener(this);
		this.getContentPane().add(m_MainPanel);
	}

	/**
	 * Initialization function for preparing all State handlers
	 * (CmeExperiment,CmeInstructions,CmeStudy)
	 */
	private void initStateHandlers() throws Exception {

		m_InstructionsHandler = new CmeInstructions(this, m_iOptions);
		m_InstructionsHandler.setPairFactory(m_PairFactory);
		m_InstructionsHandler.addAncestorListener(this);
		m_InstructionsHandler.setVisible(false);
		m_MainPanel.add(m_InstructionsHandler);
		
		m_ExperimentHandler = new CmeExperiment(this);
		m_ExperimentHandler.setPairFactory(m_PairFactory);
		m_ExperimentHandler.addAncestorListener(this);
		m_ExperimentHandler.setVisible(false);
		m_MainPanel.add(m_ExperimentHandler);

		dmsg(5, "State Handler Init Successful!");
	}

	/**
	 * Used to initialize the SubjectID and Experiment Condition.
	 */
	private void initParticipantData() {
		String sCondition;
		String sSubjectId;

		if ((m_iDebugLevel & 0x100) == 0x0) {

			do {
				sSubjectId = JOptionPane.showInputDialog("Please enter your subject ID:");
				if (sSubjectId == null) {
					System.exit(0);
				}
			} while (sSubjectId == "");

			m_eProperties.put("SubjectID", sSubjectId);
			dmsg(5, "Subject Complete!");

			do {
				sCondition = JOptionPane.showInputDialog("Please enter the experimental Condition\n"
						+ "(This should be given by the experimenter).");
				if (sCondition == null) {
					System.exit(0);
				}
			} while (!setCondition(sCondition.toUpperCase()));
			dmsg(5, "Conditions Complete!");

		} else {
			m_eProperties.put("SubjectID", "TESTID");
			m_eProperties.put("ExpCondition", "B");
		}
	}
	
	private void configureTrialGlobals(String id) {
		id = id.trim();
		Vector<String> typeList = m_PairFactory.getTypeList();
		
		for (int x=0; x<typeList.size(); x++) {
			System.err.println(typeList.get(x) + "Count_T" + id);
			m_eProperties.put(typeList.get(x) + "Count_T" + id, "0");
			m_eProperties.put(typeList.get(x) + "Points_T" + id, "0");
		}

		m_eProperties.put("TotalCount_T" + id, "0");
		m_eProperties.put("TotalPoints_T" + id, "0");
	}
	
	private void configureStateGlobals(CmeState state, String trialId) {
		if (trialId != null)
			state.setProperty("CurrentTrial", trialId);

		state.setProperty("MatchCount", "3");
	}

	private void configureGlobals() {
		m_eProperties.put("TotalCount", "0");
		m_eProperties.put("TotalPoints", "0");
	}
	
	private int setIterator(String iterator, String[] type, CmeState state) {
		String validIterators = "RANDOM:SELECTIVE:DIFFICULTY";
		String validTypes = "NONEXCLUSIVE:REVERSE:DESCENDING:ASCENDING";
		int itype = 0;
		int iiter = 0;

		if (!validIterators.contains(iterator)) {
			return -1;
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

		for (int x=0; x<type.length; x++) {
			if (!validTypes.contains(type[x])) {
				return -1;
			}
			System.out.println("Iterator Type: " + type[x]);

			if	(type[x].equals("EXCLUSIVE")) {
				itype |= CmeIterator.EXCLUSIVE;
			} else if (type[x].equals("NONEXCLUSIVE")) {
				itype |= CmeIterator.NONEXCLUSIVE;
			} else if (type[x].equals("REVERSE") || type[x].equals("DESCENDING")) {
				itype |= CmeIterator.REVERSE;
			} else if (type[x].equals("ASCENDING")) {
				itype &= ~CmeIterator.REVERSE;
			}
		}

		dmsg(0xFF, "Set Iterator: " + Integer.toString(this.m_PairFactory.getCount()));

		CmeIterator iter = m_IteratorFactory.createIterator(iiter);
		iter.initIterator(itype, 0, this.m_PairFactory.getCount() - 1);

		return state.setIterator(iter);
	}

	private boolean setStudyLimit(String action, String lhs, String rhs, CmeState state) throws Exception {
		if (state == null)
			return false;
		
		if (lhs.equals("time")) {
			int limit = CmeTimer.getDelayStringValue(rhs);
			
			System.out.println("Limit: " + limit);
			state.setProperty("Limit", String.valueOf(limit));
			return true;
		}
		
		return false;
	}
	
	private boolean setEvent(String action, String lhs, String rhs, CmeState state) {
		final CmeApp thisApp = this;
		int type = CmeState.EVENT_MAX;
		CmeEventResponse response = null;

		if (action == null) {
			return false;
		}

		if (m_EndResponse == null)
			m_EndResponse = new CmeEventResponse() {

				public void Respond() {
					System.out.println("End");
					thisApp.setNextState();
				}
			};
		
		if (m_NextResponse == null)
			m_NextResponse = new CmeEventResponse() {
				public void Respond() {
					System.out.println("Next");
					try {
						if (!m_InstructionsHandler.setNextInSequence()) {
							thisApp.setNextState();
						}
					} catch (Exception ex) {
						thisApp.dmsg(0, ex.getMessage());
					}
				}
			};
		
		if (m_BlankResponse == null)
			m_BlankResponse = new CmeEventResponse() {
				public void Respond() {
					System.out.println("Blank");
					try {
						m_InstructionsHandler.blankInstructions();
					} catch (Exception ex) {
						thisApp.dmsg(0, ex.getMessage());
					}
				}
			};
		
		if (action.equals("END")) {
			response = m_EndResponse;
		} else if (action.equals("NEXT")) {
			response = m_NextResponse;
		} else if (action.equals("BLANK")) {
			response = m_BlankResponse;
		} else {
			System.out.println("|" + action + "|");
			return false;
		}

		if (lhs.equals("Click")) {
			type = CmeState.EVENT_CLICK_PRIMARY;

			if (rhs != null && rhs.length() > 0) {
				state.setProperty("PrimaryButtonText", rhs);
			}
		} else if (lhs.equals("Time")) {
			type = CmeState.EVENT_TIME;
			CmeTimer timer = new CmeTimer();

			if (!timer.setDelayByString(rhs)) {
				System.out.println("Delay By String");
				return false;
			}
			if (!timer.setResponse(response)) {
				System.out.println("Response");
				return false;
			}
			if (!state.addEventTimer(timer)) {
				System.out.println("Event");
				return false;
			}
		}

		state.addEventResponse(type, response);
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
			newProp.add((String)property);
		} else {
			newProp = (Vector<String>)property;
		}
		
		newProp.add(value);
		
		if (state == null)
			m_eProperties.put(name, newProp);
		else
			state.setProperty(name, newProp);
	}
	
	/** Used to safely retrieve a string. */
	public String getQuotedText(String line) {
		int idx = line.indexOf("\"");
		
		if (idx < 0)
			return "";
		
		int idx1 = line.lastIndexOf("\"");
		
		if (idx1 < 0)
			return line.substring(idx);
		
		if (idx == idx1)
			return "";
		
		return line.substring(idx+1, idx);
	}
	
	/** 
	 * Initialization function for preparing all States
	 *  Reads in experiment configuration file and generates 
	 *  all configured state information.
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
		BufferedReader bufReader = null;
		CmeState thisState = null;

		String validConstraints = "REGEX|RANGE|MATCH";

		// Open the file
		try {
			instFile = new FileReader(m_sExpFileName);
		} catch (FileNotFoundException e1) {
			throw new Exception("Unable to open: " + m_sExpFileName);
		}

		// Read the text strings and add them to the text area
		try {
			bufReader = new BufferedReader(instFile);
			while ((line = bufReader.readLine()) != null) {
				lc++;
				line = line.trim();
				if (line.length() == 0 || line.trim().charAt(0) == '#')
					continue;

				if (line.contains("=") && line.charAt(0) != '<') {
					event = line.substring(0, line.indexOf('='));
				}

				// See if we need to create a new State
				// See if we need to create a new State
				if (line.startsWith("<EXPERIMENT")) {
					if (line.contains("CMEF_VERSION")) {
						String version = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
						double vdub = Double.valueOf(version);
						if (vdub < m_MinVer)
							throw new Exception ("The selected Experiment file cannot be used with this version of CMEF.\n" + 
												 "Please selected an experiment file which is written for CMEF v" + m_MinVer + 
												 " or greater.");
					} else
						throw new Exception ("The Selected Experiment file does not contain version \n information " +
											"and cannot be used with this version of CMEF.");
					
					configureGlobals();
				} else if (line.startsWith("<TRIAL")) {
					if (m_PairFactory == null)
						throw new Exception ("The data file has not been specified!\n" + m_sExpFileName);
					trialId = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					
					configureTrialGlobals(trialId);
				} else if (line.startsWith("</TRIAL")) {
					trialId = null;
				} else if (line.startsWith("<STATE>")) {
					thisState = new CmeState(this);
					configureStateGlobals(thisState, trialId);
				} else if (line.contains("TITLE")) {
					String title = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					if (thisState == null) {
						m_eProperties.put("Title", title);
					} else {
						thisState.setProperty("Title", title);
					}
				} else if (line.contains("STUDY_NAME")) {
					String study = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					m_eProperties.put("StudyName", study);
				} else if (line.startsWith("RANDOM_POOL=")) {
					int colon = line.indexOf(":");
					
					String name = line.substring(line.indexOf("\"") + 1, colon);
					String values = line.substring(colon + 1, line.lastIndexOf("\""));
					
					CmeSelectiveIter seliter = new CmeSelectiveIter(this);
					seliter.setList(values);
					m_vrPools.put(name, seliter);
				} else if (line.startsWith("FORMAT_FILE=")) {
					String formatfile = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					m_eProperties.put("FormatFile", formatfile);
					validateFile(formatfile);
				} else if (line.startsWith("DATA_FILE=")) {
					String formatfile = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					initDataSet(formatfile);
				} else if (line.startsWith("ESET=")) {
					String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					String[] props = value.split(":");
					if (props.length != 2)
						throw new Exception("ESET tag must contain the form 'property:value'");
					m_eProperties.put(props[0], props[1]);
				} else if (line.contains("CONDITIONS=")) {
					String valConditions = line.substring(line.indexOf("\"") + 1,
							line.lastIndexOf("\"")).toUpperCase().replace(',', ':');
					if (thisState == null) {
						m_eProperties.put("ValidConditions", ":" + valConditions + ":");
					} else {
						thisState.setProperty("ValidConditions", ":" + valConditions + ":");
					}
				} else if (line.startsWith("EXCLUDE=")) {
					String exclude = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					compoundProperty(null, "Exclude", exclude);
				} else if (thisState == null) {
					continue;
				} else if (line.startsWith("FILE=")) {
					String insFile = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("InstructionFile", insFile);
					validateFile(insFile);
				} else if (line.startsWith("PROMPT=")) {
					String promptText = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("PromptText", promptText);
					
				} else if (line.startsWith("POST_PROMPT=")) {
					String postPrompt = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					compoundProperty(thisState, "PostPromptText", postPrompt);
					
				} else if (line.startsWith("POST_STATE_PROMPT=")) {
					String postPrompt = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					compoundProperty(thisState, "PostStatePromptText", postPrompt);	
				} else if (line.contains("SCALE")) {
					String scaleText = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("Scale", scaleText);
					
				} else if (line.startsWith("EXCLUDE=")) {
					String exclude = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					compoundProperty(thisState, "Exclude", exclude);
					
				} else if (line.contains("END")) {
					String lhs = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":"));
					String rhs = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\""));

					if (!setEvent(event, lhs, rhs, thisState)) {
						throw new Exception("Invalid State Interaction: " + line);
					}
				} else if (line.contains("BLANK")) {
					String lhs = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":"));
					String rhs = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\""));

					if (!setEvent(event, lhs, rhs, thisState)) {
						throw new Exception("Invalid State Interaction: " + line);
					}
				} else if (line.startsWith("NEXT=")) {
					if (thisState.getState() != CmeState.STATE_MULTIPLE) {
						throw new Exception("Invalid State Interaction: " + line);
					}

					String lhs = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":"));
					String rhs = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\""));

					if (!setEvent(event, lhs, rhs, thisState)) {
						throw new Exception("Invalid State Interaction: " + line);
					}
				} else if (line.contains("STUDY_LIMIT")) {
					String lhs = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":"));
					String rhs = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\""));
					
					System.out.println(rhs);
					if (rhs.toLowerCase().equals("unlimited"))
						rhs = String.valueOf(Integer.MAX_VALUE)  + "ms";
					System.out.println(rhs);
					
					if (!setStudyLimit(event, lhs, rhs, thisState)) {
						throw new Exception("Invalid State Interaction: " + line);
					}
				} else if (line.startsWith("POOL=")) {
					String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					compoundProperty(thisState, "Pool", value);
				} else if (line.startsWith("SET=")) {
					String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					String[] props = value.split(":");
					if (props.length != 2)
						throw new Exception("SET tag must contain the form 'property:value'");
					thisState.setProperty(props[0], props[1]);
				} else if (line.contains("STUDY_FILE")) {
					String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("StudyFile", value);
					thisState.setStudyInstruction(true);
				} else if (line.contains("DISPLAY_TIMER")) {
					String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).toLowerCase();
					thisState.setProperty("DisplayTimer", value);
				} else if (line.contains("MODE")) {
					if (line.toUpperCase().contains("INSTRUCTION")) {
						thisState.setState(CmeState.STATE_INSTRUCTION);
					} else if (line.toUpperCase().contains("FEEDBACK")) {
						thisState.setState(CmeState.STATE_INPUT);
					} else if (line.toUpperCase().contains("INPUT")) {
						thisState.setState(CmeState.STATE_INPUT);
					} else if (line.toUpperCase().contains("PROMPT")) {
						thisState.setState(CmeState.STATE_PROMPT);
					} else if (line.toUpperCase().contains("STUDY")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else if (line.toUpperCase().contains("RECALL")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else if (line.toUpperCase().contains("MULTIPLE")) {
						thisState.setState(CmeState.STATE_MULTIPLE);
					} else {
						String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")).toLowerCase();
						throw new Exception("Invalid State Mode: '" + value + "'");
					}

				} else if (line.contains("</STATE>")) {
					// Add this one to the vector
					m_vStates.add(thisState);
				} else if (line.contains("NAME=")) {
					String name = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("FeedbackName", name);
				} else if (line.contains("SELECT=")) {
					String selectCount = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("Select", selectCount);
				} else if (line.contains("ITEMS=") || line.contains("COUNT=")) {
					String count = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("Count", count);
				} else if (line.contains("SETS=") || line.contains("GRIDS=")) {
					String set = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
					thisState.setProperty("Sets", set);
				} else if (line.contains("ITERATOR=")) {
					String iterator = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":")).toUpperCase();
					String type = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\"")).toUpperCase();
					
					String[] types = null;
					if (type.contains("|"))
						types = type.split("|");
					else
						types = new String[] {type};
					
					if (setIterator(iterator, types, thisState) != 0) {
						throw new Exception("Invalid Iterate: " + iterator + "|" + type);
					}
				} else if (line.contains("CONSTRAINTS=")) {
					String ctype = line.substring(line.indexOf("\"") + 1, line.lastIndexOf(":"));
					String constraint = line.substring(line.indexOf(":") + 1, line.lastIndexOf("\""));
					if (validConstraints.contains(ctype.toUpperCase())) {
						thisState.setProperty("ConstraintType", ctype);
						thisState.setProperty("Constraint", constraint);
					} else {
						throw new Exception("Invalid constraint type: " + ctype);
					}
				} else if (line.contains("</EXPERIMENT>")) {
					break;
				} else {
					throw new Exception("Unhandled Line: " + line);
				}
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage() + ": " + m_sExpFileName);
		} catch (Exception e) {
			throw new Exception("Configuration File Parsing Failure (line:" + 
					Integer.toString(lc) + "): \n" + e.getMessage());
		}

		dmsg(5, "Experiment Init Successful!");
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

		m_InstructionsHandler.adjustLayout();
		//m_ExperimentHandler.adjustLayout();
		//m_StudyHandler.adjustLayout();

		/* Ensure adjustLayout gets called next time! */
		m_dOldDims = (Dimension) this.getSize().clone();
	}
	
	public boolean handleCompoundProperty(CmeState state, String property, CmeStringHandler handler) {
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
		
		for(int x=0; x<str.size(); x++) {
			ret = (ret && handler.handleString(str.get(x)));
		}
		
		return ret;
	}
	
	private boolean isExcluded(String exc) {
		final String fexc = exc;
		boolean ret = 
			handleCompoundProperty(m_CurState, "Exclude", new CmeStringHandler() {
				public boolean handleString(String regex) {
					regex = m_CurState.translateString(regex);
					System.err.println(regex);
					return fexc.matches(regex);
				}
			});
		ret =  
			ret || handleCompoundProperty(null, "Exclude", new CmeStringHandler() {
				public boolean handleString(String regex) {
					regex = translateString(regex);
					System.err.println(regex);
					return fexc.matches(regex);
				}
			});
		return ret;
	}

	public String translateString(String text) {
		String ret = CmeApp.translateString(m_eProperties, text, true);
		return CmeApp.translateString(m_fbHashmap, ret, true);
	}
	
	public static String translateString(HashMap<String, Object> Properties, String text, boolean ignoreUndefined) {
		text = text.replace("$$", "$ ");

		Vector<String> propList = getVariableList(text);
		Comparator<String> comparator = Collections.reverseOrder();
		Collections.sort(propList, comparator);

		Iterator<String> viter = propList.iterator();
		while (viter.hasNext()) {
			String variable = (String) viter.next();
			
			Object value = Properties.get(variable);
			if (value == null) {
				if (!ignoreUndefined) {
					text = text.replace("$" + variable, "");
					text = text.replace("${" + variable + "}", "");
				}
				continue;
			}
			
			System.out.println("Strings: " + variable);
			
			if (value instanceof String) {
				text = text.replace("$" + variable, (String)value);
				text = text.replace("${" + variable + "}", (String)value);
			} else {
				Vector<String> strArray = (Vector<String>)value;
				for (int x=0; x<strArray.size(); x++) {
					String str = strArray.get(x);
					text = text.replace("$" + variable + "[" + Integer.toString(x) + "]", strArray.get(x));
					text = text.replace("${" + variable + "}[" + Integer.toString(x) + "]", strArray.get(x));
				}
				
				text = text.replaceAll("\\$" + variable + "\\[[0-9]+\\]", "index out of bounds");
				text = text.replaceAll("\\${" + variable + "}\\[[0-9]+\\]", "index out of bounds");
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
				propertyList.add(text.substring(lastOccurance + 1, endCharacter));
			}

			if (endCharacter >= text.length() || text.charAt(endCharacter) == '$') {
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
		set = ":" + set.replace(" ", "").replace("\t","").replace(",",":").replace("|",":").toLowerCase() + ":";
		String expCondition = (String) m_eProperties.get("ExpCondition");
		if (expCondition == null)
			return false;
		return set.contains(":" + expCondition.toLowerCase() + ":");
	}
	
	/** 
	 * Validates that a given condition is valid.
	 * 
	 * @param condition - the condition to be validated
	 * @return true if condition is valid, else false;
	 * 
	 * @see property ValidConditions is populated with a string of the form: 
	 * 		":cond1:cond2:cond3:"
	 */
	public boolean validateCondition(String condition) {
		String validConditions = m_eProperties.get("ValidConditions").toString();
		if (validConditions == null) {
			return false;
		}
		return validConditions.contains(":" + condition + ":");
	}

	/** 
	 * Set the condition property for the current experiment run.
	 *  
	 * @param condition - condition to be set
	 * @return true if condition is valid, else false  
	 */
	public boolean setCondition(String condition) {
		if (validateCondition(condition)) {
			System.out.println("Set Condition!");
			m_eProperties.put("ExpCondition", condition);
			return true;
		}
		return false;
	}

	//--------------------------------------------------------------------
	/** Post a message to the report file
	 *  @param msg - String to write out to file
	 *  @param giveTime - Boolean indicating if time should be output also
	 */
	//--------------------------------------------------------------------
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
			if(!conditionInSet(text.substring(0, col))) {
				System.out.println("Condition invalid!" + text.substring(0, col));
				return null;
			}
			prompt = col+1;
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
			System.out.println("No State!");
			return true;
		}
		
		int opt = getJOptionFromString(buttons);
		int ret = 0;
		
		Object property = m_CurState.getProperty(post);
		if (property == null) {
			System.out.println("No Property (" + post + ")!");
			return true;
		}
		
		if (property instanceof String) {
			String text = translatePrompt((String)property);
			
			if (text == null)
				return true;
			
			if (opt == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(this,
				text, "Info", opt);
			} else
				ret = JOptionPane.showConfirmDialog(this,
					text, "Info", opt);
		} else {
			Vector<String> texts = (Vector<String>) property;
			for (int x=0; x < texts.size(); x++) {
				String text = translatePrompt(texts.get(x));
				if (text == null)
					continue;
			
				if (opt == JOptionPane.NO_OPTION) {
					JOptionPane.showMessageDialog(this,
						text, "Info", opt);
				} else
					ret = JOptionPane.showConfirmDialog(this,
						text, "Info", opt);
			}
		}
		
		switch(opt) {
			case JOptionPane.OK_CANCEL_OPTION:
				return (ret == JOptionPane.OK_OPTION);
			case JOptionPane.YES_NO_OPTION:
				return (ret == JOptionPane.YES_OPTION);
		}
		
		return true;
	}

	public void skipInvalidConditions()
	{
		String sCondition = ((String)m_eProperties.get("ExpCondition"));
		
		while (m_CurState != null) {
			String stateConditions = (String) m_CurState.getProperty("ValidConditions");
				
			if (stateConditions == null)
				break;
				
			if (stateConditions.contains(":" + sCondition + ":"))
				break;
				
			dmsg(5, "Skipping State: Condition: :" + sCondition + ":, Condition Expected: " + stateConditions);
			m_CurState = (CmeState) m_cIterator.next();	
		}
	}
	
	//-----------------------------------------------
	/** Set the next state */
	//-----------------------------------------------
	public void setNextState() {
		if (m_cIterator == null) {
			m_cIterator = m_vStates.iterator();
		} else if (!m_InstructionsHandler.testAndSaveFeedback()) {
			String obj = m_InstructionsHandler.getObjective();
			
			if (obj == null)
				return;
			
			JOptionPane.showMessageDialog(this, obj, "Input Error!", JOptionPane.ERROR_MESSAGE);
			return;
		} else if (!m_InstructionsHandler.allowNextState())
			return;
		
		

		displayPrompt("PostStatePromptText");
		if (m_cIterator.hasNext()) {
			
			if (m_CurState != null)
				m_CurState.clean();
			m_CurState = (CmeState) m_cIterator.next();
			
			skipInvalidConditions();
			
			displayPrompt("PreStatePromptText");
		} else {
			System.exit(0);
		}

		dmsg(5, "Next State!");

		try {
			m_InstructionsHandler.setState(m_CurState);
			//m_StudyHandler.setState(m_CurState);
			//m_ExperimentHandler.setState(m_CurState);
			m_CurState.init();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "FATAL Error:\n" + ex.toString()
					+ "\n" + ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	//----------------------------------------------------------
	/** Convert a time in milliseconds to a string giving
	 *  the time since the start of the experiment
	 */
	//----------------------------------------------------------
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
		int debug = 0x100;
		String expFile = "Instructions/Experiment.txt";
		for (int x = 0; x < len; x++) {
			if (args[x].equals("-d") || args[x].equals("--debug")) {
				try {
					debug = Integer.parseInt(args[x + 1]);
				} catch (Exception ex) {
					debug = 0;
				}
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
		addFeedback(response.getName(), response.getValue());
	}

	public void addFeedback(String name, String value) throws Exception {
		m_fbHashmap.put(name, value);		
		if (isExcluded(name)) {
			System.out.println("excluded? " + name);
			return;
		}
		m_fbHashmap.put(name + "Time", Double.toString(((double)m_iTickCounter)/1000.0));
		for (int x=0;x<m_fbVectString.size();x++) {
			if (m_fbVectString.get(x).equals(name)) {
				m_fbVectString.remove(x);
				break;
			}
		}
		m_fbVectString.add(name);
		/*dmsg(0xFF, "Current Feedback(nv): " + m_fbHashmap.toString());*/
	}
	
	public String getFeedback(String name) {
		dmsg(0xFF, "getting feedback: " + name);
		return (String)m_fbHashmap.get(name);
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

			
		postStatusMessage("----------------Start Formatted Output-----------------\n\n", false);
		// Read the text strings and add them to the text area
		try {
			bufReader = new BufferedReader(instFile);
			while ((line = bufReader.readLine()) != null) {
				line = CmeApp.translateString(m_fbHashmap, line, true);
				line = CmeApp.translateString(m_eProperties, line, false);
				postStatusMessage(line, false);
			}
		} catch (Exception ex) {
			System.out.println("Failed to parse the OutputFormat file!");
			return;
		}	
		postStatusMessage("-----------------End Formatted Output------------------\n\n", false);
	}
	
	public void printResults() throws Exception {
		Calendar expCalendar = Calendar.getInstance();

		int day = expCalendar.get(Calendar.DAY_OF_MONTH);
		int month = expCalendar.get(Calendar.MONTH) + 1; // Calendar months are numbered from 0 
		int year = expCalendar.get(Calendar.YEAR);

		String fileName = new String(
				m_eProperties.get("StudyName").toString() + "_"
				+ m_eProperties.get("ExpCondition").toString() + "_SID"
				+ m_eProperties.get("SubjectID").toString() + "_"
				+ ((day < 10) ? ("0" + String.valueOf(day)) : String.valueOf(day))
				+ ((month < 10) ? ("0" + String.valueOf(month)) : String.valueOf(month))
				+ year + ".txt");

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
			for (int x=0; x<m_fbVectString.size();x++) {
				String name = m_fbVectString.get(x);
				String value = (String) m_fbHashmap.get(name);
				String time = (String) m_fbHashmap.get(name + "Time");
				line = time + "," + name + "," + value;
				postStatusMessage(line, false);
			}
		} catch (Exception e) {
			throw new Exception("Unable to write to report file: " + e.getMessage());
		}
	}
}
