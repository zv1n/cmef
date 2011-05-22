package pkgCMEF;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

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
	
	//----------------------------------------------------------------
	/** Default constructor */
	//----------------------------------------------------------------
	public CmeInstructions(CmeApp parent)
	{
		m_App = parent;
		
		this.setSize(parent.getSize());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setLayout(null);		
	}
	
	//----------------------------------------------------------------
	/** Display the instructions
	 *  @param fileName - name of instructions file to display
	 */
	//----------------------------------------------------------------
	public boolean showInstructions(String fileName)
	{
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;

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
}
