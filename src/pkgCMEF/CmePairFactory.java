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
	/** Vector of PCLE_Image objects */
	private Vector<CmePair> m_vPairs;
	
	//-------------------------------------------------
	/** Default constructor */
	//-------------------------------------------------
	public CmePairFactory()
	{
		// Create the vector to hold all images
		m_vPairs = new Vector<CmePair>();
		
		// Open the ImageList.txt file
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;
		// Open the file
		try
		{
			instFile = new FileReader("Instructions/DataList.txt");
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		{
			JOptionPane.showMessageDialog(null, 
					"Error: Unable to open ImageList.txt file", 
					"Error Opening File", JOptionPane.ERROR_MESSAGE);
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
				
				// Parse the name key and image name
				String[] strs = line.split(",");
				// Create the image and add to the vector of images
				img = new CmePair();

				img.SetNameA(strs[0]);
				img.SetImageA(strs[1]);
				img.SetNameB(strs[2]);
				img.SetImageB(strs[3]);
				
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
	/** Get the string value of the image at index idx */
	//-------------------------------------------------------------
	public String getImageValue(int idx)
	{
		if((idx < 0) || (idx >= m_vPairs.size())) return null;
		return String.valueOf(((CmePair)m_vPairs.elementAt(idx)).getImageValue());
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
