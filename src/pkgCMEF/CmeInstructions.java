package pkgCMEF;

import java.awt.*;
import java.awt.event.*;

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
	
	/** Reference to the image factory */
	private CmePairFactory m_PairFactory;
	
	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	public CmeInstructions(CmeApp parent) {
		m_App = parent;

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

		/** Configure HTML View Pane */
		m_HtmlView = new CmeHtmlView(m_App);
		/*
		 * m_HtmlView.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED));
		 */

		m_ScrollPane = new JScrollPane(m_HtmlView);
		m_ScrollPane.addAncestorListener(aListener);
		this.add(m_ScrollPane);

		/** Configure Next JButton */
		m_bNext = new JButton();
		m_bNext.setText("Next");
		m_bNext.setVisible(true);
		m_bNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_CurState != null) {
					boolean valid = true;
					if (m_CurState.getState() == CmeState.STATE_FEEDBACK ||
						m_CurState.getState() == CmeState.STATE_SEQUENTIAL||
						m_CurState.getState() == CmeState.STATE_SIMULTANEOUS) 
					{
						try {

							Iterator<CmeResponse> iter = m_HtmlView.getResponseIterator();
							valid = m_CurState.validateInput(iter);
							if (!valid)
								return;
							
							valid = generateFeedback(m_CurState);
						} catch (Exception ex) {
							System.out.println(
									"Failed to generate output for feedback:\n" + ex.getMessage());
							ex.printStackTrace();
							System.exit(1);
						}
					}
					
					if (valid)
						m_CurState.TriggerEvent(CmeState.EVENT_CLICK_PRIMARY);
				}
			}
		});
		this.add(m_bNext);
	}
	
	/** Set a reference to the image factory */
	public void setPairFactory(CmePairFactory iFact)
	{
		m_PairFactory = iFact;
	}
	
	/**
	 * Adjust all component present for the current window size.
	 * */
	public void adjustLayout() {
		adjustHtmlView();
		adjustNextButton();
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
		location.y -= border.width + dimensions.height;

		m_bNext.setSize(dimensions);
		m_bNext.setLocation(location);
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

		dimensions.height = newSz.height - (border.height * 2 + lower_offset);
		dimensions.width -= border.width * 2;

		m_HtmlView.setSize(dimensions);
		m_ScrollPane.setSize(dimensions);
		m_ScrollPane.setLocation(location);
	}

	/**
	 * Used to determine if the Sequentials are complete.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean isDoneSequential() throws Exception {
		if (m_CurState.getState() != CmeState.STATE_SEQUENTIAL)
			throw new Exception("Tested if a rating was done when NOT in a rating step!");

		int istep = m_CurState.getSequentialStep();
		int ismax = m_CurState.getSequentialStepMax();
				
		return (istep >= ismax);
	}

	/**
	 * Used to set the next rating environment.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean setNextInSequence() throws Exception {
		String instructionFile = null;
		
		if (isDoneSequential())
			return false;
		
		int cstep = m_CurState.getSequentialStep();
		m_CurState.setSequentialStep(cstep+1);

		CmeIterator iterator = m_CurState.getIterator();
		
		if (iterator == null)
			throw new Exception("Failed to retreive iterator from current state (setNextSequential)!");
		
		setSequentialProperties(iterator.getNext());
		
		instructionFile = (String)m_CurState.getProperty("InstructionFile");
		if (instructionFile != null)
			this.showProcessedInstructions((String) instructionFile);
		else
			throw new Exception("Failed to set instruction file! (InstructionFile == null)");
		
		return true;
	}
	
	private void setSequentialProperties(int step) throws Exception {
		if (m_CurState == null)
			return;
		
		double scale = 0;
		String sscale = (String) m_CurState.getProperty("Scale");
		assert(sscale != null);
		
		try {
			scale = Double.parseDouble(sscale);
		} catch (Exception ex) {
			System.out.println("Ooops, no scale defined! Using 100.0.");
			scale = 1.0;
			ex.printStackTrace();
		}		
		
		m_CurState.setProperty("CurrentPairA", m_PairFactory.getFeedbackA(step, (int)(scale*1000)));
		m_CurState.setProperty("CurrentPairB", m_PairFactory.getFeedbackB(step, (int)(scale*1000)));
		m_CurState.setProperty("CurrentPair", Integer.toString(step));
	}
	
	/**
	 * Configure the view for Simultaneous presentation.
	 * @throws Exception 
	 */
	private void setSimultaneous() throws Exception {
		Object instructionFile = m_CurState.getProperty("InstructionFile");
		assert(instructionFile != null);
		
		String count = (String) m_CurState.getProperty("Count");
		assert(count != null);
		
		System.out.println("Count: " + count);
		
		int icount = 0;
		
		try {
			icount = Integer.parseInt(count);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Invalid Simultaneous Count: " + ex.getMessage());
		}
		
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
		if (m_CurState == null)
			return;

		double scale = 0;
		CmeIterator iter = m_CurState.getIterator();
		String sscale = (String) m_CurState.getProperty("Scale");
		
		assert(sscale != null);
		
		try {
			scale = Double.parseDouble(sscale);
		} catch (Exception ex) {
			System.out.println("Ooops, no scale defined! Using 100.0.");
			scale = 1.0;
			ex.printStackTrace();
		}
		
		for (int x=0; x<count; x++) {
			int step = iter.getNext();
			String vx = Integer.toString(x+1);
			
			m_CurState.setProperty("Pair" + vx + "A", m_PairFactory.getFeedbackA(step, (int)(scale*1000)));
			m_CurState.setProperty("Pair" + vx + "B", m_PairFactory.getFeedbackB(step, (int)(scale*1000)));
			m_CurState.setProperty("Pair" + vx, Integer.toString(step));
		}
	}

	/**
	 * @param filePath
	 *            the name of the file to open.
	 */
	private static String readFile(String filePath) throws java.io.IOException {
		StringBuilder fileData = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line = new String();

		while ((line = reader.readLine()) != null)
			fileData.append(line);

		reader.close();
		return fileData.toString();
	}

	/**
	 * Show the processed instructions with the current selected image.
	 * 
	 * @param fileName - name of the instruction file to show.
	 * 
	 * @throws IOException
	 */
	private void showProcessedInstructions(String fileName) throws Exception {
		String fileContents = readFile(fileName);

		this.adjustLayout();

		fileContents = m_CurState.translateString(fileContents);
		fileContents = m_App.translateString(fileContents);
		
		m_App.dmsg(10, "Setting Contents");
		m_HtmlView.setContent(fileContents);
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

		if (msg == null)
			throw new Exception("Invalid prompt message specified!");

		msg = m_CurState.translateString(msg);
		msg = m_App.translateString(msg);

		do {
			input = JOptionPane.showInputDialog(msg);
			if (input == null)
				return false;
		} while (!m_CurState.validateInput(input));

		String name = (String) m_CurState.getProperty("FeedbackName");
		if (name == null)
			throw new Exception("Feedback name is null!");

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

		// Begin!
		try {
			switch (m_CurState.getState()) {
			case CmeState.STATE_FEEDBACK:
			case CmeState.STATE_INSTRUCTION:
				instructionFile = m_CurState.getProperty("InstructionFile");
				assert(instructionFile != null);

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

		Object pbText = m_CurState.getProperty("PrimaryButtonText");
		if (pbText != null) {
			m_bNext.setText(pbText.toString());
		}

		this.setVisible(true);

		m_App.dmsg(10, "Setup of Instructions Handler complete.");
	}
	
	/**
	 * Generate all feedback from the current component set.
	 * @param state - current state of the experiment
	 * @return true if feedback was generate; false else
	 * @throws Exception
	 */
	private boolean generateFeedback(CmeState state) throws Exception {
		Iterator<CmeResponse> iter = m_HtmlView.getResponseIterator();
		while(iter != null && iter.hasNext())
			m_App.addFeedback(iter.next());
		return true;
	}
}