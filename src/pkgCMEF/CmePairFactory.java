package pkgCMEF;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

//=============================================================================
/**
 * This class implements the Image Factory which loads all the images
 * used in the experiment.
 * @author Rick Coleman
 * @version 1.0
 */
//=============================================================================

public class CmePairFactory
{
	/** Pointer to app instance */
	private CmeApp m_App;
	
	/** Vector of PCLE_Image objects */
	private Vector<CmePair> m_vPairs;
	
	//-------------------------------------------------
	/** Default constructor 
	 * @throws Exception */
	//-------------------------------------------------
	public CmePairFactory(CmeApp app)
	{
		m_App = app;
		
		// Create the vector to hold all images
		m_vPairs = new Vector<CmePair>();
		
		// Open the ImageList.txt file
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;
		// Open the file
		try
		{
			instFile = new FileReader("Instructions/CHIPS5/DataList.txt");
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		{
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to open DataList.txt file", 
					"Error Opening File", JOptionPane.ERROR_MESSAGE);
			System.out.println(ClassLoader.getSystemClassLoader().getSystemResource(".").toString());
			System.exit(1);
			return;
		}
		// Read the text strings and add them to the text area
		try
		{
			bufReader = new BufferedReader(instFile);
			CmePair img;
			
			// For each line in the file
			while((line = bufReader.readLine()) != null)
			{
				if (line.trim().charAt(0) == '#')
					continue;
				int val = 0;
				// Parse the name key and image name
				String[] strs = line.split(",");
				try {
					val = Integer.valueOf(strs[0]);
				} catch(Exception ex) {
					System.out.println("Failed to parse image value:" + line);					
				}
				// Create the image and add to the vector of images
				img = new CmePair(m_App);
				img.setPairValue(val);
				img.setPairValueString(strs[1]);
				img.setNameA(strs[2]);
				img.setImageA(strs[3]);
				img.setNameB(strs[4]);
				
				/* if there is nothing between the last ',' and the new line, then the last
				 * array element will get culled.
				 */
				if (strs.length > 5)
					img.setImageB(strs[5]);
				
				
				m_vPairs.add(img);
			}
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to read ImageList.txt file", 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	//-------------------------------------------------------------
	/** Get an image from the factory by vector index */
	//-------------------------------------------------------------
	public Image getImageA(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getImageA();
	}
	
	//-------------------------------------------------------------
	/** Get an image from the factory by vector index */
	//-------------------------------------------------------------
	public Image getImageB(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getImageB();
	}

	//-------------------------------------------------------------
	/** Get the name of an image from the factory by vector index */
	//-------------------------------------------------------------
	public String getNameA(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getNameA();
	}

	//-------------------------------------------------------------
	/** Get the name of an image from the factory by vector index */
	//-------------------------------------------------------------
	public String getNameB(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getNameB();
	}

	//-------------------------------------------------------------
	/** Get the name of an image from the factory by vector index */
	//-------------------------------------------------------------
	public String getFeedbackA(int idx, int scale)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return "<h1>Experiment Error: Please Notify Proctor</h1>";
		return ((CmePair)m_vPairs.elementAt(idx)).getFeedbackA(scale);
	}

	//-------------------------------------------------------------
	/** Get the name of an image from the factory by vector index */
	//-------------------------------------------------------------
	public String getFeedbackB(int idx, int scale)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return "<h1>Experiment Error: Please Notify Proctor (" + Integer.toString(idx) + ")</h1>";
		return ((CmePair)m_vPairs.elementAt(idx)).getFeedbackB(scale);
	}
	
	//-------------------------------------------------------------
	/** Get the string value of the image at index idx */
	//-------------------------------------------------------------
	public String getPairValue(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return String.valueOf(((CmePair)m_vPairs.elementAt(idx)).getPairValue());
	}
	
	//-------------------------------------------------------------
	/** Get the string value of the image at index idx */
	//-------------------------------------------------------------
	public String getPairValueString(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return String.valueOf(((CmePair)m_vPairs.elementAt(idx)).getPairValueString());
	}
	
	public int getCount() {
		return m_vPairs.size();
	}
	
	//-------------------------------------------------------------
	/** Get the image vector. */
	//-------------------------------------------------------------
	public Vector<CmePair> getImagesVector()
	{
		return this.m_vPairs;
	}

}
