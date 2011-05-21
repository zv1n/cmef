package pkgCMEF;

import java.awt.Color;
import java.awt.Dialog;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

//====================================================================
/** Custom Memory Experament Framework
 *  <P>Purpose: This application was designed and written for use
 *  as the primary experimental software for an experiment in 
 *  learning conducted by Jodi Price.
 *  @author Terry Meacham, Dr. Rick Coleman
 *  @version 2.0
 *  Date: May 2011
 */
//===================================================================
public class CmeApp extends JFrame
{
	/** Main panel */
	private JPanel m_MainPanel;
	
	/** Instructions Handler */
	private CmeInstructions m_InstructionsHandler;
	
	/** Experiment Handler */
	private CmeExperiment m_ExperimentHandler;

	/** Start time in milliseconds */
	private long m_lStartTimeMillis;
	
	/** Study Name from Config Files */
	private String m_sStudyName;
	
	/** Name of the experiment definition file */
	private String m_sExpFileName = "Instructions/Experiment.txt";
	
	/** Vector of states defining the experiment */
	private Vector m_vExpStates;
	
	/** Index of the current state */
	private int m_iCurStateIdx;
	
	/** Reference to the current state */
	private CmeState m_CurState;
	
	/** Reference to the image factory */
	private CmeImageFactory m_ImageFactory;
	
	/** ID of this subject */
	private String m_sSubjectID;

	/** Experiment Condition */
	private String m_sCondition;

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
	
	//-----------------------------------------------
	// Init function for prepping all State handlers
	// (CmeExperiment,CmeInstructions,CmeStudy)
	//-----------------------------------------------
	private void initStateHandlers() {

		// Create the image factory
		m_ImageFactory = new CmeImageFactory();
		m_InstructionsHandler.setImageFactory(m_ImageFactory);
		m_ExperimentHandler.setImageFactory(m_ImageFactory);
		
	}
	
	//-----------------------------------------------
	// Default constructor
	//-----------------------------------------------
	public CmeApp(int debugLevel)
	{
		
		this.setSize(1024, 768);
		this.setLocation(50, 50);
		this.setTitle("CME Main Panel");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		initStateHandlers();
		
		
		// Create the main panel
		m_MainPanel = new JPanel();
		m_MainPanel.setSize(1024, 768);
		m_MainPanel.setLocation(0, 0);
		m_MainPanel.setLayout(null);
		m_MainPanel.setBackground(Color.LIGHT_GRAY);
		m_MainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.getContentPane().add(m_MainPanel);
		
		// Create and show the instructions panel
		m_InstructionsHandler = new CmeInstructions(1010, 686, this);
		m_InstructionsHandler.setLocation(-1, -1);
		m_MainPanel.add(m_InstructionsHandler);
		
		// Create the image panel
		m_ExperimentHandler = new CmeExperiment(this, 1010, 728);
		m_ExperimentHandler.setLocation(-1, -1);
		m_MainPanel.add(m_ExperimentHandler);
		m_ExperimentHandler.setVisible(false);
/*		
		// Create and add the Continue button
		m_ContinueButton = new JButton("Continue");
		m_ContinueButton.setSize(100, 20);
		m_ContinueButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_ContinueButton.setLocation(462, 692);
		
		m_ContinueButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					m_CurState.TriggerEvent(CmeState.EVENT_CLICK_CONTINUE);
				}
			}
		);

		m_MainPanel.add(m_ContinueButton); */
		
		
		// Get the subject ID first
		m_sSubjectID = JOptionPane.showInputDialog("Please enter the subject's ID.");
		if (m_sSubjectID == null)
			System.exit(0);

		String sCondition;
		do {
			sCondition = JOptionPane.showInputDialog("Please enter the experimental Condition\n(This should be given by the experimenter).");
			if (sCondition == null) {
				System.exit(0);
			}
		} while (!setCondition(sCondition));
		
		// Set up the experiment states
		m_vExpStates = new Vector();
		if(!initExperiment())	// Oops! Couldn't read the file
			System.exit(0);
		
		m_iCurStateIdx = -1;
		m_CurState = null;
		
		// Show the window
		this.setVisible(true);
		
		// Set the initial state
		this.setNextState();
	}

