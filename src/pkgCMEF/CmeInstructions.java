package pkgCMEF;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

//====================================================================
/**
 * CmeInstructions
 * <P>
 * Purpose: This panel displays a JTextArea object for display instructions.
 * 
 * @author Terry Meacham
 * @version 2.0 Date: May, 2011
 */
// ===================================================================
@SuppressWarnings("serial")
public class CmeInstructions extends JPanel {

	/** Parent frame */
	private CmeApp m_App;
	/** Current state */
	private CmeState m_CurState;
	/** Html Pane for Instructions */
	private CmeHtmlView m_HtmlView;
	/** Scroll Pane for Instruction Scrolling */
	private JScrollPane m_ScrollPane;
	/** JButton for primary button */
	private JButton m_bNext;
	/** JButton for primary button */
	private JButton m_bRefresh;
	/** Reference to the image factory */
	private CmePairFactory m_PairFactory;
	/** Submit Action Response */
	private ActionListener m_SubmitListener;
	
	private CmeClock m_cClock;
	
	private CmeEventResponse m_LimitResponse;
	
	
	private String m_bString;
	private String m_sContent;
	private String m_sStudyContent;
	private boolean m_bInStudyState;
	private int m_iStudyCount;
	
	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	public CmeInstructions(CmeApp parent, int ops) throws Exception {
		m_App = parent;
		
		final CmeInstructions cInst = this;
		
		clearStudyState();

		this.setVisible(false);
		this.setSize(parent.getSize());
		this.setBorder(null);

		this.setLayout(null);

		AncestorListener aListener = new AncestorListener() {

			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}

			@Override
			public void ancestorAdded(AncestorEvent arg0) {
			}

			@Override
			public void ancestorMoved(AncestorEvent e) {
				adjustLayout();
			}
		};
		
		HyperlinkListener hListener = new HyperlinkListener() {		
			CmeInstructions inst = cInst;
			public void hyperlinkUpdate(HyperlinkEvent e) {
				String[] desc = e.getDescription().toLowerCase().split(":");
				if (desc.length != 2)
					return;
				if (desc[0].equals("study")) {
					try {
						inst.setStudyState(desc[1]);
					} catch (Exception ex) {
						System.out.println("Failed to set study state!!\n" + desc[1]);
					}
				}
			}
		};

		/** Configure HTML View Pane */
		m_HtmlView = new CmeHtmlView(m_App, hListener);
		/*
		 * m_HtmlView.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED));
		 */
		
		m_SubmitListener = new ActionListener() {
			CmeInstructions inst = cInst;
			public void actionPerformed(ActionEvent e) {
				inst.primaryClickEvent();
			}
		};
		
		ActionListener refreshListener = new ActionListener() {
			CmeInstructions inst = cInst;
			public void actionPerformed(ActionEvent e) {
				try {
					inst.refreshView();
				} catch (Exception ex) {
					System.out.println("Failed to catch refresh exception!");
				}
			}
		};
		
		/** Configure Next JButton */
		m_bNext = new JButton();
		m_bNext.setText("Next");
		m_bNext.setVisible(true);
		m_bNext.addActionListener(m_SubmitListener);
		this.add(m_bNext);
		
		if ((ops & CmeApp.CME_ENABLE_REFRESH) == CmeApp.CME_ENABLE_REFRESH) {
			/** Configure Next JButton */
			m_bRefresh = new JButton();
			m_bRefresh.setText("Refresh");
			m_bRefresh.setVisible(true);
			m_bRefresh.addActionListener(refreshListener);
			this.add(m_bRefresh);
		}
		
		m_LimitResponse = new CmeEventResponse() {
			CmeInstructions inst = cInst;
			public void Respond() {
				inst.clockLimitHit();
			}
			
		};
		
		m_cClock = new CmeClock();
		m_cClock.setEventResponse(m_LimitResponse);
		m_cClock.setVisible(false);
		this.add(m_cClock);

