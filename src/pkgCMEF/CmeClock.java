package pkgCMEF;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.Timer;

//====================================================================
/** CmeClock
 *  <P>Purpose: This panel displays and counts down a clock for the
 *  CME Framework.
 *  @author Terry Meacham; Dr. Rick Coleman
 *  @version 1.1
 */
//===================================================================

@SuppressWarnings("serial")
public class CmeClock extends JPanel implements CmeLimit
{
	/** Initial time in milliseconds */
	private int m_iTimeLimit;
	
	/** Current time to display in milliseconds */
	private int m_iCurTime;
	
	private Timer m_tTimer;
	
	private CmeEventResponse m_cResponse;
	
	private boolean m_bCountDown;
	private boolean m_bEnable;
	
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public CmeClock()
	{
		final CmeClock sclock = this;
		System.out.println("Start...");
		this.setSize(45, 35);
		// Creater sets the location
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		this.setVisible(true);
		
		m_bCountDown = false;
		m_bEnable = false;
		
		ActionListener tPerformer = new ActionListener() {
			CmeClock clock = sclock;
			public void actionPerformed(ActionEvent evt) {
				if (clock != null && clock.isEnabled())
					clock.tick();
			}
		};
        
		m_tTimer = new Timer(0x1, tPerformer);//, taskPerformer);
		m_tTimer.setRepeats(true); // Run till done
		m_tTimer.start();
	}

	//---------------------------------------------------
	/** Get the time left on the clock */
	//---------------------------------------------------
	public int getTimeRemaining()
	{
		return m_iCurTime;
	}
	
	public void setCountDown(boolean b) {
		m_bCountDown = b;
	}
	
	/* Set Limit */
	public void setTimeLimit(int ms) {
		m_iTimeLimit = ms;
		setEnable(false);
	}
	
	/* Enable Counter */
	public void setEnable(boolean enable) {
		m_bEnable = enable;
	}
	
	/* Get whether the clock is enabled or not. */
	public boolean isEnabled() {
		return m_bEnable;
	}
	
	public void tick() {
		m_iCurTime++;
		if (isComplete() && m_cResponse != null)
			m_cResponse.Respond();
		if (m_iCurTime%1000 == 0)
			paint(this.getGraphics());
	}
	
	public boolean reset() {
		m_iCurTime = 0;
		return false;
	}
	
	public boolean isComplete() {
		return (m_iCurTime >= m_iTimeLimit);
	}
	
	//---------------------------------------------------
	/** Override the paint() function to draw the time
	 *  hack in seconds
	 */
	//---------------------------------------------------
	public void paint(Graphics g) {
		if(!this.isVisible()) return; // Don't paint if we are not visible
		// Paint the border
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight()); 
		// Paint the area
		g.setColor(Color.WHITE);
		g.fillRect(1, 1, this.getWidth()-2, this.getHeight()-2); 
		// Print the seconds
		g.setFont(CmeApp.SysTitleFontB);
		g.setColor(Color.BLACK);
		
		int display = m_iCurTime;
		
		if (m_bCountDown)
			display = m_iTimeLimit - m_iCurTime;
		
		display /= 1000;
			// Draw the 10s if needed	
		g.drawString(String.valueOf(display / 10), (int)(0.20*this.getWidth()), 26);
			// Draw the 1s
		g.drawString(String.valueOf(display % 10), (int)(0.55*this.getWidth()), 26);
	}

	public boolean start(int item) {
		if (isComplete())
			return false;
		setEnable(true);
		System.out.println("Time:" + m_iCurTime + "\nLimit: " + m_iTimeLimit);
		return true;
	}

	public boolean stop(int item) {
		setEnable(false);
		System.out.println("Time:" + m_iCurTime + "\nLimit: " + m_iTimeLimit);
		return true;
	}
	
	public boolean setEventResponse(CmeEventResponse response) {
		m_cResponse = response;
		return (m_cResponse != null);
	}

	public boolean willComplete(int item) {
		return (m_iCurTime > (m_iTimeLimit-1000));
	}
}
