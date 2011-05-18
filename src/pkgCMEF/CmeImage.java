package pkgCMEF;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;

//=====================================================================
/** This class implements a PCLE_Image which references an Image object 
*   for use in the experiment.
*   @author Dr. Rick Coleman
*/
//=====================================================================
public class CmeImage
{

    /** Reference to the image icon */
    private Image				m_Image;

    /** Reference name associated with this image */
    private String				m_sRefName;

    /** Name of this image file */
    private String				m_sImageName;
    
    //-----------------------------------------------------
    // The following variables are for storing information
    // during a test run for later printout.
    
    /** Ease of Learning presentation order */
    private int					m_iEOLPresentationOrder;
    
    /** User's ease of learning estimation */
    private int					m_iEOLRate;
    
    /** Test presentation order */
    private int					m_iTestPresentationOrder;
    
    /** User's response in the test */
    private String				m_sUserResponse;

    /** Was user's answer correct */
    private boolean				m_bCorrect;
    
    /** Trial this image was in (1 or 2) */
    private int					m_iTrial;
    
    /** Image Value (2, 4, or 8)*/
    private int					m_iValue;
    
    /** Grid number this image was on (1, 2, or 3) */
    private int					m_iGrid;
    
    /** Row this image was on (1=top, 2=middle, 3=bottom) */
    private int					m_iRow;
    
    /** Column this image was on (1=left, 2=middle, 3=right) */
    private int					m_iColumn;
    
    /** Number of times this was studied */
    private int					m_iTimesStudied;
    
    /** Vector of Integers giving time in seconds studied each time */
    private Vector				m_vStudyTimes;
    
    /** Vector of the order in which each study time occurred */
    private Vector				m_vStudyTimesOrder;
    
    /** Total time studies */
    private int					m_iTotalStudyTime;
    
	//-------------------------------------------------
	/** Default constructor */
	//-------------------------------------------------
	public CmeImage(String refName, String imgName)
	{
		m_sRefName = refName;
		m_sImageName = imgName;
		m_Image = null;
		
        Toolkit tk = Toolkit.getDefaultToolkit();
        try
        {
	        m_Image = tk.createImage(imgName);
	        // Wait for it to load
	        while(m_Image.getWidth(null) == 0);
        }
        catch(Exception e)
        {
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to read image file" + imgName, 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
        	
        }
        
        // ------------- Init all the variables ------------------
        // Test presentation order 
        m_iTestPresentationOrder = 0; 
        // Ease of Learning presentation order 
        m_iEOLPresentationOrder = 0; 
        // User's ease of learning estimation
        m_iEOLRate = 0;
        // User's response in the test 
        m_sUserResponse = "";
        // Was user's answer correct
        m_bCorrect = false;
        // Trial this image was in (1 or 2)
        m_iTrial = 0;
        // Grid number this image was on (1, 2, or 3)
        m_iGrid = 0;
        // Row this image was on (1=top, 2=bottom)
        m_iRow = 0;
        // Value of this particular Image (determined by condition)
        m_iValue = 0;
        // Column this image was on (1=left, 2=middle, 3=right) 
        m_iColumn = 0;
        // Number of times this was studied
        m_iTimesStudied = 0;
        // Vector of Integers giving time in seconds studied each time
        m_vStudyTimes = new Vector();
        // Vector of Integers giving order for each study time in this set
        m_vStudyTimesOrder = new Vector();
        // Total time studies
        m_iTotalStudyTime = 0;
	}

	//-------------------------------------------------
	/** Get the image reference name */
	//-------------------------------------------------
	public String getReferenceName()
	{
		return m_sRefName;
	}

	//-------------------------------------------------
	/** Get the image*/
	//-------------------------------------------------
	public Image getImage()
	{
		return m_Image;
	}
	
	//-------------------------------------------------
	/** Get the image value*/
	//-------------------------------------------------
	public int getImageValue()
	{
		return m_iValue;
	}
	
	//--------------------------------------------
	/** Set the value of this image (2, 4, or 8) */
	//--------------------------------------------
	public void setImageValue(int val)
	{
		m_iValue = val;
	}

