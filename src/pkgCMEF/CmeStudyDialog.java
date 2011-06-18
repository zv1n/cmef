package pkgCMEF;

import java.awt.Color;
import java.awt.Graphics;
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

@SuppressWarnings("serial")
public class CmeStudyDialog extends JDialog
{
	/** Parent panel */
	private CmeStudy m_Parent;
	
	/** Done studying button */
	private JButton m_DoneBtn;
	
	/** Main panel */
	private JPanel m_MainPanel;
	
	/** Image to draw */
	private CmePair m_cImage;
	
	//---------------------------------------------------
	/** Default constructor */
	//---------------------------------------------------
	public CmeStudyDialog(CmeStudy parent)
	{
		m_Parent = parent;
		
		this.setSize(parent.getSize());
		this.setLayout(null);
		this.setBackground(Color.LIGHT_GRAY);
		this.getContentPane().setLayout(null);
		this.setModal(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		m_MainPanel = new JPanel();
		m_MainPanel.setSize(m_Parent.getSize());
		m_MainPanel.setBackground(Color.LIGHT_GRAY);
		m_MainPanel.setLayout(null);
		this.getContentPane().add(m_MainPanel);
		
		m_DoneBtn = new JButton("Finished Studying");
		m_DoneBtn.setSize(300, 40);
		m_DoneBtn.setLocation(300, 425);
		
		ActionListener doneListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		m_DoneBtn.addActionListener(doneListener);
				
		m_MainPanel.add(m_DoneBtn);		
	}
	
	//----------------------------------------------------
	/** Set the PCLE_Image to display */
	//----------------------------------------------------
	public void setImage(CmePair img)
	{
		m_cImage = img;
	}

	//----------------------------------------------------
	/** Get the PCLE_Image displayed */
	//----------------------------------------------------
	public CmePair getImage()
	{
		return m_cImage;
	}

	//----------------------------------------------------
	/** Override the paint function */
	//----------------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
/*		Image img = m_cImage.getImage();
		// Paint the area
		g.setColor(Color.white);
		
//		g.drawImage(img, 40, m_iImgAreaY + 10, null);
		String str = m_cImage.getReferenceName();
		g.setFont(CmeApp.SysTitleFontB);
		g.setColor(Color.BLACK);
//		g.drawString(str, m_iImgAreaX+(m_iImgAreaWt / 2)+100, 
//				210);
//		g.drawString(String.valueOf(m_cImage.getImageValue()),
//				m_iImgAreaX+(m_iImgAreaWt / 2)+125,	245);
//		g.drawString(str, m_iImgAreaX+m_PImage.getImage().getWidth(null)+20, 
//				100);*/
	}
}
