/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgCMEF;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author terrymeacham
 */
public class CmeTimer {
	
	/** Timer duration */
	private int m_iMilliseconds;
	
	/** Event response */
	private CmeEventResponse m_Response;

  private CmeApp m_aApp;
	
	/** Timer */
	private Timer m_jTimer;
	
	/** Constructor */
	public CmeTimer(CmeApp app) {
    m_aApp = app;
		m_iMilliseconds = 0;
		m_Response = null;
		m_jTimer = new Timer();

    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "New Timer.");
	}
	
	public int getDelay() {
		return m_iMilliseconds;
	}
	
	/**
	 * Returns the validity of this timer.
	 * @return true if the timer is valid; return false
	 */
	public boolean isValid() {
		return (m_Response != null && m_iMilliseconds > 0);
	}
	
	/**
	 * Sets the CmeEventResponse to be undertaken by the timer.
	 * @param response - event response to trigger
	 * @return true if the response is valid; else false
	 */
	public boolean setResponse(CmeEventResponse response) {
		if (response == null)
			return false;
		m_Response = response;
		return true;
	}
	
	/** Set timer delay be a string 
	 * @param delay - ##{ms,s)
	 * @return true if valid; false else
	 */
	public boolean setDelayByString(String delay) {
		
		try {
			m_iMilliseconds = CmeTimer.getDelayStringValue(delay);
		} catch (Exception ex) {
			return false;
		}
		if (m_iMilliseconds < 0)
			return false;
		return true;
	}
	
	public static int getDelayStringValue(String delay) {
		int multiplier = 1000;
		delay = delay.trim().toLowerCase();
		
		if (delay.length() < 1)
			return -1;
		
		if (delay.endsWith("ms"))
			multiplier = 1;
		
		for(int x=0; x<delay.length(); x++) {
			if(!Character.isDigit(delay.charAt(x))) {
				delay = delay.substring(0, x);
			}
		}
		
		return (Integer.valueOf(delay) * multiplier);
	}
	
	public void start() {
    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Start Timer.");
		TimerTask task = new TimerTask() {
			CmeEventResponse response = m_Response;
			@Override
			public void run() {
        m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Response Event!");
				if (response != null)
					response.Respond();
			}
		};
		
    m_aApp.dmsg(CmeApp.DEBUG_TIMERS, "Scheduling timer: " + String.valueOf(m_iMilliseconds));
		m_jTimer.schedule(task, m_iMilliseconds);
	}
	
	public void restart() {
		m_jTimer.cancel();	
		m_jTimer = new Timer();
		start();
	}
	
	public void stop() {
		m_jTimer.cancel();
	}
}
