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
	
	private int m_iResolution;
	
	/** Millisecond point that the clock started */
	private int m_iLast;
	
	/** Current item being viewed */
	private String m_sItem;
	
	private Timer m_tTimer;
	
	private CmeEventResponse m_cResponse;
	private ActionListener m_tPerformer;

  private CmeApp m_aApp;
	
	private boolean m_bCountDown;
	private boolean m_bEnable;
	
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public CmeClock(CmeApp app)
	{
    m_aApp = app;
		final CmeClock sclock = this;
		//System.out.println("Start...");
		this.setSize(45, 35);
		// Creater sets the location
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		this.setVisible(true);
		
		m_bCountDown = false;
		m_bEnable = false;
		
		m_tPerformer = new ActionListener() {
			CmeClock clock = sclock;
			public void actionPerformed(ActionEvent evt) {
				if (clock != null && clock.isEnabled())
					clock.tick();
			}
		};
        
		m_tTimer = new Timer(0x1, m_tPerformer);//, taskPerformer);
		m_tTimer.setRepeats(true); // Run till done
		m_tTimer.start();
		
		m_iLast = 0;

    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "New Clock.");
	}

	//---------------------------------------------------
	/** Get the time left on the clock */
	//---------------------------------------------------
	public int getTimeRemaining()
	{
		return m_iTimeLimit - m_iCurTime;
	}
	
	public void setCountDown(boolean b) {
		m_bCountDown = b;
	}
	
	/* Set Limit */
	public void setTimeLimit(int ms) {
		m_iTimeLimit = ms;
		setEnable(false);
	}
	
	public void setResolution(int ms) {
		m_iResolution = ms;
		m_tTimer.stop();
		m_tTimer.setDelay(m_iResolution);
		m_tTimer.start();
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
		m_iCurTime += m_iResolution;
		if (isComplete() && m_cResponse != null) {
      m_aApp.dmsg(CmeApp.DEBUG_RESPONSES, "tick(): Clock Response Triggering.");
			m_cResponse.Respond();
			paint(this.getGraphics());
		} else if (m_iCurTime%(1000/m_iResolution) == 0)
			paint(this.getGraphics());
	}
	
	public boolean reset() {
    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Reset Clock.");
		m_iCurTime = 0;
    paint(this.getGraphics());
		return false;
	}
	
	public void complete() {
		m_iCurTime = m_iTimeLimit;
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
		
		if (m_bCountDown) {
			display = m_iTimeLimit - m_iCurTime;
			if (display <= 0)
				g.setColor(Color.RED);
		}

    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Paint Clock: " + String.valueOf(display));
		
		if (display > 0)
			display += 999;
		display /= 1000;

    String disp_str = String.valueOf(display);
    float offset = disp_str.length() * 0.075f;

    g.drawString(disp_str, (int)((0.50-offset)*this.getWidth()), 26);
	}

	public String getElement() {
		return m_sItem;
	}
	
	public boolean start(String item) {
		if (isComplete())
			return false;

    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Start Clock.");
		
		m_sItem = item;
		m_iLast = m_iCurTime;
		
		if (isEnabled()) {
			//System.out.println("Time:" + m_iCurTime + "\nLimit: " + m_iTimeLimit + "\nStarted Running Clock");
			return true;
		}
		
		setEnable(true);
		//System.out.println("Time:" + m_iCurTime + "\nLimit: " + m_iTimeLimit);
		return true;
	}

	public int stop() {
		setEnable(false);
    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Stop Clock.");
		//System.out.println("Time:" + m_iCurTime + "\nLimit: " + m_iTimeLimit);
		return (m_iCurTime - m_iLast);
	}
	
	public int getElapsedTime() {
		return (m_iCurTime - m_iLast);
	}
	
	public boolean setEventResponse(CmeEventResponse response) {
		m_cResponse = response;
		return (m_cResponse != null);
	}

	public boolean willComplete(int item) {
		return (m_iCurTime > (m_iTimeLimit-1000));
	}
}
