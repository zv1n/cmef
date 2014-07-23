package pkgCMEF;

import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.util.Vector;
import javax.imageio.ImageIO;

import javax.swing.JOptionPane;

//=====================================================================
/** This class implements a CmePair which references an Image object 
*   for use in the experiment.
*   @author Terry Meacham; Dr. Rick Coleman
*/
//=====================================================================
public class CmePair
{
	/** Pointer to app instance */
	CmeApp m_App;
	
	/** Pointer to factory instance */
	CmePairFactory m_pFactory;
	
    /** Reference to the image icon */
	private Dimension[] m_ImageSize;

    /** Word name associated with this image */
    private String[]	m_sName;

    /** Name of this image file */
	private String[]	m_sFile;
    
    /** Point Value of the Image */
    private int 		m_iValue;
    
    /** Name Value of the Image */
    private String 		m_sValue;
    
    private Vector<String> m_sExtraInfo = new Vector<String>();
    
    //-----------------------------------------------------
    // The following variables are for storing information
    // during a test run for later printout.
        
	//-------------------------------------------------
	/** Default constructor */
	//-------------------------------------------------
	public CmePair(CmeApp app) {
		m_App = app;
		
		m_ImageSize = new Dimension[2];
		m_sName = new String[2];
		m_sFile = new String[2];
	}

	private String getFeedback(int idx, int scale) {
		if (m_sFile[idx] == null || m_sFile[idx].length() == 0)
			return m_sName[idx];
		
		int xsize = m_ImageSize[idx].width*scale/1000;
		int ysize = m_ImageSize[idx].height*scale/1000;
		CmeState state = m_App.getCurrentState();
		
		String border = (String) state.getProperty("BorderWidth");
		if (border == null)
			border="0";
		
		/** Display the image... */
		return "<img src=\"" + m_App.getImagePrefix() + m_sFile[idx] + "\" alt=\"" +
				m_sName[idx] + "\" width=\"" + Integer.toString(xsize) + "\" height=\"" + 
				Integer.toString(ysize) + "\" border=\"" + border + "\">";
	}

	public String getFeedbackA(int scale) {
		return getFeedback(0, scale);
	}
	
	public String getFeedbackB(int scale) {
		return getFeedback(1, scale);
	}
	
	public void setNameA(String name) {
		m_sName[0] = name;
	}
	
	public void setNameB(String name) {
		m_sName[1] = name;
	}

	public String getFileA() {
		return m_sFile[0];
	}
	
	public String getFileB() {
		return m_sFile[1];
	}

	public boolean setImageA(String file) {
		return setImage(0, file);
	}
	
	public boolean setImageB(String file) {
		return setImage(1, file);
	}
	
	private boolean setImage(int ind, String file) {
		if (ind > m_sFile.length)
			return false;

		m_sFile[ind] = file;

		try
		{
			File image = new File(file);
			if (image.exists() && !image.isDirectory()) {
				BufferedImage img = ImageIO.read(image);

				if (m_ImageSize[ind] == null)
					m_ImageSize[ind] = new Dimension();

				m_ImageSize[ind].setSize(img.getWidth(), img.getHeight());
			}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
			"Error: Unable to read image file: " + m_sFile[ind], 
			"Error Reading File", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	//-------------------------------------------------
	/** Get the image reference name */
	//-------------------------------------------------
	public String getNameA()
	{
		return m_sName[0];
	}

	//-------------------------------------------------
	/** Get the image reference name */
	//-------------------------------------------------
	public String getNameB()
	{
		return m_sName[1];
	}
	
	//-------------------------------------------------
	/** Get the pair value*/
	//-------------------------------------------------
	public int getPairValue()
	{
		return m_iValue;
	}
	
	//-------------------------------------------------
	/** Get the pair value*/
	//-------------------------------------------------
	public String getPairGroup()
	{
		return m_sValue;
	}
	
	//--------------------------------------------
	/** Set the value of this pair (2, 4, or 8) */
	//--------------------------------------------
	public void setPairValue(int val)
	{
		m_iValue = val;
	}
	
	//--------------------------------------------
	/** Set the value of this pair ("Simple", "Medium", "Hard") */
	//--------------------------------------------
	public void setPairGroup(String val)
	{
		m_sValue = val;
	}

	public void addExtraInfo(String val) {
		if (val != null)
			m_sExtraInfo.add(val);
	}
	
	public String getExtraInfo(int val) {
		if (val < 0 || val >= m_sExtraInfo.size())
			return null;
		return m_sExtraInfo.get(val);
	}

	public Object getExtraInfoVector() {
		return m_sExtraInfo;
	}
}
