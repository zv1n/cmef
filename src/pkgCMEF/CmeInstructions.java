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
		m_HtmlView = new JEditorPane();
		m_HtmlView.setEditable(false);
		m_HtmlView.setDoubleBuffered(true);
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
					boolean nextState = true;
					if (m_CurState.getState() == CmeState.STATE_FEEDBACK ||
						m_CurState.getState() == CmeState.STATE_SEQUENTIAL||
						m_CurState.getState() == CmeState.STATE_SIMULTANEOUS) 
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
		clearSequenceInfo();
		m_App.dmsg(10, "Generate Component List!");

		if (!(m_HtmlView.getDocument() instanceof HTMLDocument))
			return;
		
		Element[] root = ((HTMLDocument) m_HtmlView.getDocument())
				.getRootElements();
		
		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseComponentList(root[htm], 0);
			}
		}
		
		m_App.dmsg(11, "Edit List:\n" + m_EditList.toString());
		m_App.dmsg(11, "Radio List:\n" + m_RadioList.toString());
		m_App.dmsg(10, "Componet List Generation Complete!");
	}

	private void recurseComponentList(Element node, int depth) {

		m_App.dmsg(11, "|" + node.getName().toLowerCase() + "|");
		if (node.getName() == "input") {
			AttributeSet attr = node.getAttributes();
			
			String type = (String) attr.getAttribute(HTML.Attribute.TYPE);
			String name = (String) attr.getAttribute(HTML.Attribute.NAME);
			String value = (String) attr.getAttribute(HTML.Attribute.VALUE);

			if (name == null) {
				System.out.println("All inputs must have a name!");
				System.exit(1);
			}

			m_App.dmsg(11, "|" + type.toLowerCase() + "|");
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

		m_App.dmsg(11, "Node Depth: " + Integer.toString(node.getElementCount()));
		for (int x = 0; x < node.getElementCount(); x++) {
			if (depth < MAX_DEPTH) {
				recurseComponentList(node.getElement(x), depth + 1);
			} else {
				System.out.println("Max depth reached!");
			}
		}

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
					throw new Exception("Ack, Radio Object, but no radio Iterator!");

				if (m_RadioValueIter == null)
					throw new Exception("Ack, Radio Object, but no radio Value Iterator!");
				
				if (m_RadioIter.hasNext() && m_RadioValueIter.hasNext()) {
					String radioName = new String();
					String radioId = new String();
					
					radioName = m_RadioIter.next();
					radioId = m_RadioValueIter.next();
					
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
		
		m_HtmlView.removeAll();
		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
		generateComponentList();
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
		
		int sscale = 0;
		String scale = (String) m_CurState.getProperty("Scale");
		assert(scale != null);
		
		try {
			sscale = Integer.parseInt(scale);
		} catch (Exception ex) {
			System.out.println("Ooops, no scale defined! Using 100.0.");
			sscale = 1000;
		}
		
		
		m_CurState.setProperty("CurrentPairA", m_PairFactory.getFeedbackA(step, sscale));
		m_CurState.setProperty("CurrentPairB", m_PairFactory.getFeedbackB(step, sscale));
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

		int sscale = 0;
		CmeIterator iter = m_CurState.getIterator();
		String scale = (String) m_CurState.getProperty("Scale");
		
		assert(scale != null);
		
		try {
			sscale = Integer.parseInt(scale);
		} catch (Exception ex) {
			System.out.println("Ooops, no scale defined! Using 100.0.");
			sscale = 100;
		}
		
		for (int x=0; x<count; x++) {
			int step = iter.getNext();
			String vx = Integer.toString(x+1);
			
			m_CurState.setProperty("Pair" + vx + "A", m_PairFactory.getFeedbackA(step, sscale));
			m_CurState.setProperty("Pair" + vx + "B", m_PairFactory.getFeedbackB(step, sscale));
			m_CurState.setProperty("Pair" + vx, Integer.toString(step));
		}
	}
	
	/**
	 * Clear the sequence data from m_RadioIter, m_RadioValIter, etc.
	 */
	private void clearSequenceInfo() {	
		m_EditList.clear();
		m_EditIter = null;

		m_RadioList.clear();
		m_RadioIter = null;
		
		m_RadioValueList.clear();
		m_RadioValueIter = null;
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
		m_EditList.clear();
		m_RadioList.clear();
		m_RadioValueList.clear();

		fileContents = m_CurState.translateString(fileContents);
		fileContents = m_App.translateString(fileContents);
		
		m_HtmlView.removeAll();

		//m_HtmlView.setContentType("text/plain");
		m_HtmlView.setContentType("text/html");
		((HTMLDocument)m_HtmlView.getDocument()).setBase(ClassLoader.getSystemResource("."));
		m_HtmlView.setText(fileContents);
		generateComponentList();
		System.out.println(m_RadioList.size());
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
		clearSequenceInfo();

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