	//-----------------------------------------------------------------
	//              GET AND SET FUNCTIONS FOR ALL VARIABLES
	//-----------------------------------------------------------------
	//--------------------------------------------
	// Set the ease of learning presentation order
	//--------------------------------------------
	public void setEOLPresentationOrder(int val)
	{
		m_iEOLPresentationOrder = val;
	}
	
	//--------------------------------------------
	// Get the ease of learning presentation order
	//--------------------------------------------
	public int getEOLPresentationOrder()
	{
		return m_iEOLPresentationOrder;
	}
	
	//--------------------------------------------
	// Set the users ease of learning rating
	//--------------------------------------------
	public void setEOLRate(int val)
	{
		m_iEOLRate = val;
	}
	
	//--------------------------------------------
	// Get the users ease of learning rating
	//--------------------------------------------
	public int getEOLRate()
	{
		return m_iEOLRate;
	}
	
	//--------------------------------------------
	// Set the test sequence presentation order
	//--------------------------------------------
	public void setTestPresentationOrder(int val)
	{
		m_iTestPresentationOrder = val;
	}
	
	//--------------------------------------------
	// Get the test sequence presentation order
	//--------------------------------------------
	public int getTestPresentationOrder()
	{
		return m_iTestPresentationOrder;
	}
	
	//--------------------------------------------
	// Set the users response in the test
	//--------------------------------------------
	public void setUserAnswer(String val)
	{
		m_sUserResponse = val;
		if(m_sUserResponse.compareToIgnoreCase(m_sRefName) == 0)
			m_bCorrect = true;
		else
			m_bCorrect = false;
	}
	
	//--------------------------------------------
	// Get the users response in the test
	//--------------------------------------------
	public String getUserAnswer()
	{
		return m_sUserResponse;
	}
	
	//--------------------------------------------
	// Set the users response correct flag
	//--------------------------------------------
	public boolean getUserAnswerCorrect()
	{
		return m_bCorrect;
	}
	
	//--------------------------------------------
	// Set the trial this image was in (1 or 2)
	//--------------------------------------------
	public void setTrial(int val)
	{
		m_iTrial = val;
	}
	
	//--------------------------------------------
	// Get the trial this image was in (1 or 2)
	//--------------------------------------------
	public int getTrial()
	{
		return m_iTrial;
	}
	
	//--------------------------------------------
	// Set the grid location of this image
	//--------------------------------------------
	public void setGridLocation(int grid, int row, int col)
	{
		m_iGrid = grid;
		m_iRow = row;
		m_iColumn = col;
	}
	
	//--------------------------------------------
	// Get the grid this image was in
	//--------------------------------------------
	public int getGrid()
	{
		return m_iGrid;
	}

	//--------------------------------------------
	// Get the row this image was in
	//--------------------------------------------
	public int getRow()
	{
		return m_iRow;
	}

	//--------------------------------------------
	// Get the column this image was in
	//--------------------------------------------
	public int getColumn()
	{
		return m_iColumn;
	}

	//--------------------------------------------
	// Get the trial this image was in (1 or 2)
	//--------------------------------------------
	public int getTimesStudied()
	{
		return m_iTimesStudied;
	}
	
	//--------------------------------------------
	// Add a study time
	//--------------------------------------------
	public void addStudyTime(int sTime, int sOrder)
	{
		// Make sure it is at least 1 second
		int t = (sTime > 0) ? sTime : (sTime + 1);
		m_iTimesStudied++;
		Integer st = new Integer(t);
		m_vStudyTimes.add(st);
		// Add the order for this study also
		st = new Integer(sOrder);
		m_vStudyTimesOrder.add(st);
		m_iTotalStudyTime+=t;
	}
	
	//--------------------------------------------
	// Get the trial this image was in (1 or 2)
	//--------------------------------------------
	public Vector getStudyTimeVector()
	{
		return m_vStudyTimes;
	}
	
	//--------------------------------------------
	// Get the trial this image was in (1 or 2)
	//--------------------------------------------
	public Vector getStudyOrderVector()
	{
		return m_vStudyTimesOrder;
	}
	
	//--------------------------------------------
	// Get the total study time
	//--------------------------------------------
	public int getTotalStudyTime()
	{
		return m_iTotalStudyTime;
	}
}
