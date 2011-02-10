package pkgPriceCLE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

//====================================================================
/** PCLE_InstructionsPanel
 *  <P>Purpose: This panel displays a JTextArea object for display
 *  instructions.
 *  @author Dr. Rick Coleman
 *  @version 1.0
 *  Date: January, 2009
 */
//===================================================================

public class PCLE_InstructionsPanel extends JPanel
{
	/** Parent frame */
	PriceCLE m_Parent;
	
	/** Text area width */
	private int m_iTextFrameWidth;
	
	/** Text area height */
	private int m_iTextFrameHeight;
	
	/** Text frame left X */
	private int m_iTextFrameLeftX;
	
	/** Text frame top Y */
	private int m_iTextFrameTopY;
	
	/** Starting Y location for text */
	private int m_iTopY;
	
	/** Starting X location for all lines of text */
	private int m_iLeftX;

	/** Current Y location of next line to draw */
	private int m_iCurY;
	
	/** Vector of string currently being displayed */
	private Vector m_vTextStrings;
	
	/** The image factory */
	private PCLE_ImageFactory m_ImageFactory;
	
	/** The text area where instructions are displayed */
//	private JTextArea m_Instructions;

	/** Scroll pane to hold the instructions */
//	private JScrollPane m_InsScrollPane;
	
	//----------------------------------------------------------------
	/** Default constructor */
	//----------------------------------------------------------------
	public PCLE_InstructionsPanel(int width, int height, PriceCLE parent)
	{
		m_Parent = parent;
		
		this.setSize(width, height);
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
//		this.setBackground(Color.blue);
		this.setLayout(null);
		
		m_vTextStrings = new Vector();
		
		// Define the area in which to draw the text
		m_iTextFrameWidth = width - 35;
		m_iTextFrameHeight = height - 60;
		m_iTextFrameLeftX = 15;
		m_iTextFrameTopY = 40;
		
		m_iTopY = 60;
		m_iLeftX = 21;
		
		JLabel tempLbl = new JLabel("Instructions:");
		tempLbl.setFont(PriceCLE.SysTitleFontB);
		tempLbl.setSize(200, 25);
		tempLbl.setLocation(25, 5);
		this.add(tempLbl);
		
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

		// Clear current strings
		m_vTextStrings.removeAllElements();
		
		// Open the file
		try
		{
			instFile = new FileReader(fileName);
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to open " + fileName, 
					"Error Opening File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// Read the text strings and add them to the text area
		try
		{
			bufReader = new BufferedReader(instFile);
			while((line = bufReader.readLine()) != null)
			{
				m_vTextStrings.add(line);
			}
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to read " + fileName, 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		this.paint(this.getGraphics());
		return true;
	}
	
	//----------------------------------------------------------------
	/** Override the paint method */
	//----------------------------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
		// Paint the area
		g.setColor(Color.white);
		g.fillRect(m_iTextFrameLeftX, m_iTextFrameTopY, m_iTextFrameWidth, m_iTextFrameHeight);
		m_iCurY = m_iTopY;
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY,
				m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY + m_iTextFrameHeight);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY + m_iTextFrameHeight, 
				m_iTextFrameLeftX, m_iTextFrameTopY + m_iTextFrameHeight);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY, 
				m_iTextFrameLeftX, m_iTextFrameTopY);
		g.drawLine(m_iTextFrameLeftX, m_iTextFrameTopY,
				m_iTextFrameLeftX, m_iTextFrameTopY + m_iTextFrameHeight);
		// Draw all the strings
		g.setColor(Color.black);
		g.setFont(PriceCLE.SysInstructionFont);
		for(int i=0; i<m_vTextStrings.size(); i++)
		{
			String str = (String)m_vTextStrings.elementAt(i);
			// See if it defines an image
			if(str.contains("IMAGE"))
			{
				String tempStr = str.substring(str.indexOf('<'), str.lastIndexOf('>'));
				String[] strs = tempStr.split(" ");
				// Get the image from the ImageFactory
				Image theImg = m_ImageFactory.getImage(strs[1]);
				// Draw the image
				if(theImg != null)
				{
					int X = (this.getWidth() - theImg.getWidth(null)) / 2;
					g.drawImage(theImg, X, m_iCurY, null);
					m_iCurY+=theImg.getHeight(null);
				}
				else
				{
					g.drawString("*** MISSING IMAGE  ***", m_iLeftX, m_iCurY);
					m_iCurY+=15;
				}
				// Reset the m_iCurY for the next text line
			}
			else
			{
				g.drawString((String)m_vTextStrings.elementAt(i), m_iLeftX, m_iCurY);
				m_iCurY+=15;
			}
		}
	}

	//-------------------------------------------------------
	/** Set a reference to the image factory */
	//-------------------------------------------------------
	public void setImageFactory(PCLE_ImageFactory iFact)
	{
		m_ImageFactory = iFact;
	}
}
