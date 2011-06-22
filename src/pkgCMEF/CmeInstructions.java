package pkgCMEF;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
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
	private JEditorPane m_HtmlView;

	/** Scroll Pane for Instruction Scrolling */
	private JScrollPane m_ScrollPane;

	/** JButton for primary button */
	private JButton m_bNext;
	
	/** Reference to the image factory */
	private CmePairFactory m_PairFactory;

	/** Vector store for the names of each editable component */
	private Vector<String> m_EditList;
	private Iterator<String> m_EditIter;

	private Vector<String> m_RadioList;
	private Iterator<String> m_RadioIter;
	
	private Vector<String> m_RadioValueList;
	private Iterator<String> m_RadioValueIter;
	
	/** Keep track to ensure at least one radio button is true for each set */
	private HashMap<String, Boolean> m_hRadResults;

	private int MAX_DEPTH = 30;

	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	public CmeInstructions(CmeApp parent) {
		m_App = parent;

		m_EditList = new Vector<String>();
		m_RadioList = new Vector<String>();
		m_RadioValueList = new Vector<String>();
		m_hRadResults = new HashMap<String, Boolean>();

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
		m_HtmlView = new JTextPane();
		m_HtmlView.setEditable(false);
		m_HtmlView.setDoubleBuffered(true);
		/*
		 * m_HtmlView.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED));
		 */

		m_HtmlView.addContainerListener(new ContainerListener() {
			@Override
			public void componentAdded(ContainerEvent arg0) {
				if (m_EditList.isEmpty())
					generateComponentList();
			}

			@Override
			public void componentRemoved(ContainerEvent arg0) {
			}
		});

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
					boolean nextState = true;
					if (m_CurState.getState() == CmeState.STATE_FEEDBACK ||
						m_CurState.getState() == CmeState.STATE_RATING) 
					{
						try {
							nextState = generateFeedbackInfo(m_HtmlView, 0);
						} catch (Exception ex) {
							System.out.println(
									"Failed to generate output for feedback:\n" + ex.getMessage());
							System.exit(1);
						}
					}
					
					if (nextState)
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

	private void generateComponentList() {
		m_App.dmsg(10, "Generate Component List!");

		Element[] root = ((HTMLDocument) m_HtmlView.getDocument())
				.getRootElements();
		
		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseComponentList(root[htm], 0);
			}
		}
		
		m_App.dmsg(11, "Edit List:\n" + m_EditList.toString());
		m_App.dmsg(10, "Componet List Generation Complete!");
	}

	private void recurseComponentList(Element node, int depth) {
		m_App.dmsg(10, "Recursing Component List! (" + node.getName() + ")");

		if (node.getName() == "input") {
			AttributeSet attr = node.getAttributes();
			
			String type = (String) attr.getAttribute(HTML.Attribute.TYPE);
			String name = (String) attr.getAttribute(HTML.Attribute.NAME);
			String value = (String) attr.getAttribute(HTML.Attribute.VALUE);

			if (name == null) {
				System.out.println("All inputs must have a name!");
				System.exit(1);
			}

			m_App.dmsg(25, "|" + type.toLowerCase() + "|");
			if (type.toLowerCase().equals("radio")) {
				if (value == null) {
					System.out.println("Radio buttons must have an input Value!");
					System.exit(1);
				}

				m_RadioList.add(name);
				m_RadioValueList.add(value);
			} else { /*if (type.toLowerCase() == "text")*/
				m_EditList.add(name);
			}
		}

		for (int x = 0; x < node.getElementCount(); x++) {
			if (depth < MAX_DEPTH) {
				recurseComponentList(node.getElement(x), depth + 1);
			} else {
				System.out.println("Max depth reached!");
			}
		}

		m_App.dmsg(10, "Recurse Component List Complete!");
	}

	/**	
	 * Iterate through the containers and pull out the necessary information.
	 * 
	 * @param container
	 *            - container from the HTMLView
	 * @return true if the feedback given is valid, false else.
	 * @throws Exception
	 */
	private boolean generateFeedbackInfo(Container container, int depth) throws Exception {
		boolean ret = true;
		Component[] components = null;
		
		if (depth == 0)
			m_hRadResults.clear();
		
		if (m_EditIter == null) {
			m_EditIter = m_EditList.iterator();
			m_App.dmsg(10, "Edit: " + String.valueOf(m_EditList.size()));
		}

		if (m_RadioIter == null) {
			m_RadioIter = m_RadioList.iterator();
			m_App.dmsg(10, "Radio: " + String.valueOf(m_RadioList.size()));
		}
		
		if (m_RadioValueIter == null) {
			m_RadioValueIter = m_RadioValueList.iterator();
			m_App.dmsg(10, "RadioId: " + String.valueOf(m_RadioValueList.size()));
		}

		components = container.getComponents();
		for (int x = 0; x < components.length; x++) {

			if (components[x] instanceof JTextField) {
				JTextField tf = (JTextField) components[x];

				if (m_EditIter.hasNext()) {
					if (!m_CurState.validateInput(tf.getText())) {
						ret = false;
						m_App.dmsg(10, "Failed to validate input: " + tf.getText());
					} else 
						m_App.addFeedback(m_EditIter.next(), tf.getText());
				} else {
					throw new Exception("Invalid number of components! (tf)");
				}

			} else if (components[x] instanceof JRadioButton) {

				JRadioButton rb = (JRadioButton) components[x];

				if (m_RadioIter == null)
					m_App.dmsg(10, "Null Radio Iterator!");

				if (m_RadioValueIter == null)
					m_App.dmsg(10, "Null Radio Value Iterator!");

				if (m_RadioIter.hasNext() && m_RadioValueIter.hasNext()) {
					String radioName = m_RadioIter.next();
					String radioId = m_RadioValueIter.next();

					if (rb.isSelected()) {
						m_hRadResults.put(radioName, true);
						m_App.addFeedback(radioName, radioId);
					} else if (!m_hRadResults.containsKey(radioName)) 
						m_hRadResults.put(radioName, false);
				
					m_App.dmsg(12, "Radio Button Names: " + radioName);
				} else {
					throw new Exception("Invalid number of components! (rb)");
				}

			} else if (components[x] instanceof Container) {
				ret &= generateFeedbackInfo((Container) components[x], depth+1);
			}
		}

		if (m_EditIter != null && !m_EditIter.hasNext()) {
			m_EditIter = null;
		}

		if (m_RadioIter != null && !m_RadioIter.hasNext()) {
			m_RadioIter = null;	
		}
		
		if (m_RadioValueIter != null && !m_RadioValueIter.hasNext()) {
			m_RadioValueIter = null;
		}

		/* Iterator through the current results for radial sets without a selection */
		if (depth == 0) {
			Collection<Boolean> col = m_hRadResults.values();
			Iterator<Boolean> iter = col.iterator();
			
			m_App.dmsg(10, "Rad Results:\n" + m_hRadResults.toString());
			
			while(iter.hasNext()) {
				if (iter.next() == false)
					return false;
			}
			
			m_App.dmsg(10, "Final return: " + String.valueOf(ret));
		}
		
		return ret;
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
	 * Display the instructions
	 * 
	 * @param fileName
	 *            - name of instructions file to display
	 * @throws IOException
	 */
	private void showInstructions(String fileName) throws IOException {
		File instFile = new File(fileName);

		this.adjustLayout();
		m_EditList.clear();
		m_RadioList.clear();
		m_RadioValueList.clear();

		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
	}

	/**
	 * Used to determine if the Ratings are complete.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean isDoneRating() throws Exception {
		if (m_CurState.getState() != CmeState.STATE_RATING)
			throw new Exception("Tested if a rating was done when NOT in a rating step!");

		int istep = m_CurState.getRatingStep();
		int ismax = m_CurState.getRatingStepMax();
				
		return (istep >= ismax);
	}

	/**
	 * Used to set the next rating environment.
	 * 
	 * @return true if the number of iterations has been met; false else.
	 */
	public boolean setNextRating() throws Exception {
		String instructionFile = null;
		
		if (isDoneRating())
			return false;
		
		int cstep = m_CurState.getRatingStep();
		m_CurState.setRatingStep(cstep+1);
		
		setRatingProperties(cstep+1);
		
		instructionFile = (String)m_CurState.getProperty("InstructionFile");
		if (instructionFile != null)
			this.showRatingInstructions((String) instructionFile);
		else
			throw new Exception("Failed to set instruction file! (InstructionFile == null)");
		
		return true;
	}
	

	private void setRatingProperties(int step) {
		
		if (m_CurState == null)
			return;
		
		m_CurState.setProperty("RatingItemA", m_PairFactory.getFeedbackA(step));
		m_CurState.setProperty("RatingItemB", m_PairFactory.getFeedbackB(step));
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
	 * Show the rating instructions with the current selected image.
	 * 
	 * @param fileName - name of the instruction file to show.
	 * 
	 * @throws IOException
	 */
	private void showRatingInstructions(String fileName) throws Exception {
		String fileContents = readFile(fileName);
		File instFile = new File(fileName);

		this.adjustLayout();
		m_EditList.clear();
		m_RadioList.clear();
		m_RadioValueList.clear();

		fileContents = m_CurState.translateString(fileContents);
		fileContents = m_App.translateString(fileContents);

		m_HtmlView.setContentType("text/html");
		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
		m_HtmlView.setText(fileContents);
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
	 * @param g
	 *            - graphics context for the current JPane
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
				if (instructionFile != null)
					this.showInstructions((String) instructionFile);
				break;

			case CmeState.STATE_RATING:
				instructionFile = m_CurState.getProperty("InstructionFile");
				
				m_CurState.setRatingStep(0);
				m_CurState.setRatingStepMax(m_PairFactory.getCount());
				
				setRatingProperties(0);
				
				if (instructionFile != null)
					this.showRatingInstructions((String) instructionFile);
				break;

			case CmeState.STATE_PROMPT:
				this.setVisible(false);
				showInputPrompt((String) m_CurState.getProperty("PromptText"));
				m_App.setNextState();
				return;

			default:
				m_App.dmsg(0XFF, "Default state hit on Instruction Handler!");
			case CmeState.STATE_TEST:
			case CmeState.STATE_STUDY:
				this.setVisible(false);
				return;
			}
		} catch (IOException ex) {
			throw new Exception("IO Error: " + ex.getMessage());
		} catch (Exception ex) {
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
}