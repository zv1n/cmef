package pkgCMEF;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
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
	
	/** Vector store for the names of each editable component */
	private Vector<String> m_ComponentList;
	private Iterator<String> m_CompIter;
	
	private int MAX_DEPTH = 10;

	// ----------------------------------------------------------------
	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	// ----------------------------------------------------------------
	public CmeInstructions(CmeApp parent) {
		m_App = parent;
		
		m_ComponentList = new Vector<String>();

		this.setVisible(false);
		this.setSize(parent.getSize());
		this.setBorder(null);

		this.setLayout(null);
		
		AncestorListener aListener = new AncestorListener(){
			@Override public void ancestorRemoved(AncestorEvent arg0) {}
			@Override public void ancestorAdded(AncestorEvent arg0) {}
			@Override public void 
			ancestorMoved(AncestorEvent e) {
				adjustLayout();
			}
		};

		/** Configure HTML View Pane */
		m_HtmlView = new JTextPane();
		m_HtmlView.setEditable(false);
		m_HtmlView.setDoubleBuffered(true);
		/*m_HtmlView.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));*/
	
		m_HtmlView.addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(ContainerEvent arg0) {	
				if (m_ComponentList.isEmpty())
					generateComponentList();
			}
			@Override public void componentRemoved(ContainerEvent arg0) {}
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
				if (m_CurState != null)
					if (m_CurState.getState() == CmeState.STATE_FEEDBACK) {
						try {
							generateFeedbackInfo(m_HtmlView);
						} catch (Exception ex) {
							System.out.println("Failed to generate output for feedback:\n" + ex.getMessage());
						}
					}
					m_CurState.TriggerEvent(CmeState.EVENT_CLICK_PRIMARY);
			}
		});
		this.add(m_bNext);
	}
	
	private void generateComponentList() 
	{
		m_App.dmsg(10, "Generate component list!");
		Element[] root = ((HTMLDocument)m_HtmlView.getDocument()).getRootElements();
		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseComponentList(root[htm], 0);
			}
		}
	}
	
	private void recurseComponentList(Element node, int depth) 
	{
		if (node.getName() == "input") {
			AttributeSet attr = node.getAttributes();
			String name = (String) attr.getAttribute(HTML.Attribute.NAME);
			if (name == null) 
				name = (String) attr.getAttribute(HTML.Attribute.ID);
			if (name == null) {
				m_App.dmsg(10, "No Name Input or ID!");
				System.exit(1);
			}
			m_ComponentList.add(name);
		}
		for (int x = 0; x < node.getElementCount(); x++) {
			if (depth < MAX_DEPTH) {
				recurseComponentList(node.getElement(x), depth+1);
			} else {
				System.out.println("Max depth reached!");
			}
		}	
	}
	
	private void generateFeedbackInfo(Container container) throws Exception
	{
		if (m_CompIter == null) {
			m_CompIter = m_ComponentList.iterator();
		}
		
		Component[] components = container.getComponents();
		for (int x=0; x<components.length; x++) {
			
			if (components[x] instanceof JTextField) {
				JTextField tf = (JTextField) components[x];
				
				if (m_CompIter.hasNext()) {
					System.out.println(m_CompIter.next() + "|" + tf.getText());
				} else {
					throw new Exception("Invalid number of components! (tf)");
				}
				
			} else if (components[x] instanceof JRadioButton) {
				JRadioButton rb = (JRadioButton) components[x];
				
				if (m_CompIter.hasNext()) {
					System.out.println(m_CompIter.next() + "|" + Boolean.toString(rb.isSelected()));
				} else {
					throw new Exception("Invalid number of components! (rb)");
				}
				
			} else if (components[x] instanceof Container) {
				generateFeedbackInfo((Container)components[x]);
			}
		}
		
		if (m_CompIter != null && !m_CompIter.hasNext()) {
			m_CompIter = null;
		}
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
		Dimension dimensions = new Dimension(128,24);
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
	private void showInstructions(String fileName) throws IOException 
	{
		File instFile = new File(fileName);

		this.adjustLayout();
		m_ComponentList.clear();
		
		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
	}
	
	private void showInputPrompt(String msg) throws Exception 
	{
		String input;
		
		msg = m_CurState.translateString(msg);
		msg = m_App.translateString(msg);
		
		do {
			input = JOptionPane.showInputDialog(msg);
		} while (!m_CurState.validateInput(input));
	}

	/**
	 * Paint function for the Instruction
	 * @param g - graphics context for the current JPane
	 */
	public void paint(Graphics g) {
		super.paint(g);
		// Paint the area
	}

	/**
	 * Sets the current state and adjusts the layout appropriately
	 * @param mCurState - the current state to be displayed
	 * @throws Exception
	 */
	public void setState(CmeState mCurState) throws Exception {
		m_CurState = mCurState;

		// Begin!
		try {
			switch (m_CurState.getState()) {
			case CmeState.STATE_FEEDBACK:
			case CmeState.STATE_INSTRUCTION:
				Object instructionFile = m_CurState.getProperty("InstructionFile");
				if (instructionFile != null)
					this.showInstructions(instructionFile.toString());
				break;

			case CmeState.STATE_PROMPT:
				showInputPrompt((String)m_CurState.getProperty("PromptText"));
				this.setVisible(false);
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
			throw new Exception("Failed to set Instruction State: " + ex.getMessage());
		}
		
		String pbText = m_CurState.getProperty("PrimaryButtonText").toString();
		if (pbText != null) {	
			m_bNext.setText(pbText);
		}

		this.setVisible(true);
	}
}
