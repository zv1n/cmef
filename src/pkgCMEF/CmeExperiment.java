package pkgCMEF;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

//====================================================================
/** CmeExperiment
 *  <P>Purpose: This panel displays 2 rows of 3 images for the
 *  Price Chinese Learning Experiment.
 *  @author Dr. Rick Coleman
 *  @version 1.0
 *  Date: January, 2009
 */
//===================================================================

@SuppressWarnings("serial")
public class CmeExperiment extends JPanel implements MouseListener
{
	/** The parent application JFrame */
	private CmeApp m_App;

	/** The image factory */
	private CmeImageFactory m_ImageFactory;
		
	//--------------------------------------------
	// Default constructor
	//--------------------------------------------
	public CmeExperiment(CmeApp parent)
	{
		this.m_App = parent;
		
		this.setSize(parent.getSize());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		
		this.addMouseListener(this);
	}

	//--------------------------------------------
	/** Get a reference to the parent JFrame */
	//--------------------------------------------
	public CmeApp getParentFrame()
	{
		return m_App;
	}
	
	//--------------------------------------------
	/** Override the paint() method */
	//--------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
		// Paint the area
	}

	//-------------------------------------------------------
	/** Do testing */
	//-------------------------------------------------------
	public void doTesting(int trial, int[] group1, int[] group2)
	{
	}
	
	//-------------------------------------------------------
	/** Set a reference to the image factory */
	//-------------------------------------------------------
	public void setImageFactory(CmeImageFactory iFact)
	{
		m_ImageFactory = iFact;
	}
		
		//-------------------------------------------------------
	/** Implement the mouseClicked function of MouseListener*/
	//-------------------------------------------------------
	public void mouseClicked(MouseEvent e)
	{
		// Ignore this event - handle clicks in mouseReleased
	}
	
	//-------------------------------------------------------
	/** Implement the mouseEntered function of MouseListener*/
	//-------------------------------------------------------
	public void mouseEntered(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mouseExited function of MouseListener*/
	//-------------------------------------------------------
	public void mouseExited(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mousePressed function of MouseListener*/
	//-------------------------------------------------------
	public void mousePressed(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mouseReleased function of MouseListener*/
	//-------------------------------------------------------
	public void mouseReleased(MouseEvent e)
	{
	}
}