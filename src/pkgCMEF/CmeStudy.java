package pkgCMEF;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;


@SuppressWarnings("serial")
public class CmeStudy extends JPanel implements MouseListener
{
	/** The parent application JFrame */
	private CmeApp m_App;

	/** The image factory */
	@SuppressWarnings("unused")
	private CmePairFactory m_PairFactory;

	/** Current state */
	private CmeState m_CurState;
	
	/** Button to close the window */
	private JButton m_bClose;
	
	/** HtmlView of the window */
	private CmeHtmlView m_HtmlView;
	
	/** Scrolling Window... */
	private JScrollPane m_ScrollPane;
	
	/** Property hash mapping */
	private HashMap<String, Object> m_hProperties = new HashMap<String, Object>();
	
	/** The content of the study file */
	private String m_Content;
	
	private CmeLimit m_cLimit;
	
	private boolean m_bStudy;

	//--------------------------------------------
	// Default constructor
	//--------------------------------------------
	public CmeStudy(CmeApp parent)
	{
		
		this.m_App = parent;
		
		this.setSize(parent.getSize());
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		
		m_HtmlView = new CmeHtmlView(parent, null);
		
		AncestorListener aListener = new AncestorListener() {

			@Override
			public void ancestorRemoved(AncestorEvent arg0) {
			}

			@Override
			public void ancestorAdded(AncestorEvent arg0) {
			}

			@Override
			public void ancestorMoved(AncestorEvent e) {
				adjustLayout();
			}
		};
		/*
		 * m_HtmlView.setBorder(BorderFactory
		 * .createBevelBorder(BevelBorder.LOWERED));
		 */

		m_ScrollPane = new JScrollPane(m_HtmlView);
		m_ScrollPane.addAncestorListener(aListener);
		this.add(m_ScrollPane);
		
		m_bClose = new JButton();
		
		this.addMouseListener(this);
	}

	public void setLimit(CmeLimit limit) {
		m_cLimit = limit;
	}
	
	/**
	 * Adjust all component present for the current window size.
	 * */
	public void adjustLayout() {
		adjustHtmlView();
		adjustCloseButton();
	}

	/**
	 * Adjust the Next Button for the current window size.
	 */
	private void adjustCloseButton() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = new Dimension(128, 24);
		Point location = new Point(newSz.width, newSz.height);

		location.x -= border.width + dimensions.width;
		location.y -= border.width + dimensions.height;

		m_bClose.setSize(dimensions);
		m_bClose.setLocation(location);
	}

	/**
	 * Adjust the HTML View for the current window size.
	 */
	private void adjustHtmlView() {
		Dimension newSz = this.getSize();

		Dimension border = new Dimension(10, 10);
		Dimension dimensions = (Dimension) newSz.clone();
		Point location = new Point(border.width, border.height);

		int lower_offset = border.height + 24;

		if (m_bClose.isVisible()) {
			dimensions.height = newSz.height - (border.height * 2 + lower_offset);
		} else {
			dimensions.height = newSz.height - (border.height * 2);
		}
		dimensions.width -= border.width * 2;

		m_HtmlView.setSize(dimensions);
		m_ScrollPane.setSize(dimensions);
		m_ScrollPane.setLocation(location);
	}
	
	//-------------------------------------------------------
	/** Set a reference to the image factory */
	//-------------------------------------------------------
	public void setPairFactory(CmePairFactory iFact)
	{
		m_PairFactory = iFact;
	}
		
	//--------------------------------------------
	/** Get a reference to the parent JFrame */
	//--------------------------------------------
	public CmeApp getParentFrame()
	{
		return m_App;
	}
	
	//--------------------------------------------
	/** Override the paint() method */
	//--------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
		// Paint the area
	}
	
	/** Set property to display */
	public void setProperty(String name, String value) {
		m_hProperties.put(name, value);
	}
	
	/** Get property to display */
	public String getProperty(String name) {
		return (String)m_hProperties.get(name);
	}
	
	public void setContent(String content) throws Exception {
		m_Content = content;
		CmeApp.translateString(m_hProperties, content, false);
		m_HtmlView.setContent(content, null);
	}
	
	//-------------------------------------------------------
	/** Implement the mouseClicked function of MouseListener*/
	//-------------------------------------------------------
	public void mouseClicked(MouseEvent e)
	{
		// Ignore this event - handle clicks in mouseReleased
	}
	
	//-------------------------------------------------------
	/** Implement the mouseEntered function of MouseListener*/
	//-------------------------------------------------------
	public void mouseEntered(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mouseExited function of MouseListener*/
	//-------------------------------------------------------
	public void mouseExited(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mousePressed function of MouseListener*/
	//-------------------------------------------------------
	public void mousePressed(MouseEvent e)
	{
		// Ignore this event
	}
	//-------------------------------------------------------
	/** Implement the mouseReleased function of MouseListener*/
	//-------------------------------------------------------
	public void mouseReleased(MouseEvent e)
	{
	}

	public void setState(CmeState mCurState) {
		m_CurState = mCurState;		
	}
}
