package pkgCMEF;

import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JOptionPane;

//=====================================================================
/** This class implements a CmePair which references an Image object 
*   for use in the experiment.
*   @author Terry Meacham; Dr. Rick Coleman
*/
//=====================================================================
public class CmePair
{
    /** Reference to the image icon */
	private Image[]		m_Image;

    /** Word name associated with this image */
    private String[]	m_sName;

    /** Name of this image file */
	private String[]	m_sFile;
    
    /** Point Value of the Image */
    private int 		m_iValue;
    
    //-----------------------------------------------------
    // The following variables are for storing information
    // during a test run for later printout.
        
	//-------------------------------------------------
	/** Default constructor */
	//-------------------------------------------------
	public CmePair() {
		m_Image = new Image[2];
		m_sName = new String[2];
		m_sFile = new String[2];
	}
	
	public void SetNameA(String name) {
		m_sName[0] = name;
	}
	
	public void SetNameB(String name) {
		m_sName[1] = name;
	}

	public boolean SetImageA(String file) {
		return SetImage(0, file);
	}
	
	public boolean SetImageB(String file) {
		return SetImage(1, file);
	}
	
	private boolean SetImage(int ind, String file) {
		if (ind > m_sFile.length)
			return false;
		
		m_sFile[ind] = file;
		
        Toolkit tk = Toolkit.getDefaultToolkit();
        try
        {
	        m_Image[ind] = tk.createImage(m_sFile[ind]);
	        // Wait for it to load
	        while(m_Image[ind].getWidth(null) == 0);
        }
        catch(Exception e)
        {
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to read image file" + m_sFile[ind], 
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
	/** Get the image*/
	//-------------------------------------------------
	public Image getImageA()
	{
		return m_Image[0];
	}
	
	//-------------------------------------------------
	/** Get the image*/
	//-------------------------------------------------
	public Image getImageB()
	{
		return m_Image[1];
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
}