	public boolean validateCondition(String condition)
	{
		return true;
	}
	
	//-----------------------------------------------
	/** Set the condition; return false 
	 * if the string is invalid 
	 */	
	//-----------------------------------------------
	public boolean setCondition(String condition)
	{
		m_sCondition = condition;
		return validateCondition(condition);
	}
	
	//-----------------------------------------------
	/** Set up to run the experiment */
	//-----------------------------------------------
	public boolean initExperiment()
	{
		final CmeApp thisApp = this;
		
		// Open the experiment file
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;
		CmeState 		thisState = null;
		boolean			recordState = false;
		Calendar		expCalendar;
		// Open the file
		try
		{
			instFile = new FileReader(m_sExpFileName);
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to open " + m_sExpFileName, 
					"Error Opening Experiment File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// Read the text strings and add them to the text area
		try
		{
			bufReader = new BufferedReader(instFile);
			while((line = bufReader.readLine()) != null)
			{
				// See if we need to create a new State
				if((line.contains("STATE")) && (!(line.contains("/STATE"))))
				{
					thisState = new CmeState();
				}
				// Check for options in "Show Instructions"
				else if(line.contains("Show Instructions"))
				{
					thisState.setState(CmeState.STATE_INSTRUCTION);
				}
				else if(line.contains("FILE"))
				{
					String insFile = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
					thisState.setProperty("InstructionFile", insFile);
				}
				else if(line.contains("CONDITIONS"))
				{
					String valConditions = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
					thisState.setProperty("ValidConditions", valConditions);
				}
				else if(line.contains("END"))
				{
					if(line.contains("On Click Continue"))
					{
						thisState.setEventResponse(CmeState.EVENT_CLICK_CONTINUE,
							new CmeEventResponse() {
									public void Respond() {
										thisApp.setNextState();
									}
							});
					}
				}
								// Check for options in "Learning Phase"
				else if(line.contains("Learning Phase"))
				{
					thisState.setState(CmeState.STATE_LEARNING);
				}
				// Check for options in "Testing Phase"
				else if(line.contains("Testing Phase"))
				{
					thisState.setState(CmeState.STATE_TEST);
				}
				else if(line.contains("/STATE"))
				{
					// Add this one to the vector
					m_vExpStates.add(thisState);
				}
				else if((line.contains("STUDY_TIMES")) && (!line.contains("/")))
				{
					// Get the number of study times to write out to report
					line = bufReader.readLine().trim();
				}

			}
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to read " + m_sExpFileName, 
					"Error Reading Experiment File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
				
		// Create a unique report file name using the time
		// Note: Calendar months are numbered from 0 so add 1 to Calendar.MONTH
		expCalendar = Calendar.getInstance();
		int hr = expCalendar.get(Calendar.HOUR_OF_DAY);
		int min = expCalendar.get(Calendar.MINUTE);
		int sec = expCalendar.get(Calendar.SECOND);
		int day = expCalendar.get(Calendar.DAY_OF_MONTH);
		int month = expCalendar.get(Calendar.MONTH)+1; // Calendar months are numbered from 0 
		int year = expCalendar.get(Calendar.YEAR);
		
		// Create an image file name
		String fileName = new String(m_sStudyName + "_"
				+ m_sCondition + "_SID" + m_sSubjectID
                + "_" + ((day<10)?("0"+String.valueOf(day)):String.valueOf(day))
                + ((month<10)?("0"+String.valueOf(month)):String.valueOf(month))
                + year + ".txt");
		try
		{
			// Open the report file
			m_fileReport = new File(fileName);
			// Place it in the FileWriter
			m_fileWriter = new FileWriter(m_fileReport);
			// Place that in the BufferedWriter
			m_bufWriter = new BufferedWriter(m_fileWriter);
			// Get the experiment start time in milliseconds
			m_lStartTimeMillis = System.currentTimeMillis();
			
			// Save and write some initial time data
			// Create time string as HH:MM:SS
			/*m_sExpTimeStr = 
				((hr<10)?("0"+String.valueOf(hr)):String.valueOf(hr)) + ":" +
				((min<10)?("0"+String.valueOf(min)):String.valueOf(min)) + ":" +
				((sec<10)?("0"+String.valueOf(sec)):String.valueOf(sec));*/
			// Create date string as DOW Month Date Year "EEEE MMMM dd yyyy"
			SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL);
			sdf.applyPattern("EEEE MMMM dd yyyy");
			postStatusMessage("Subject Test Time: " + 
					expCalendar.getTime().toString() + "\n", false);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to write to report file.", 
					"Error Writing Report File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		// Record the setup for this experiment
		postStatusMessage("Setup for this run: ", false);
		postStatusMessage("\tDisplay arrangement for each trial will be top row (l to r), bottom row (l to r) ", false);
		Vector iVec = this.m_ImageFactory.getImagesVector();
		
		postStatusMessage(line, false);
		
		// Set to record experiment events
		postStatusMessage("--------------------------------------------------------------", false);
		return true;
	}
	
	//--------------------------------------------------------------------
	/** Post a message to the report file
	 *  @param msg - String to write out to file
	 *  @param giveTime - Boolean indicating if time should be output also
	 */
	//--------------------------------------------------------------------
	public void postStatusMessage(String msg, boolean giveTime)
	{
		String line;
		try
		{
			if(giveTime)
			{
				// Add time hack in front of string
				line = timeToString() + msg + "\n";
			}
			else
			{
				line = msg + "\n";
			}
			m_bufWriter.write(line, 0, line.length());
			m_bufWriter.flush();
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to write to report file.", 
					"Error Writing Report File", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	//-----------------------------------------------
	/** Set the next state */
	//-----------------------------------------------
	public void setNextState()
	{
		m_iCurStateIdx++;
		//TODO Move header prints to the initialization
		
		// write out all the data in comma separated format.
		if (m_CurState != null)
		{
			try
			{/*
				String line;
				// Post headings for first section
				line = "\n\nSubject ID,Condition,Run Date,Run Time,Total Exp Time," +
					   "Test 1 Pretrial,Test 1 Posttrial,Test 1 Postdict," +
				   "Test 2 Pretrial,Test 2 Posttrial,Test 2 Postdict";
				postStatusMessage(line, false);
				String estStr = "";
				// Build the estimates string

				for(int i=0; i<6; i++)
				{
					estStr = estStr.concat("," + String.valueOf(m_iUserEstimates[i]));
				}
				
				// Get the total experiment time
				line = 	m_sSubjectID + "," + 
						sCondition + "," +
						timeToString() + 
						estStr;
				postStatusMessage(line, false);
				// Post headings for second section
				line = "\nSID,Word number,English,EOL Order," +
					"EOL Rate,Test Order,User Answer,Correct(T/F),Answer Value,Trial,Set,Grid Row," +
					"Grid Column,Times Studied,Total Study Time";
				String stStr = "";
				for(int i=0; i<m_iMaxStudyTimes; i++)
				{
					stStr = stStr.concat(",Study Time " + String.valueOf(i+1) +
							",Study Order " + String.valueOf(i+1));
				}
				line = line.concat(stStr);
				postStatusMessage(line, false);
				// Write out all the information on this run
				// postStatusMessage for the column headings
				Vector vec = this.m_ImageFactory.getImagesVector();
				for(int i=0; i<36; i++) // Only do the exp images
				{
					postStatusMessage(m_sSubjectID + "," +
							String.valueOf(i) + "," +
							img.getReferenceName() + "," +
							String.valueOf(img.getEOLPresentationOrder()) + "," +
							String.valueOf(img.getEOLRate()) + "," +
							String.valueOf(img.getTestPresentationOrder()) + "," +
							img.getUserAnswer() + "," +
							String.valueOf(img.getUserAnswerCorrect()) + "," +
							String.valueOf(img.getImageValue()) + "," +
							String.valueOf(img.getTrial()) + "," +
							String.valueOf(img.getGrid()) + "," +
							String.valueOf(img.getRow()) + "," +
							String.valueOf(img.getColumn()) + "," +
							String.valueOf(img.getTimesStudied()) + "," +
							String.valueOf(img.getTotalStudyTime()) +
							studyStr
							, false);
				}
				postStatusMessage("\n\n", false);
				postStatusMessage(" - End of experiment.", true);
				m_bufWriter.close(); // Close the file
				*/
			}
			catch(Exception e)
			{}
			
		}

		if (m_iCurStateIdx >= m_vExpStates.size()) {
			// Terminate the experiment
			System.exit(0);
		}

		m_CurState = (CmeState)m_vExpStates.elementAt(m_iCurStateIdx);		
		
		switch(m_CurState.getState())
		{
			case CmeState.STATE_INSTRUCTION :
				postStatusMessage(" - Displaying instructions: " + m_CurState.getProperty("InstructionFile").toString(), true);
				m_InstructionsHandler.setVisible(false);
				m_InstructionsHandler.showInstructions(m_CurState.getProperty("InstructionFile").toString());
				m_InstructionsHandler.setVisible(true);
				break;
			case CmeState.STATE_LEARNING :
				m_InstructionsHandler.setVisible(false);
				m_ExperimentHandler.setVisible(true);
				paint(getGraphics());
				postStatusMessage(" - Begin study phase.", true);
			case CmeState.STATE_TEST :
				//set up panels
				m_InstructionsHandler.setVisible(false);
				m_ExperimentHandler.setVisible(true);
				paint(getGraphics());
				postStatusMessage(" - Begin testing phase.", true);
		}
	}

	
	//----------------------------------------------------------
	/** Convert a time in milliseconds to a string giving
	 *  the time since the start of the experiment
	 */
	//----------------------------------------------------------
	private String timeToString()
	{
		long currentTimeMillis = System.currentTimeMillis();
		double tDiffSec = (double)(currentTimeMillis - m_lStartTimeMillis) / 1000.0;
		double hours = tDiffSec / 3600.0;
		double minutes = (hours - Math.floor(hours)) * 60.0;
		double seconds = (minutes - Math.floor(minutes)) * 60.0;

		int h, m, s;
		h = (int)(Math.floor(hours));
		m = (int)(Math.floor(minutes));
		s = (int)(Math.round(seconds));

		String tStr = new String("");
		if(h < 10)
			tStr = tStr.concat("0" + String.valueOf(h) + ":");
		else
			tStr = tStr.concat(String.valueOf(h) + ":");
		if(m < 10)
			tStr = tStr.concat("0" + String.valueOf(m) + ":");
		else
			tStr = tStr.concat(String.valueOf(m) + ":");
		if(s < 10)
			tStr = tStr.concat("0" + String.valueOf(s));
		else
			tStr = tStr.concat(String.valueOf(s));

		return tStr;
	}
	
	//-----------------------------------------------
	/** Everything states in main */
	//-----------------------------------------------
	public static void main(String[] args)
	{
		int len = args.length;
		int debug = 0;
		for (int x = 0; x < len; x++) {
			if(args[x] == "-d" || args[x] == "--debug") {
				try {
					debug = Integer.parseInt(args[x+1]);
				}
				catch(Exception ex) {
					debug = 0;
				}
			}
		}
		
		CmeApp theApp = new CmeApp(debug);
	}

}
