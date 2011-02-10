package pkgPriceCLE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

//====================================================================
/** PCLE_ClockPanel
 *  <P>Purpose: This panel displays and counts down a clock for the
 *  Price Chinese Learning Experiment.
 *  @author Dr. Rick Coleman
 *  @version 1.0
 *  Date: February, 2009
 */
//===================================================================

public class PCLE_ClockPanel extends JPanel
{
	/** Initial time in seconds */
	private int m_iInitTime;
	
	/** Current time to display in seconds */
	private int m_iCurTime;
	
	/** Drawing area left */
	private int m_iClockLeft;
	
	/** Drawing area top */
	private int m_iClockTop;
	
	/** Drawing area width */
	private int m_iClockWidth;
	
	/** Drawing area height */
	private int m_iClockHeight;
	
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public PCLE_ClockPanel()
	{
		this.setSize(45, 35);
		// Creater sets the location
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		this.setVisible(true);
		
		// Define the area in which to draw the clock face
		m_iClockLeft = 5;
		m_iClockTop = 5;
		m_iClockWidth = this.getWidth() - 10;
		m_iClockHeight = this.getHeight() - 10;
	}
	
	//---------------------------------------------------
	/** Set the initial time to display */
	//---------------------------------------------------
	public void setInitialTime(int timeInSec)
	{
		m_iInitTime = timeInSec;
		m_iCurTime = timeInSec;
		paint(getGraphics());
	}
	
	//---------------------------------------------------
	/** Decrement display time by 1 second and redraw. */
	//---------------------------------------------------
	public void decrementTime()
	{
		m_iCurTime--;
		paint(getGraphics());
	}
	
	//---------------------------------------------------
	/** Reset the current time to zero. */
	//---------------------------------------------------
	public void setTimeToZero()
	{
		m_iCurTime = 0;
		paint(getGraphics());
	}

	//---------------------------------------------------
	/** Get the time left on the clock */
	//---------------------------------------------------
	public int getTimeRemaining()
	{
		return m_iCurTime;
	}
	
	//---------------------------------------------------
	/** Override the paint() function to draw the time
	 *  hack in seconds
	 */
	//---------------------------------------------------
	public void paint(Graphics g)
	{
		if(!this.isVisible()) return; // Don't paint if we are not visible
		// Paint the border
		g.setColor(Color.DARK_GRAY);
		g.drawLine(this.getWidth()-1, 0, this.getWidth()-1, this.getHeight()-1);
		g.drawLine(this.getWidth()-1, this.getHeight()-1, 0, this.getHeight()-1);
		g.setColor(Color.WHITE);
		g.drawLine(0, 0, this.getWidth(), 0);
		g.drawLine(0, 0, 0, this.getHeight());
		// Paint the area
		g.setColor(Color.WHITE);
		g.fillRect(m_iClockLeft, m_iClockTop, m_iClockWidth, m_iClockHeight);
		// Print the seconds
		g.setFont(PriceCLE.SysTitleFontB);
		g.setColor(Color.BLACK);
		// Draw the 10s if needed
		if((m_iCurTime / 10) > 0)	
		{
			g.drawString(String.valueOf(m_iCurTime / 10), 10, 25);
		}
		// Draw the 1s
		g.drawString(String.valueOf(m_iCurTime % 10), 23, 25);
		
		
	
	}
}
