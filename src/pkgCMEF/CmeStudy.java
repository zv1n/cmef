package pkgCMEF;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

//====================================================================
/** CmeStudy
 *  <P>Purpose: This panel displays one image and the English 
 *  translation for the Price Chinese Learning Experiment.
 *  @author Dr. Rick Coleman
 *  @version 1.0
 *  Date: March, 2009
 */
//===================================================================

public class CmeStudy extends JDialog
{
	/** Parent panel */
	private CmeExperiment m_App;
	
	/** Done studying button */
	private JButton m_DoneBtn;
	
	/** Main panel */
	private JPanel m_MainPanel;
	
	/** Image to draw */
	private CmeImage m_PImage;
	
	/** Width of image area */
	private int m_iImgAreaWt;
	
	/** Height of the image area */
	private int m_iImgAreaHt;
	
	/** Image area X coord */
	private int m_iImgAreaX;
	
	/** Image area Y coord */
	private int m_iImgAreaY;
	
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public CmeStudy(CmeExperiment parent)
	{
		m_App = parent;
		// Center this frame on the parent frame
		CmeApp parentFrame = m_App.getParentFrame();
		int parentXPos = parentFrame.getLocation().x;
		int parentYPos = parentFrame.getLocation().y;
		int parentWidth = parentFrame.getSize().width;
		int parentHeight = parentFrame.getSize().height;
		
		this.setSize(900, 600);
		this.setLocation((parentWidth-900)/2,0);
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		this.getContentPane().setLayout(null);
		this.setModal(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		m_MainPanel = new JPanel();
		m_MainPanel.setSize(900, 600);
		m_MainPanel.setLocation(0,0);
		m_MainPanel.setBackground(Color.LIGHT_GRAY);
		m_MainPanel.setLayout(null);
		this.getContentPane().add(m_MainPanel);
		
		m_DoneBtn = new JButton("Finished Studying");
		m_DoneBtn.setSize(300, 40);
		m_DoneBtn.setLocation(300, 425);
		m_DoneBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_App.stopClock();
						
						m_App.getParentFrame().postStatusMessage(" - Done studying selected character." +
								" Clock = " + m_App.getClock().getTimeRemaining() + " seconds.", true);
						setVisible(false);
					}
				});
				
		m_MainPanel.add(m_DoneBtn);
		
		m_iImgAreaWt = 880;
		m_iImgAreaHt = 380;
		m_iImgAreaX = 10;
		m_iImgAreaY = 10;
		
	}
	
	//----------------------------------------------------
	/** Set the PCLE_Image to display */
	//----------------------------------------------------
	public void setImage(CmeImage img)
	{
		m_PImage = img;
	}

	//----------------------------------------------------
	/** Get the PCLE_Image displayed */
	//----------------------------------------------------
	public CmeImage getImage()
	{
		return m_PImage;
	}

	//----------------------------------------------------
	/** Override the paint function */
	//----------------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
		Image img = m_PImage.getImage();
		// Paint the area
		g.setColor(Color.white);
		g.fillRect(m_iImgAreaX, m_iImgAreaY, m_iImgAreaWt, m_iImgAreaHt);
		g.drawImage(img, 
				m_iImgAreaX + (((m_iImgAreaWt / 2) - img.getWidth(null)) / 2), 
				m_iImgAreaY + ((m_iImgAreaHt - img.getHeight(null)) / 2), null);
//		g.drawImage(img, 40, m_iImgAreaY + 10, null);
		String str = m_PImage.getReferenceName();
		g.setFont(CmeApp.SysTitleFontB);
		g.setColor(Color.BLACK);
		g.drawString(str, m_iImgAreaX+(m_iImgAreaWt / 2)+100, 
				210);
		g.drawString(String.valueOf(m_PImage.getImageValue()),
				m_iImgAreaX+(m_iImgAreaWt / 2)+125,	245);
//		g.drawString(str, m_iImgAreaX+m_PImage.getImage().getWidth(null)+20, 
//				100);
	}
}
