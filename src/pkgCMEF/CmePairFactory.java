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
	public CmePairFactory(CmeApp app, String file)
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
			instFile = new FileReader(file);
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		{
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to open " + file + " file", 
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
				img.setPairGroup(strs[1]);
				img.setNameA(strs[2]);
				img.setImageA(strs[3]);
				img.setNameB(strs[4]);
				String ib = null;
				
				if (strs.length > 5) {
					ib = strs[5];
				} else {
					ib = "";
				}
				
				img.setImageB(ib);
				
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "V", strs[0]);
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "D", strs[1]);
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "A", strs[2]);
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "IA", strs[3]);
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "B", strs[4]);
				m_App.setProperty(Integer.toString(m_vPairs.size()) + "IB", ib);
				
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
	/** Get a file from the factory by vector index */
	//-------------------------------------------------------------
	public String getFileA(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getFileA();
	}
	
	//-------------------------------------------------------------
	/** Get a file from the factory by vector index */
	//-------------------------------------------------------------
	public String getFileB(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return ((CmePair)m_vPairs.elementAt(idx)).getFileB();
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
	/** Get the integer value of idx */
	//-------------------------------------------------------------
	public int getIntValue(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return -1;
		return ((CmePair)m_vPairs.elementAt(idx)).getPairValue();
	}
	
	//-------------------------------------------------------------
	/** Get the string value of the image at index idx */
	//-------------------------------------------------------------
	public String getPairGroup(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return String.valueOf(((CmePair)m_vPairs.elementAt(idx)).getPairGroup());
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
