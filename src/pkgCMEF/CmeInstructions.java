package pkgCMEF;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;

import com.sun.xml.internal.ws.api.ResourceLoader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

//====================================================================
/** CmeInstructions
 *  <P>Purpose: This panel displays a JTextArea object for display
 *  instructions.
 *  @author Terry Meacham
 *  @version 2.0
 *  Date: May, 2011
 */
//===================================================================

@SuppressWarnings("serial")
public class CmeInstructions extends JPanel
{
	/** Parent frame */
	CmeApp m_App;
	
	/** Current state */
	CmeState m_CurState;
	
	JEditorPane m_HtmlView;
		
	//----------------------------------------------------------------
	/** Default constructor 
	 * @throws IOException */
	//----------------------------------------------------------------
	public CmeInstructions(CmeApp parent)
	{
		m_App = parent;

		this.setVisible(false);
		this.setSize(parent.getSize());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		this.setLayout(null);
		m_HtmlView = new JTextPane();
		
		m_HtmlView.setEditable(false);
		m_HtmlView.setDoubleBuffered(true);
		m_HtmlView.setBorder(null);
				
		this.setLayout(null);
		this.add(m_HtmlView);
		
		
	}

	//----------------------------------------------------------------
	/**  
	 * */
	//----------------------------------------------------------------
	public void adjustLayout() {
		Dimension newSz = this.getSize();
		Dimension dimHtmlView = (Dimension) newSz.clone();

		switch (m_CurState.getState()) 
		{
		case CmeState.STATE_FEEDBACK:
			break;
			
		case CmeState.STATE_INSTRUCTION:
			dimHtmlView.height = newSz.height-50;
			m_HtmlView.setSize(dimHtmlView);
			break;
			
		case CmeState.STATE_PROMPT:
			break;
		}
	}
	
	//----------------------------------------------------------------
	/** Display the instructions
	 *  @param fileName - name of instructions file to display
	 * @throws IOException 
	 */
	//----------------------------------------------------------------
	public boolean showInstructions(String fileName) throws IOException
	{
		File	instFile = new File(fileName);

		this.adjustLayout();
		
		//JEditorPane.registerEditorKitForContentType("text/html", "com.xxxxx.SynchronousHTMLEditorKit");
		m_HtmlView.setPage("file://" + instFile.getCanonicalPath());
	
		return true;
	}
	
	

	/**
	 * Paint function for the Instruction
	 * @param g - graphics context for the current JPane
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		// Paint the area
	}

	public void setState(CmeState mCurState) throws Exception {
		m_CurState = mCurState;
		
		//Begin!
		try 
		{ 
			switch (m_CurState.getState()) 
			{
			case CmeState.STATE_FEEDBACK:
				//this.showFeedbackArea();
				
			case CmeState.STATE_INSTRUCTION:
				String instructionFile = m_CurState.getProperty("InstructionFile").toString();
				this.showInstructions(instructionFile);
				m_App.dmsg(8, "Instruction!");
				break;
				
			case CmeState.STATE_PROMPT:
				break;
				
			default: 
				m_App.dmsg(9, "Default state hit on Instruction Handler!");
			case CmeState.STATE_TEST:
			case CmeState.STATE_STUDY:
				this.setVisible(false);
				return;
			}
		}
		catch (IOException ex) {
			throw new Exception ("IO Error: " + ex.getMessage());
		}
		catch (Exception ex) {
			throw new Exception ("Failed to set Instruction State!");
		}

		m_App.dmsg(8, "Entering Instruction State!");		
		this.setVisible(true);
	}
}
