package pkgPriceCLE;

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

public class PCLE_ImageFactory
{
	/** Vector of PCLE_Image objects */
	private Vector m_vImages;
	
	//-------------------------------------------------
	/** Default constructor */
	//-------------------------------------------------
	public PCLE_ImageFactory()
	{
		// Create the vector to hold all images
		m_vImages = new Vector();
		
		// Open the ImageList.txt file
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;
		// Open the file
		try
		{
			instFile = new FileReader("Instructions/ImageList.txt");
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
			PCLE_Image img;
			
			// For each line in the file
			while((line = bufReader.readLine()) != null)
			{
				// Parse the name key and image name
				String[] strs = line.split(" ");
				// Create the image and add to the vector of images
				img = new PCLE_Image(strs[0], strs[1]);
				m_vImages.add(img);
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
	/** Get an image from the factory by reference name */
	//-------------------------------------------------------------
	public Image getImage(String refName)
	{
		for(int i=0; i<m_vImages.size(); i++)
		{
			PCLE_Image img = (PCLE_Image)m_vImages.elementAt(i);
			if(refName.compareTo(img.getReferenceName()) == 0)
			{
				return img.getImage();
			}
		}
		return null; // Oops!
	}
	
	//-------------------------------------------------------------
	/** Get an image from the factory by vector index */
	//-------------------------------------------------------------
	public Image getImage(int idx)
	{
		if((idx < 0) || (idx >= m_vImages.size())) return null;
		return ((PCLE_Image)m_vImages.elementAt(idx)).getImage();
	}
	
	//-------------------------------------------------------------
	/** Get the name of an image from the factory by vector index */
	//-------------------------------------------------------------
	public String getReferenceName(int idx)
	{
		if((idx < 0) || (idx >= m_vImages.size())) return null;
		return ((PCLE_Image)m_vImages.elementAt(idx)).getReferenceName();
	}
	//-------------------------------------------------------------
	/** Get the image vector. */
	//-------------------------------------------------------------
	public Vector getImagesVector()
	{
		return this.m_vImages;
	}

}
