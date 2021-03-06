package pkgCMEF;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;

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
public class CmeView extends JPanel {

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
	
	
	/*****************************************************************************
	/** Calibration Controls
	/****************************************************************************/
	
	/** Controls */
	private Vector<JButton> m_vjbCalibrationControl = new Vector<JButton>();
	/** Text */
	private JLabel m_lblCalText = null;
	private JLabel m_lblSliderText = null;
	/** Slider */
	private JSlider m_CalSlider = null;
	
	/** Clock to display the timer countdown to the user. */
	private CmeClock m_cClock;
	
	private CmeEventResponse m_LimitResponse;

  private Vector<Integer> m_viCurrentItems = new Vector<Integer>();
	
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
	public CmeView(CmeApp parent, int ops) throws Exception {
		m_App = parent;
		
		final CmeView cInst = this;
		
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
			CmeView inst = cInst;
			public void hyperlinkUpdate(HyperlinkEvent e) {
				String[] desc = e.getDescription().toLowerCase().split(":");
				if (desc.length != 2)
					return;
				if (desc[0].equals("study")) {
					try {
						inst.setStudyState(desc[1]);
					} catch (Exception ex) {
						//System.out.println("Failed to set study state!!\n" + desc[1]);
					}
				}
			}
		};

		boolean textOnly = ((ops & CmeApp.CME_TEXT_ONLY) == CmeApp.CME_TEXT_ONLY);
		
		/** Configure HTML View Pane */
		m_HtmlView = new CmeHtmlView(m_App, hListener, textOnly);
		/*
		 * m_HtmlView.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED));
		 */
		
		m_SubmitListener = new ActionListener() {
			CmeView inst = cInst;
			public void actionPerformed(ActionEvent e) {
				inst.primaryClickEvent();
			}
		};
		