		m_ScrollPane = new JScrollPane(m_HtmlView);
		m_ScrollPane.setBackground(Color.white);
		m_ScrollPane.addAncestorListener(aListener);
		this.add(m_ScrollPane);

	}
	
	private void primaryClickEvent() {
		if (m_bInStudyState) {
			try {
				clearStudyState();
			} catch (Exception ex) {
				System.out.println("Failed to clear study state!");
				System.exit(1);
			}
		} else if (m_CurState != null) {
			m_CurState.TriggerEvent(CmeState.EVENT_CLICK_PRIMARY);
		}
	}
	
	private void clockLimitHit() {
		try {
			clearStudyState();
		} catch (Exception ex) {
			System.out.println(ex.getMessage() + ": Failed to propertly clear Study State.");
			System.exit(1);
		}
	}
	
	public void clearStudyState() throws Exception {
		if (m_bInStudyState) {
			String element = m_cClock.getElement();
			int elapsed = m_cClock.stop();
			
			String postStudyColor = (String)m_CurState.getProperty("PostStudyColor");
			if (postStudyColor != null) {
				String pair = (String)m_CurState.getProperty("Pair" + element + "Xref");
				System.out.println("Pair" + pair + "Color");
				m_CurState.setProperty("Pair" + pair + "Color", postStudyColor);
			}
			
			String content = m_CurState.translateString(m_sContent);
			m_HtmlView.setContent(content, null);
			String trial = (String) m_CurState.getProperty("CurrentTrial");
			
			if (trial == null)
				trial = "";
			else
				trial = "_T" + trial;
			
			String name = "Study" + trial + "_" + element;
			String value = Double.toString(((double)elapsed)/1000.0);
			
			String fbval = m_App.getFeedback(name);
			if (fbval == null)
				fbval = "";
			else
				fbval += ",";
			fbval += Integer.toString(m_iStudyCount) + "," + value;
			m_App.addFeedback(name, fbval);
			
			m_iStudyCount++;
		}
		
		m_bInStudyState = false;
		updateButtonText();
	}
	
	public void setStudyState(String set) throws Exception {
		if (!m_CurState.canStudy())
			return;
		
		m_CurState.setProperty("CurrentPairA", m_CurState.getProperty("Pair" + set + "A"));
		m_CurState.setProperty("CurrentPairB", m_CurState.getProperty("Pair" + set + "B"));
		m_CurState.setProperty("CurrentPair", m_CurState.getProperty("Pair" + set));
		
		m_CurState.setProperty("CurrentPairAFile", m_CurState.getProperty("Pair" + set + "AFile"));
		m_CurState.setProperty("CurrentPairBFile", m_CurState.getProperty("Pair" + set + "BFile"));
		
		m_CurState.setProperty("CurrentGroup", m_CurState.getProperty("Pair" + set + "Group"));	
		m_CurState.setProperty("CurrentValue", m_CurState.getProperty("Pair" + set + "Value"));	
		m_CurState.setProperty("CurrentOrder", m_CurState.getProperty("Pair" + set + "Order"));
		
		String translation = m_CurState.translateString(m_sStudyContent);
		translation = m_App.translateString(translation);
		
		if (!m_cClock.start((String)m_CurState.getProperty("Pair" + set))) {
			JOptionPane.showMessageDialog(this, "Please click '" + m_bString + 
					"', your time has expired.", "Click " + m_bString + 
					" to Proceed", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		m_bNext.setText("Close");
		
		m_HtmlView.setContent(translation, null);
		m_bInStudyState = true;
	}
	
	/** Return the component string for the 'PageObjective' component! */
	public String getObjective() {
		CmeComponent comp = m_HtmlView.getComponentByName("PageObjective");
		if (comp == null)
			return null;
		return comp.getValue();
	}
	
	/** Set a reference to the image factory */
	public void setPairFactory(CmePairFactory iFact) {
		m_PairFactory = iFact;
	}

	/**
	 * Adjust all component present for the current window size.
	 * */
	public void adjustLayout() {
		adjustHtmlView();
		adjustNextButton();
		if (m_bRefresh != null)
			adjustRefreshButton();
		adjustClock();
	}
	
	public void refreshView() throws Exception {
		m_HtmlView.refreshView();
	}

	/**
	 * Adjust the Next Button for the current window size.
	 */
	private void adjustNextButton() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = new Dimension(128, 24);
		Point location = new Point(newSz.width, newSz.height);

		location.x -= border.width + dimensions.width;
		location.y -= border.height + dimensions.height;

		m_bNext.setSize(dimensions);
		m_bNext.setLocation(location);
	}
	
	/**
	 * Adjust the Refresh Button for the current window size.
	 */
	private void adjustRefreshButton() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = new Dimension(128, 24);
		Point location = new Point(0, newSz.height);

		location.x += border.width;
		location.y -= border.height + dimensions.height;

		m_bRefresh.setSize(dimensions);
		m_bRefresh.setLocation(location);
	}

	/**
	 * Adjust the HTML View for the current window size.
	 */
	private void adjustHtmlView() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = (Dimension) newSz.clone();
		Point location = new Point(border.width, border.height);

		int lower_offset = border.height + 24;

		if (m_bNext.isVisible()) {
			dimensions.height = newSz.height - (border.height * 2 + lower_offset);
		} else {
			dimensions.height = newSz.height - (border.height * 2);
		}
		dimensions.width -= border.width * 2;

		m_HtmlView.setSize(dimensions);
		m_ScrollPane.setSize(dimensions);
		m_ScrollPane.setLocation(location);
	}
	
	/**
	 * Adjust the Next Button for the current window size.
	 */
	private void adjustClock() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = new Dimension(64, 32);
		Point location = new Point(newSz.width/2-dimensions.width/2+border.width, newSz.height);

		location.x -= border.width;
		location.y -= border.height + dimensions.height;

		m_cClock.setSize(dimensions);
		m_cClock.setLocation(location);
	}

	/**
	 * Used to determine if the sequential are complete.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean isDoneSequential() throws Exception {
		if (m_CurState.getState() != CmeState.STATE_SEQUENTIAL) {
			throw new Exception("Tested if a rating was done when NOT in a rating step!");
		}
		
		CmeIterator iterator = m_CurState.getIterator();
		if (iterator != null && iterator.isComplete())
			return true;

		int istep = m_CurState.getSequentialStep();
		int ismax = m_CurState.getSequentialStepMax();

		return (istep >= ismax);
	}
	
	public boolean allowNextState() {
		System.out.println("Clock check...");
		
		if (m_cClock != null && !m_cClock.isComplete()) {
			System.out.println("Display Limit Prompt");
			return (m_App.displayPrompt("LimitPromptText", "LimitPromptButtons"));
		}
		
		return true;
	}

	/**
	 * Used to set the next rating environment.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean setNextInSequence() throws Exception {
		String instructionFile = null;
 
		int cstep = m_CurState.getSequentialStep();
		
		if (cstep > 0) {
			if (!testAndSaveFeedback()) {
				return false;
			}
		
			m_HtmlView.setVisible(false);
			m_App.displayPrompt("PostPromptText");
			
			if (isDoneSequential()) {
				m_HtmlView.clearContent();
				return false;
			}
		}

		m_CurState.setSequentialStep(cstep + 1);
		CmeIterator iterator = m_CurState.getIterator();

		if (iterator == null) {
			throw new Exception("Failed to retreive iterator from current state (setNextSequential)!");
		}
		
		m_App.displayPrompt("PrePromptText");
		
		setSequentialProperties(cstep, iterator.getNext());

		instructionFile = (String) m_CurState.getProperty("InstructionFile");
		if (instructionFile != null) {
			this.showProcessedInstructions((String) instructionFile);
		} else {
			throw new Exception("Failed to set instruction file! (InstructionFile == null)");
		}

		m_CurState.resetSeqState();
		
		m_HtmlView.requestFirstFocus();

		return true;
	}

	private void setSequentialProperties(int step, int nextStep) throws Exception {
		if (m_CurState == null) {
			return;
		}

		double scale = 0;
		String sscale = (String) m_CurState.getProperty("Scale");

		if (sscale == null || sscale.equals("")) {
			scale = 1.0;
		} else {
			try {
				scale = Double.parseDouble(sscale);
			} catch (Exception ex) {
				System.out.println("Ooops, no scale defined! Using 100.0.");
				scale = 1.0;
				ex.printStackTrace();
			}
		}
			
		m_CurState.setProperty("CurrentPairA", m_PairFactory.getFeedbackA(nextStep, (int) (scale * 1000)));
		m_CurState.setProperty("CurrentPairB", m_PairFactory.getFeedbackB(nextStep, (int) (scale * 1000)));
		m_CurState.setProperty("CurrentPairAFile", m_PairFactory.getFileA(nextStep));
		m_CurState.setProperty("CurrentPairBFile", m_PairFactory.getFileB(nextStep));
		m_CurState.setProperty("CurrentPair", Integer.toString(nextStep));
		
		m_CurState.setProperty("CurrentGroup", m_PairFactory.getPairGroup(nextStep));	
		m_CurState.setProperty("CurrentValue", m_PairFactory.getPairValue(nextStep));	
		m_CurState.setProperty("CurrentOrder", Integer.toString(step));
	}

	/**
	 * Configure the view for Simultaneous presentation.
	 * @throws Exception 
	 */
	private void setSimultaneous() throws Exception {
		Object instructionFile = m_CurState.getProperty("InstructionFile");
		assert (instructionFile != null);

		int icount = m_CurState.getIntProperty("Count");
		System.out.print("Count: ");
		System.out.println(icount);

		setSimultaneousProperties(icount);
		showProcessedInstructions((String) instructionFile);
	}

	/**
	 * Set the simultaneous display properties for this state.
	 * 
	 * @param count - number of items to be displayed at once.
	 * @throws Exception 
	 */
	private void setSimultaneousProperties(int count) throws Exception {
		if (m_CurState == null) {
			return;
		}

		double scale = 0;
		CmeIterator iter = m_CurState.getIterator();
		String sscale = (String) m_CurState.getProperty("Scale");

		assert (sscale != null);

		try {
			scale = Double.parseDouble(sscale);
		} catch (Exception ex) {
			System.out.println("Ooops, no scale defined! Using 100.0.");
			scale = 1.0;
			ex.printStackTrace();
		}
		
		String preStudyColor = (String) m_CurState.getProperty("PreStudyColor");

		for (int x = 0; x < count; x++) {
			int step = iter.getNext();
			String vx = Integer.toString(x + 1);

			m_CurState.setProperty("Pair" + vx + "A", m_PairFactory.getFeedbackA(step, (int) (scale * 1000)));
			m_CurState.setProperty("Pair" + vx + "B", m_PairFactory.getFeedbackB(step, (int) (scale * 1000)));
			m_CurState.setProperty("Pair" + vx + "AFile", m_PairFactory.getFileA(step));
			m_CurState.setProperty("Pair" + vx + "BFile", m_PairFactory.getFileB(step));
			m_CurState.setProperty("Pair" + vx, Integer.toString(step));
			
			m_CurState.setProperty("Pair" + vx + "Group", m_PairFactory.getPairGroup(step));	
			m_CurState.setProperty("Pair" + vx + "Order", Integer.toString(x));	
			m_CurState.setProperty("Pair" + vx + "Value", m_PairFactory.getPairValue(step));
			m_CurState.setProperty("Pair" + step + "Xref", vx);
			
			if (m_CurState.canStudy() && preStudyColor != null) {
				m_CurState.setProperty("Pair" + vx + "Color", preStudyColor);
			}
		}
	}

	/**
	 * Show the processed instructions with the current selected image.
	 * 
	 * @param fileName - name of the instruction file to show.
	 * 
	 * @throws IOException
	 */
	private void showProcessedInstructions(String fileName) throws Exception {
		m_sContent = CmeApp.readFile(fileName);

		this.adjustLayout();
		
		String content;
		
		/*Application properties are global and shouldn't change...*/
		m_sContent = m_App.translateString(m_sContent);
		content = m_CurState.translateString(m_sContent);
		
		ActionListener listener = m_SubmitListener;
		
		m_App.dmsg(10, "Setting Contents");
		if (m_CurState.getProperty("DisableSubmitHook") != null)
			listener = null;
		
		m_HtmlView.setContent(content, listener);
	}
	
	private void updateClock() {
		if (!m_CurState.canStudy()) {
			m_cClock.setVisible(false);
			return;
		}
		
		String displayClock = (String)m_CurState.getProperty("DisplayTimer");
		if (displayClock == null) {
			m_cClock.setVisible(false);
		} else {
			if (displayClock.equals("down")) 
				m_cClock.setCountDown(true);
			else
				m_cClock.setCountDown(false);
			m_cClock.setVisible(true);
		}
		
		int timeLimit = m_CurState.getIntProperty("Limit");
		int timeRes = m_CurState.getIntProperty("Resolution");
		if (timeLimit <= 0) {
			System.out.println("Failed to properly retrieve limit.");
			System.exit(1);
		}
		if (timeRes < 0)
			timeRes = 1;
		
		m_cClock.setTimeLimit(timeLimit);
		m_cClock.setResolution(timeRes);
		m_cClock.reset();
	}

	private void updateButtonText() {
		if (m_bNext == null || m_CurState == null)
			return;
		
		if (m_bRefresh != null)
			m_bRefresh.setVisible(true);
		
		if (m_bInStudyState) {
			m_bNext.setText("Close");
			m_bNext.setVisible(true);
			} else if (m_CurState.getEventResponseCount(CmeState.EVENT_CLICK_PRIMARY) == 0) {
			m_bNext.setVisible(false);
			if (m_bRefresh != null)
				m_bRefresh.setVisible(false);
		} else {
			String bString = (String)m_CurState.getProperty("PrimaryButtonText");
			if (bString != null) {
				m_bString = bString;
				m_bNext.setText(m_bString);
			} else {
				m_bString = "Next";
			}
			m_bNext.setVisible(true);
		}
	}
	
	private void updateStudyContent() throws IOException {
		m_iStudyCount = 0;
		Object pbStudyFile = m_CurState.getProperty("StudyFile");
		
		if (pbStudyFile != null) {
			m_sStudyContent = CmeApp.readFile(pbStudyFile.toString());
			System.out.println("Study File: " + pbStudyFile.toString());
		} else {
			System.out.println("No Study File Set!");
		}
	}
	/** 
	 * Blank the instruction screen.
	 */
	public void blankInstructions() {
		m_HtmlView.setVisible(false);
	}

	/**
	 * Show the input prompt.
	 * 
	 * @param msg
	 *            - the message to display to the user
	 * @return true if successful; false else
	 * @throws Exception
	 */
	private boolean showInputPrompt(String msg) throws Exception {
		String input;

		if (msg == null) {
			throw new Exception("Invalid prompt message specified!");
		}

		msg = m_CurState.translateString(msg);
		msg = m_App.translateString(msg);

		do {
			input = JOptionPane.showInputDialog(msg);
			if (input == null) {
				return false;
			}
		} while (!m_CurState.validateInput(input));

		String name = (String) m_CurState.getProperty("FeedbackName");
		if (name == null) {
			throw new Exception("Feedback name is null!");
		}

		m_App.addFeedback(name, input);

		return true;
	}

	/**
	 * Paint function for the Instruction
	 * 
	 * @param g - graphics context for the current JPane
	 */
	public void paint(Graphics g) {
		super.paint(g);
		// Paint the area
	}

	/**
	 * Sets the current state and adjusts the layout appropriately
	 * 
	 * @param mCurState
	 *            - the current state to be displayed
	 * @throws Exception
	 */
	public void setState(CmeState mCurState) throws Exception {
		Object instructionFile = null;
		m_CurState = mCurState;

		updateClock();
		updateButtonText();
		updateStudyContent();
		
		// Begin!
		try {
			switch (m_CurState.getState()) {
				case CmeState.STATE_FEEDBACK:
				case CmeState.STATE_INSTRUCTION:
					instructionFile = m_CurState.getProperty("InstructionFile");
					assert (instructionFile != null);

					showProcessedInstructions((String) instructionFile);
					break;

				case CmeState.STATE_SEQUENTIAL:
					m_CurState.setSequentialStep(0);
					m_CurState.setSequentialStepMax(m_PairFactory.getCount());
					/** This calls showProcessedInstructions... */
					setNextInSequence();
					break;

				case CmeState.STATE_SIMULTANEOUS:
					setSimultaneous();
					break;

				case CmeState.STATE_PROMPT:
					this.setVisible(false);
					showInputPrompt((String) m_CurState.getProperty("PromptText"));
					if (testAndSaveFeedback())
						m_App.setNextState();
					return;

				default:
					throw new Exception("Default state handler hit!");
			}
		} catch (IOException ex) {
			throw new Exception("IO Error: " + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Failed to set Instruction State: "
					+ ex.getMessage());
		}
		
		this.setVisible(true);
		
		m_HtmlView.requestFirstFocus();
	}

	public boolean testAndSaveFeedback() {
		boolean valid = false;
		try {
			Iterator<CmeResponse> iter = m_HtmlView.getResponseIterator();

			valid = m_CurState.validateInput(iter);
			if (!valid) {
				System.out.println("Invalid input!");
				return false;
			}

			valid = generateFeedback();
		} catch (Exception ex) {
			System.out.println(
					"Failed to generate output for feedback:\n" + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		return valid;
	}
	
	/**
	 * Generate all feedback from the current component set.
	 * @return true if feedback was generate; false else
	 * @throws Exception
	 */
	private boolean generateFeedback() throws Exception {
		Iterator<CmeResponse> iter = m_HtmlView.getResponseIterator();
		while (iter != null && iter.hasNext()) {
			m_App.addFeedback(iter.next());
		}
		return true;
	}
}