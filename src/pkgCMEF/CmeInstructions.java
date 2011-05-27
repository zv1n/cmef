package pkgCMEF;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTMLDocument;
import com.sun.xml.internal.ws.api.ResourceLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

	// ----------------------------------------------------------------
	/**
	 * Default constructor
	 * 
	 * @throws IOException
	 */
	// ----------------------------------------------------------------
	public CmeInstructions(CmeApp parent) {
		m_App = parent;

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
					//if (m_CurState.getState() == CmeState.STATE_FEEDBACK) 
						generateFeedback(m_HtmlView);
					m_CurState.TriggerEvent(CmeState.EVENT_CLICK_PRIMARY);
			}
		});
		this.add(m_bNext);

	}
	
	private void generateFeedback(Container container)
	{
		Component[] components = container.getComponents();
		for (int x=0; x<components.length; x++) {
			if (components[x] instanceof JTextField) {
				JTextField tf = (JTextField) components[x];
				System.out.print("d");
			} else if (components[x] instanceof JRadioButton) {
				JRadioButton rb = (JRadioButton) components[x];
				System.out.print("d");
			//} else if (components[x] instanceof FormView) {
			//	generateFeedback((Container)components[x]);
			} else if (components[x] instanceof Container) {
				generateFeedback((Container)components[x]);
			}
		}
	}
	
	// ----------------------------------------------------------------
	/**  
	 * */
	// ----------------------------------------------------------------
	public void adjustLayout() {
		adjustHtmlView();
		adjustNextButton();
	}
	
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
	
	private void adjustHtmlView() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = (Dimension) newSz.clone();
		Point location = new Point(border.width, border.height);

		int lower_offset = 0;

		if (m_CurState == null)
			lower_offset = 0;
		else {
			switch (m_CurState.getState()) {
			case CmeState.STATE_FEEDBACK:
				lower_offset = border.height + 72;
				break;
				
			case CmeState.STATE_INSTRUCTION:
				lower_offset = border.height + 24;
				break;
				
			case CmeState.STATE_PROMPT:
				break;
			}	
		}
		
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
	public boolean showInstructions(String fileName) throws IOException 
	{
		File instFile = new File(fileName);

		this.adjustLayout();
		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
			
		return true;
	}
	
	/**
	 * Used to configure the feedback GUI components when feedback is requested. 
	 */
	private void showFeedbackArea() 
	{
		
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
				this.showFeedbackArea();

			case CmeState.STATE_INSTRUCTION:
				Object instructionFile = m_CurState.getProperty("InstructionFile");
				if (instructionFile != null)
					this.showInstructions(instructionFile.toString());
				break;

			case CmeState.STATE_PROMPT:
				break;

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