		ActionListener refreshListener = new ActionListener() {
			CmeView inst = cInst;
			public void actionPerformed(ActionEvent e) {
				try {
					inst.refreshView();
				} catch (Exception ex) {
					//System.out.println("Failed to catch refresh exception!");
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
			CmeView inst = cInst;
			public void Respond() {
				inst.clockLimitHit();
			}
		};
		
		m_cClock = new CmeClock(m_App);
		m_cClock.setEventResponse(m_LimitResponse);
		m_cClock.setVisible(false);
		this.add(m_cClock);

		m_ScrollPane = new JScrollPane(m_HtmlView);
		m_ScrollPane.setBackground(Color.white);
		m_ScrollPane.addAncestorListener(aListener);
		this.add(m_ScrollPane);

	}

	private void primaryClickEvent() {
    m_App.dmsg(CmeApp.DEBUG_STATES, "Primary click event!");
		if (m_bInStudyState) {
			try {
				clearStudyState();
			} catch (Exception ex) {
				//System.out.println("Failed to clear study state!");
				System.exit(1);
			}
		} else if (m_CurState != null) {
			m_CurState.TriggerEvent(CmeState.EVENT_CLICK_PRIMARY);
		}
	}

	private void clockLimitHit() {
		try {
			clearStudyState();
			m_cClock.stop();
		} catch (Exception ex) {
			//System.out.println(ex.getMessage() + ": Failed to propertly clear Study State.");
			System.exit(1);
		}
	}

	public void updateHtmlContent() throws Exception {
		String content = m_CurState.translateString(m_sContent);
		content = m_App.translateString(content);
		m_HtmlView.setContent(content, null);
	}

	public void updateLinkColor(String element) {
		
		String postStudyColor = (String)m_CurState.getProperty("PostStudyColor");
		
		if (postStudyColor != null) {
			//System.out.println("Pair" + element + "Color");
      m_CurState.setProperty("Pair" + element + "Color", postStudyColor);
      int step = m_CurState.getIntProperty("Pair" + element);
      if (step >= 0)
        m_CurState.setProperty("Item" + step + "Color", postStudyColor);
		}
	}

	public boolean clearStudyState() throws Exception
	{
		boolean ret = false;
		
		if (m_bInStudyState) {
			String pairNum = m_cClock.getElement();
			int elapsed = 0;
			
			if (isContinuousTimer() && !m_cClock.isComplete())
				elapsed = m_cClock.getElapsedTime();
			else
				elapsed = m_cClock.stop();
			
			double elapsedTime = ((double)elapsed)/1000.0;

			/* Setup: trial variable. */
			String trial = (String) m_CurState.getProperty("CurrentTrial");
			if (trial == null)
				trial = "";
			else
				trial = "_T" + trial;
			
			/* Setup: pair being studied. */
			String pair = (String) m_CurState.getProperty("Pair" + pairNum);
			
			if (pair == null) 
				throw new Exception("Failed to get the current pair!");

			updateLinkColor(pairNum);

			updatePairSelection(trial, pair, pairNum, elapsedTime);
			updatePairTotals(trial, pair, elapsedTime);
			updatePairCount(trial, pair);
			
      if (m_CurState.getBooleanProperty("ShuffleItems", false))
        shuffleProperties();

      updateHtmlContent();

			m_iStudyCount++;
			ret = true;
		}
		
		m_bInStudyState = false;
		updateButtonText();
		
		return ret;
	}
	
	private void updatePairSelection(String trial, String pair, String pairNum, double elapsedTime) throws Exception
	{
		String name = "Study" + trial + "_" + pair;
		String value = Double.toString(elapsedTime);
		String fbval = m_App.getFeedback(name);
		
		if (fbval == null)
			fbval = "";
		else
			fbval += ",";

    if (m_CurState.getBooleanProperty("ShuffleItems", false))
      fbval += pairNum + ",";

		fbval += Integer.toString(m_iStudyCount) + "," + value;

		m_App.addFeedback(name, fbval);
	}
	
	private void updatePairCount(String trial, String pair) throws Exception
	{
		String name = "StudyTotalCount" + trial + "_" + pair;
		String value = m_App.getFeedback(name);

		if (value == null) {
			value ="1";
		} else try {
			int ival = Integer.valueOf(value);
			ival++;
			value = String.valueOf(ival);
		} catch (Exception ex) {
			System.err.println("Failed to properly set value!");
			throw new Exception("Failed to properly set total count value!");
		}

		//System.out.println("Count: " + name + ":" + value);	
		m_App.addFeedback(name, value);
	}
	
	private void updatePairTotals(String trial, String pair, double elapsedTime) throws Exception {
		String name = "StudyTotalTime" + trial + "_" + pair;
		String value = m_App.getFeedback(name);
	
		if (value == null) {
			value = Double.toString(elapsedTime);;
		} else try {
			double dval = Double.valueOf(value);
			dval += elapsedTime;
			value = String.valueOf(dval);
		} catch (Exception ex) {
			System.err.println("Failed to properly set value!");
			throw new Exception("Failed to properly set total time value!");
		}
	
		//System.out.println("Timer: " + name + ":" + value);
		m_App.addFeedback(name, value);
	}
	
	public void setStudyState(String set) throws Exception
	{
		if (!m_CurState.canStudy()) {
			throw new  Exception("Not a STUDY MODE!");
			//return;
		}

    System.err.println("SetStudyState: " + set.toString());
		
		m_CurState.setProperty("CurrentPairA", m_CurState.getProperty("Pair" + set + "A"));
		m_CurState.setProperty("CurrentPairB", m_CurState.getProperty("Pair" + set + "B"));
		m_CurState.setProperty("CurrentPair", m_CurState.getProperty("Pair" + set));
		
		m_CurState.setProperty("CurrentPairAFile", m_CurState.getProperty("Pair" + set + "AFile"));
		m_CurState.setProperty("CurrentPairBFile", m_CurState.getProperty("Pair" + set + "BFile"));
		
		m_CurState.setProperty("CurrentGroup", m_CurState.getProperty("Pair" + set + "Group"));	
		m_CurState.setProperty("CurrentValue", m_CurState.getProperty("Pair" + set + "Value"));	
		m_CurState.setProperty("CurrentOrder", m_CurState.getProperty("Pair" + set + "Order"));
		m_CurState.setProperty("CurrentDataOrder", m_CurState.getProperty("Pair" + set + "DataOrder"));
		m_CurState.setProperty("CurrentExtraInfo", m_CurState.getProperty("Pair" + set + "ExtraInfo"));
		m_CurState.setAudioVolumeProperty("CurrentVolume");

		String translation = m_CurState.translateString(m_sStudyContent);
		translation = m_App.translateString(translation);
		
		if (!m_cClock.start(set)) {
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
		adjustCalControls();
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
		Dimension dimensions = new Dimension(128, 35);
		Point location = new Point(newSz.width/2-dimensions.width/2+border.width, newSz.height-1);

		location.x -= border.width;
		location.y -= border.height + dimensions.height;

		m_cClock.setSize(dimensions);
		m_cClock.setLocation(location);
	}
	
	public boolean allowNextState() {
		//System.out.println("Clock check...");
		
		if (m_cClock != null && !m_cClock.isComplete()) {
			//System.out.println("Display Limit Prompt");
			return (m_App.displayPrompt("LimitPromptText", "LimitPromptButtons"));
		}
		
		return true;
	}

	/** 
	 * 	Says that the clock timer should be reset after each Step in a 
	 *  multi-step instruction.
	 *  
	 * @return true if the current state has the property.
	 */
	private boolean isPerStepTimer() {
		return m_CurState.getBooleanProperty("ResetOnNext", false);
	}

	/** 
	 * 	Says that the clock timer should be continuous regardless of
	 *  study state.
	 *  
	 * @return true if the current state has the property.
	 */
	private boolean isContinuousTimer() {
		String pst = m_CurState.getStringProperty("ContinuousTimer");
		
		if (pst == null)
			return false;
		
		if (pst.equals("yes") || pst.equals("true"))
			return true;
		
		/* Make sure that we consider the clock continuous
		 * if ONLY ContinuousAfterFirstSelection is set.
		 */
		return isContinuousAfterFirstSelection();
	}

	/** 
	 *  Says that the clock is continuous after the first study attempt.
	 *  
	 * @return true if the current state has the property.
	 */
	private boolean isContinuousAfterFirstSelection() {
		String pst = m_CurState.getStringProperty("ContinuousAfterFirstSelection");
		
		if (pst == null)
			return false;
		
		if (pst.equals("yes") || pst.equals("true"))
			return true;
		
		return false;
	}

	
	private void updateInstructionFile(boolean reconfigure) throws Exception {
		// TODO: move stateful information into the STATE (is too much in this class?)
		String instructionFile = (String) m_CurState.getSequenceFile();
		
		if (reconfigure) {
			int icount = m_CurState.getIntProperty("Count");
			int isets = m_CurState.getIntProperty("Sets");
			
			if (isets < 1)
				isets = 1;
			if (icount < 1)
				icount = 1;

			m_CurState.setStep(1);
			m_CurState.resetTimeElapsed();
			m_CurState.setStepMax(isets);
			m_CurState.setPerStepCount(icount);
			
			setProperties();
		}

		if (instructionFile != null) {
			try {
				this.showProcessedInstructions((String) instructionFile);
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new Exception("IO Error: " + ex.getMessage() + "\nFile: " + instructionFile);
			}
		} else {
			throw new Exception("Failed to set instruction file! (InstructionFile == null)");
		}
		
		m_HtmlView.requestFirstFocus();
		
    updateInterface();
		m_CurState.startAudioPlayback();
	}
	
	private void updateInterface() {
	  updateClock();
		updateButtonText();

		/* The clock is continuous and is NOT only after first Study */
		if (isContinuousTimer() && !isContinuousAfterFirstSelection())
			m_cClock.start("");

    showCalibrationControls(m_CurState.getState() == CmeState.STATE_AUDIO_CAL);
	}

	private void recordElapsedTime() throws Exception {
		long elapsed = m_CurState.getTimeElapsed();

		for (Integer item : m_viCurrentItems) {
			String currentIndex = Integer.toString(m_PairFactory.getTrueIndex(item));
			m_App.addFeedback("TimeElapsed" + currentIndex, Long.toString(elapsed));	
		}
	}
	
	/**
	 * Used to set the next rating environment.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean setNextInSequence() throws Exception {
		if (!allowNextState()) {
			return true;
		}
		
		if (!isProvidedFeedbackValid()) {
			return false;
		}

		// If there is another instruction file to show, then show it!
		if (m_CurState.nextSequencePosition()) {
			updateInstructionFile(false);
			return true;
		}
 
		recordElapsedTime();

		int cstep = m_CurState.getStep();
		if (cstep > 0) {
			m_HtmlView.setVisible(false);
			m_App.displayPrompt("PostPromptText");

			m_CurState.resetTimeElapsed();

			if (m_CurState.isDone()) {
					m_HtmlView.clearContent();

				if (m_cClock != null)
					m_cClock.complete();
				return false;
			}
		}

		m_iStudyCount = 1;

		m_CurState.setStep(cstep + 1);
		CmeIterator iterator = m_CurState.getIterator();

		if (iterator == null) {
			throw new Exception("Failed to retreive iterator from current state (setNextSequential)!");
		}
		
		m_App.displayPrompt("PrePromptText");
		
		if (isPerStepTimer()) {
			m_cClock.reset();
			this.updateUI();
		}

    m_CurState.resetSeqState();
    m_CurState.initSequence();

    clearColors();
    setProperties();

		updateInstructionFile(false);
		return true;
	}
	
	private double getScale() {
		double scale = 1.0;
		
		String sscale = (String) m_CurState.getProperty("Scale");
		if (sscale != null) {
			try {	
				scale = Double.parseDouble(sscale);
			} catch (Exception ex) {
				//System.out.println("Ooops, no scale defined! Using 1.0.");
				scale = 1.0;
				ex.printStackTrace();
			}
		}
		
		return scale;
	}

  /**
   * Set the display properties for a shuffled study state.
   *
   * @throws Exception
   */
  private void shuffleProperties() throws Exception {
    double scale = getScale(); 
    int count = m_CurState.getPerStepCount();
    final int currentStep = m_CurState.getStep();
    String preStudyColor = m_CurState.getStringProperty("PreStudyColor");
    
    int step;
    String vx;

    m_App.dmsg(CmeApp.DEBUG_STUDY_PHASES, "Shuffling Properties");

    CmeRandomIter iter = new CmeRandomIter(CmeIterator.EXCLUSIVE);
    iter.initIterator(m_viCurrentItems);

    for (int x = 0; x < count; x++) {
      step = iter.getNext();
      vx = Integer.toString(x + 1);
      //System.out.println("Generating Pairs for " + vx);

      m_CurState.setProperty("Pair" + vx + "A", m_PairFactory.getFeedbackA(step, (int) (scale * 1000)));
      m_CurState.setProperty("Pair" + vx + "B", m_PairFactory.getFeedbackB(step, (int) (scale * 1000)));
      m_CurState.setProperty("Pair" + vx + "AFile", m_PairFactory.getFileA(step));
      m_CurState.setProperty("Pair" + vx + "BFile", m_PairFactory.getFileB(step));
      m_CurState.setProperty("Pair" + vx, Integer.toString(step));
      
      m_CurState.setProperty("Pair" + vx + "Sequence", Integer.toString(m_CurState.getStep()));
      m_CurState.setProperty("Pair" + vx + "Group", m_PairFactory.getPairGroup(step));  
      m_CurState.setProperty("Pair" + vx + "Order", Integer.toString(x));  
      m_CurState.setProperty("Pair" + vx + "Value", m_PairFactory.getPairValue(step));
      m_CurState.setProperty("Pair" + vx + "DataOrder", Integer.toString(m_PairFactory.getTrueIndex(step)));
      m_CurState.setProperty("Pair" + vx + "ExtraInfo", m_PairFactory.getPairExtraInfoVector(step));

      if (m_CurState.canStudy() && preStudyColor != null) {
        String color = preStudyColor;

        Object itemColor = m_CurState.getProperty("Item" + Integer.toString(step) + "Color");
        if (itemColor != null && itemColor instanceof String)
          color = (String) itemColor;

        m_CurState.setProperty("Pair" + vx + "Color", color);
      }
    }
  }

  private void clearColors() throws Exception {
    int count = m_CurState.getPerStepCount();
    String preStudyColor = m_CurState.getStringProperty("PreStudyColor");
    String vx = null;

    for (int x = 0; x < count; x++) {
      vx = Integer.toString(x + 1);
      m_CurState.setProperty("Pair" + vx + "Color", preStudyColor);
    }
  }

	/**
	 * Set the display properties for a study state.
	 * 
	 * @throws Exception 
	 */
	private void setProperties() throws Exception {
		if (m_CurState == null)
			return;
		
		double scale = getScale();
		CmeIterator iter = m_CurState.getIterator();		
		int count = m_CurState.getPerStepCount();
		final int currentStep = m_CurState.getStep();
		String preStudyColor = m_CurState.getStringProperty("PreStudyColor");
		
		int step;
		String vx;

		m_CurState.setProperty("CurrentSet", Integer.toString(currentStep));
    m_viCurrentItems.clear();

    boolean shuffle = m_CurState.getBooleanProperty("ShuffleItems", false);

    m_App.dmsg(CmeApp.DEBUG_STUDY_PHASES, "Configuring Set Properties");

		for (int x = 0; x < count; x++) {
			step = iter.getNext();
      m_viCurrentItems.add(step);

      if (!shuffle) {
  			vx = Integer.toString(x + 1);
  			//System.out.println("Generating Pairs for " + vx);

  			m_CurState.setProperty("Pair" + vx + "A", m_PairFactory.getFeedbackA(step, (int) (scale * 1000)));
  			m_CurState.setProperty("Pair" + vx + "B", m_PairFactory.getFeedbackB(step, (int) (scale * 1000)));
  			m_CurState.setProperty("Pair" + vx + "AFile", m_PairFactory.getFileA(step));
  			m_CurState.setProperty("Pair" + vx + "BFile", m_PairFactory.getFileB(step));
  			m_CurState.setProperty("Pair" + vx, Integer.toString(step));
  			
  			m_CurState.setProperty("Pair" + vx + "Sequence", Integer.toString(m_CurState.getStep()));
  			m_CurState.setProperty("Pair" + vx + "Group", m_PairFactory.getPairGroup(step));	
  			m_CurState.setProperty("Pair" + vx + "Order", Integer.toString(x));	
  			m_CurState.setProperty("Pair" + vx + "Value", m_PairFactory.getPairValue(step));
  			m_CurState.setProperty("Pair" + vx + "DataOrder", Integer.toString(m_PairFactory.getTrueIndex(step)));
  			m_CurState.setProperty("Pair" + vx + "ExtraInfo", m_PairFactory.getPairExtraInfoVector(step));
  			m_CurState.setAudioVolumeProperty("Pair" + vx + "Volume");

  			if (m_CurState.canStudy() && preStudyColor != null) {
  				m_CurState.setProperty("Pair" + vx + "Color", preStudyColor);
  			}
      }
		}
		
    if (shuffle)
      shuffleProperties();

		setSetPoolProperties();
		setStatePoolProperties();
		
		//System.out.println("Done creating random pools.");
	}
	
	private boolean setSetPoolProperties() {
		return m_App.handleCompoundProperty(m_CurState, "SetPool", new CmeStringHandler() {
				public boolean handleString(String handle) {	
					
					/* Let the current state properties override the local properties. */
					handle = m_CurState.translateString(handle);
					handle = m_App.translateString(handle);
					
					CmeSelectiveIter sel = m_App.getPool(handle);
					
					if (sel == null)
						return false;
					
					sel.reset();
					int count = sel.base_count();
		
					for (int x=0; x<count; x++) {
						int next = sel.getNext();
						handle = handle.trim();
						String str = handle + String.valueOf(x+1);
						//System.out.println("Adding Property: " + str + " as '" + String.valueOf(next) + "'");
						m_CurState.setProperty(str, String.valueOf(next));
					}
			
					return true;
				}
			});
	}
	
	private boolean setStatePoolProperties() {
		return m_App.handleCompoundProperty(m_CurState, "StatePool", new CmeStringHandler() {
					public boolean handleString(String handle) {
						
						/* Let the current state properties override the local properties. */
						handle = m_CurState.translateString(handle);
						handle = m_App.translateString(handle);
						
						CmeSelectiveIter sel = m_App.getPool(handle);
						
						if (sel == null)
							return false;
						
						if (m_CurState.getStep() == 0)
							sel.reset();
						
						int next = sel.getNext();
						String snext = String.valueOf(next);
						
						handle = handle.trim();
						m_CurState.setProperty(handle, snext);
						
						return true;
					}
			});
	}

	/**
	 * Show the processed instructions with the current selected image.
	 * 
	 * @param fileName - name of the instruction file to show.
	 * 
	 * @throws IOException
	 */
	private void showProcessedInstructions(String fileName) throws Exception {
		/* Let the current state properties override the local properties. */
		fileName = m_CurState.translateString(fileName);
		fileName = m_App.translateString(fileName);

		m_sContent = CmeApp.readFile(fileName);

		this.adjustLayout();
		
		String content;
		
		/* App props may be constant, but the state namespace may chose to override them! */
		content = m_CurState.translateString(m_sContent);
		content = m_App.translateString(content);
		
		ActionListener listener = m_SubmitListener;
		
		// m_App.dmsg(10, "Setting Contents");
		if (m_CurState.getProperty("DisableSubmitHook") != null)
			listener = null;
		if (m_CurState.getProperty("DisableSubmitOnEnter") != null)
			listener = null;
		
		m_HtmlView.setContent(content, listener);
	}

	private int displayTimer() {
		String pst = m_CurState.getStringProperty("DisplayTimer");
		
		if (pst == null)
			return 0;
		
		pst = pst.toLowerCase();
		
		if (pst.equals("no") || pst.equals("false"))
			return 0;
		
		if (pst.equals("up"))
			return +1;
		
		return -1;
	}
	
	private void updateClock() {
		int clockDirection = displayTimer();
		if (clockDirection == 0)
				m_cClock.setVisible(false);
		else {
			if (clockDirection < 0) 
				m_cClock.setCountDown(true);
			else
				m_cClock.setCountDown(false);
			m_cClock.setVisible(true);
		}
		
		int timeLimit = m_CurState.getIntProperty("Limit");
		int timeRes = m_CurState.getIntProperty("Resolution");
		
		if (timeLimit <= 0) {
			m_cClock.reset();
			m_cClock.setVisible(false);
			return;
		}

		if (timeRes < 0)
			timeRes = 1;
		
		m_cClock.setTimeLimit(timeLimit);
		m_cClock.setResolution(timeRes);
		m_cClock.reset();
		
		/* The clock is continuous and is NOT only after first Study */
		if (isContinuousTimer() && !isContinuousAfterFirstSelection())
			m_cClock.start("");
	}

	/**
	 * Update the button to the appropriate text according to the current
	 * sequence and state.
	 */
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

		adjustLayout();
	}
	
	/**
	 * Load the STUDY_FILE into the main view.
	 */
	private void updateStudyContent() throws IOException {
		m_iStudyCount = 1;
		Object pbStudyFile = m_CurState.getProperty("StudyFile");
		
		if (pbStudyFile != null) {
			m_sStudyContent = CmeApp.readFile(pbStudyFile.toString());
			//System.out.println("Study File: " + pbStudyFile.toString());
		} else {
			//System.out.println("No Study File Set!");
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
		} while (!m_CurState.validateInput(null, input));

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
	 * Updates the PairFactory to the desired dataset settings.
	 */
	public void updateDataset() {
		String group = m_CurState.getStringProperty("CurrentDataset");
		if (group == null)
			m_PairFactory.clearDataSet();
		else {
			String[] groups = group.split(",");
			m_PairFactory.setDataSets(groups);
		}
	}
	
	/**
	 * Sets the current state and adjusts the layout appropriately
	 * 
	 * @param mCurState
	 *            - the current state to be displayed
	 * @throws Exception
	 */
	public void setState(CmeState mCurState) throws Exception {
		m_CurState = mCurState;
		
		updateDataset();

		updateStudyContent();
	  m_CurState.configureAudioPlayback();
		
		// Begin!
		try {
			switch (m_CurState.getState()) {
				case CmeState.STATE_INPUT:
				case CmeState.STATE_INSTRUCTION:
					updateInstructionFile(false);
					break;

				case CmeState.STATE_MULTIPLE:
					updateInstructionFile(true);
					break;

				case CmeState.STATE_AUDIO_CAL:
					updateInterface();
					break;

				case CmeState.STATE_PROMPT:
					this.setVisible(false);
					showInputPrompt((String) m_CurState.getProperty("PromptText"));
					if (isProvidedFeedbackValid())
						m_App.setNextState();
					return;

				default:
					throw new Exception("Default state handler hit!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Failed to set Instruction State: "
					+ ex.getMessage());
		}
		
		this.setVisible(true);
		
		m_HtmlView.requestFirstFocus();
	}

	public boolean isProvidedFeedbackValid() {
		boolean valid = false;
		
		try {
			Iterator<CmeResponse> iter = m_HtmlView.getResponseIterator();

			valid = m_CurState.validateInput(iter);
			if (!valid) {
				//System.out.println("Invalid input!");
				return false;
			}
			
			valid = generateFeedback();
		} catch (Exception ex) {
			//System.out.println(
			//		"Failed to generate output for feedback:\n" + ex.getMessage());
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

  private String getCalLabel() {
    String lbl = m_CurState.getStringProperty("CalibrationText");

    if (lbl == null)
      lbl = "Adjust the volume till all audio is at comfortable volume.";

    return lbl;
  }

	/**
	 * Adjust the Next Button for the current window size.
	 */
	private void adjustCalControls() {
	  if (m_lblCalText == null || m_CalSlider == null || m_lblSliderText == null)
  	  return;

  	if (m_CurState == null || m_CurState.getState() != CmeState.STATE_AUDIO_CAL)
  	  return;

    Dimension newSz = this.getSize();

    Dimension border = new Dimension(10, 10);
    Dimension dimensions = new Dimension(128, 24);

    int center = newSz.width/2;
    int count = m_vjbCalibrationControl.size();
    int boffset = count * (5+64) - 10;
    int top = newSz.height/2 - 12;

    Point location = new Point(center - boffset, top);

    for (JButton btn : m_vjbCalibrationControl) {
      btn.setSize(dimensions);
      btn.setLocation(location);
      location.x += dimensions.width + border.width;
    }

    location.x = newSz.width/4;
    location.y -= 100;

    m_lblCalText.setSize(newSz.width/2, 32);
    m_lblCalText.setLocation(location);
    m_lblCalText.setHorizontalAlignment(SwingConstants.CENTER);

    location.y += 200;
    location.x = newSz.width/4;

    m_CalSlider.setSize(newSz.width/2, 40);
    m_CalSlider.setLocation(location);
    m_CalSlider.setMajorTickSpacing(20);
    m_CalSlider.setPaintLabels(true);
    m_CalSlider.setPaintTicks(true);
    
    location.y -= 40;
    location.x = newSz.width/4;
    
    m_lblSliderText.setSize(newSz.width/2, 32);
    m_lblSliderText.setLocation(location);
    m_lblSliderText.setHorizontalAlignment(SwingConstants.CENTER);
    
	}
	
	// Vector<JButton> m_vjbCalibrationControl
	// JLabel m_lblCalText
	// JSlider m_CalSlider
	
	private void showCalibrationControls(boolean show) {
    m_ScrollPane.setVisible(!show);

    if (show) {
      if (m_lblCalText == null) {
        m_lblCalText = new JLabel();
        this.add(m_lblCalText);
      }
      
      m_lblCalText.setText(getCalLabel());

      if (m_lblSliderText == null) {
        m_lblSliderText = new JLabel("Volume");
        this.add(m_lblSliderText);
      }

      if (m_CalSlider == null) {
        m_CalSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 50);
        final CmeState changeState = m_CurState;
        m_CalSlider.addChangeListener(new ChangeListener() {

          @Override
          public void stateChanged(ChangeEvent e) {
            JSlider sld = (JSlider)e.getSource();
		        String maxbias = changeState.getStringProperty("MaxAudioBias");
		        float maxb = 0.0f;
		        if (maxbias != null)
		        	maxb = Float.valueOf(maxbias);
            float volume = CmeAudioPlayer.getGainFromPercent(sld.getValue(), maxb);
            m_App.setProperty("AudioVolume", String.valueOf(volume));
          }

        });
        this.add(m_CalSlider);
      }

      recreateCalibrationControls();
      adjustCalControls();

      m_CalSlider.setVisible(true);
      m_lblCalText.setVisible(true);
    } else {
      if (m_lblCalText != null)
        m_lblCalText.setVisible(false);

      if (m_CalSlider != null)
        m_CalSlider.setVisible(false);

      if (m_lblSliderText != null)
        m_lblSliderText.setVisible(false);

      for (JButton btn : m_vjbCalibrationControl) {
        btn.setVisible(false);
      }
    }
	}

	private void recreateCalibrationControls() {
    final CmeState actionState = m_CurState;

    String calGroup = m_CurState.getStringProperty("CalibrationGroup");
    String[] groups = m_PairFactory.getDataSets();

    m_PairFactory.setDataSet(calGroup);
    int count = m_PairFactory.getCount();

    for (JButton btn : m_vjbCalibrationControl)
      this.remove(btn);

    m_vjbCalibrationControl.clear();

    for (int index = 0; index < count; index++) {
      JButton btn = new JButton(String.format("Sound %d", index+1));
      m_vjbCalibrationControl.add(btn);
      this.add(btn);

      final String audioFile = m_PairFactory.getNameB(index);
      final String audioBias = m_PairFactory.getExtraInfo(index, 0);

      btn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          actionState.setProperty("AudioPath", audioFile);
          actionState.setProperty("BiasVolume", audioBias);
          actionState.setProperty("PlayAudio", "immediate");

          try {
            actionState.startAudioPlayback();
          } catch (Exception ex) {
            System.err.println("Failed to play file:");
            System.err.println(audioFile);

            ex.printStackTrace();
          }
        }
      });
    }

    m_PairFactory.setDataSets(groups);
	}
}