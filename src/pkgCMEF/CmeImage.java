package pkgCMEF;

import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JOptionPane;

//=====================================================================
/** This class implements a PCLE_Image which references an Image object 
*   for use in the experiment.
*   @author Terry Meacham; Dr. Rick Coleman
*/
//=====================================================================
public class CmeImage
{
    /** Reference to the image icon */
	private Image		m_Image;

    /** Reference name associated with this image */
    private String		m_sRefName;

    /** Name of this image file */
    @SuppressWarnings("unused")
	private String		m_sImageName;
    
    /** Point Value of the Image */
    private int 		m_iValue;
    
    //-----------------------------------------------------
    // The following variables are for storing information
    // during a test run for later printout.
        
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
}
