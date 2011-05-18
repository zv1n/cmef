package pkgCMEF;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

//====================================================================
/** CmeExperiment
 *  <P>Purpose: This panel displays 2 rows of 3 images for the
 *  Price Chinese Learning Experiment.
 *  @author Dr. Rick Coleman
 *  @version 1.0
 *  Date: January, 2009
 */
//===================================================================

public class CmeExperiment extends JPanel implements MouseListener
{
	/** The parent application JFrame */
	private CmeApp m_Parent;

	/** Text area width */
	private int m_iTextFrameWidth;
	
	/** Text area height */
	private int m_iTextFrameHeight;
	
	/** Text frame left X */
	private int m_iTextFrameLeftX;
	
	/** Text frame top Y */
	private int m_iTextFrameTopY;
		
	/** The image factory */
	private CmeImageFactory m_ImageFactory;
	
	/** Current display state */
	private int m_State;
	
	/** Vector of images to scan through for rating */
	private Vector m_vImgVec;
	
	/** Current index in the images vector to display */
	private int m_iImgIdx;
	
	/** Rating for this word */
	private int m_iEaseRating;
	
	/** Ease of learning ratings */
	private int[] m_EaseRatings;
	
	/** Array of image indices giving order or EOL presentation */
	private int[] m_EOLOrder;
	
	/** Next index in the ease of learning array to fill */
	private int m_iERIdx;
	
	/** "How easy" label */
	private JLabel m_HowEasyLbl;
	
	/** Line under label */
	private JLabel m_LineUnderLbl;
	
	/** Panel holding the ease of learning widgets */
	private JPanel m_EOLPanel;
	
	// Radio buttons for rating
	
	/** Rating 1 radiobutton */
	private JRadioButton m_Rate1RB;
	
	/** Rating 2 radiobutton */
	private JRadioButton m_Rate2RB;
	
	/** Rating 3 radiobutton */
	private JRadioButton m_Rate3RB;
	
	/** Rating 4 radiobutton */
	private JRadioButton m_Rate4RB;
	
	/** Rating 5 radiobutton */
	private JRadioButton m_Rate5RB;
	
	/** Rating 6 radiobutton */
	private JRadioButton m_Rate6RB;
	
	/** Rating 7 radiobutton */
	private JRadioButton m_Rate7RB;
	
	/** Rating 8 radiobutton */
	private JRadioButton m_Rate8RB;
	
	/** Rating 9 radiobutton */
	private JRadioButton m_Rate9RB;
	
	/** Hidden dummy radio button for clearning all */
	private JRadioButton m_NullRB;
	
	/** Button group for all radio buttons */
	private ButtonGroup m_BGroup;
	
	/** Continue button on the Ease of Learning panel*/
	private JButton m_EOLContinueButton;

	/** The timer object */
	private Timer m_Timer;
	
	/** Timer delay (1 second) */
	private int m_iTimerInterval;
	
	/** Seconds countdown */
	private int m_iInitialTime;
	
	/** Seconds remaining */
	private int m_iSecondsRemaining;
	
	/** Flag if the timer should count down */
	private boolean m_bClockIsRunning;
	
	/** Clock panel for showing the time */
	private CmeClock m_Clock;
	
	/** Vector of string currently being displayed for estimate number to remember*/
	private Vector m_vTextStrings;
	
	/** Panel holding the estimate number to remember widgets */
	private JPanel m_ENRPanel;

	/** Flag if an ENR estimate has been made */
	private boolean m_bEstNumDone;
	
	/** Label 1 for estimate number to remember */
	private JLabel m_ENRNumToRemLbl1;
	
	/** Label 2 for estimate number to remember */
	private JLabel m_ENRNumToRemLbl2;
	
	/** Label 1 for estimate number correct */
	private JLabel m_ENRNumCorrectLbl1;
	
	/** Label 2 for estimate number correct */
	private JLabel m_ENRNumCorrectLbl2;

	/** Continue button on the Estimate Number to Remember panel*/
	private JButton m_ENRContinueButton;

	/** Combo box with number to remember */
	private JTextField m_ENRTextField;
	
	/** Estimated number to remember in test 1 pretrial */
	private int m_ENRPre1;
	
	/** Estimated number to remember in test 2 pretrial*/
	private int m_ENRPre2;
	
	/** Estimated number to remember in test 1 posttrial */
	private int m_ENRPost1;
	
	/** Estimated number to remember in test 2 posttrial*/
	private int m_ENRPost2;
	
	/** Estimated number correct in test 1 */
	private int m_ENC1;
	
	/** Estimated number correct in test 2 */
	private int m_ENC2;
	
	/** Starting Y location for text */
	private int m_iTopY;
	
	/** Starting X location for all lines of text */
	private int m_iLeftX;

	/** Array of indices for first group */
	private int[] m_iGroup1;
	
	/** Array of indices for second group */
	private int[] m_iGroup2;
	
	/** Group being displayed */
	private int m_iGroupDisplayed;
	
	/** Trial number */
	private int m_iTrialNumber;
	
	/** Study order for items in this grid */
	private int m_iStudyOrder;
	
	/** X coordinates for image grid */
	private int[] m_iXCoords;
	
	/** Y coordinates for image grid */
	private int[] m_iYCoords;
	
	/** Width of each grid cell */
	private int m_iGridCellWidth;
	
	/** Height of each grid cell */
	private int m_iGridCellHeight;
	
	/** Spacing around grid */
	private int m_iSpacing;
	
	/** Array flagging which images have been studied */
	private int[] m_iStudied;
	
	/** Study panel for display of Chinese image and English translation */
	private CmeStudy m_StudyPan;
	
	/** Clock reading when a studying was begun */
	private int m_iStartStudyTime;
	
	/** Clock reading when a studying was ended */
	private int m_iEndStudyTime;
	
	/** Panel holding the testing widgets */
	private JPanel m_TestPanel;
	
	/** Text field for test answer input */
	private JTextField m_TestAnswer;
	
	/** Continue button on the test panel*/
	private JButton m_TestContinueButton;

	/** Done studying grid button */
	private JButton m_DoneStudyingGridBtn;
	
	/** Testing image group */
	private int m_iTestGroup;

	/** Testing image index */
	private int m_iTestIndex;
	
	/** Remaining image count */
	private int m_iRemainingCount;
	
	/** Testing image index list */
	private int [] m_iRemainingIdx;
	
	
	//--------------------------------------------
	// Default constructor
	//--------------------------------------------
	public CmeExperiment(CmeApp parent, int width, int height)
	{
		this.m_Parent = parent;
		
		this.setSize(width, height);
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(null);
		
		this.addMouseListener(this);
		
		// Create the clock panel
		m_Clock = new CmeClock();
		m_Clock.setLocation((this.getWidth() - m_Clock.getWidth()) / 2, 
				this.getHeight() - 105);
		this.add(m_Clock);
		
		// Define the area in which to draw the images
		m_iTextFrameWidth = width - 35;
		m_iTextFrameHeight = height - 175;
		m_iTextFrameLeftX = 15;
		m_iTextFrameTopY = 15;
		
		// Create the Ease of Learning panel
		m_EOLPanel = new JPanel();
		m_EOLPanel.setSize(width - 35, 125);
		m_EOLPanel.setLocation(15, this.getHeight() - 135);
		m_EOLPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_EOLPanel.setBackground(Color.LIGHT_GRAY);
		m_EOLPanel.setLayout(null);
		this.add(m_EOLPanel);
		
		int lblX, rbX, lblY, rbY, xInc, rbWidth, rbHeight, lblSize;
		rbX = 50;
		lblX = rbX+5;
		lblY = 44;
		rbY = 28;
		xInc = 108;
		JLabel tempLbl;
		rbWidth = 30;
		rbHeight = 20;
		lblSize = 20;
		
		// Add the instructions
		tempLbl = new JLabel("Rate how easy or difficult this Chinese word will be to learn then click Continue.");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(width - 35, 20);
		tempLbl.setLocation(182, 5);
		m_EOLPanel.add(tempLbl);

		// Draw a line across the panel
		tempLbl = new JLabel("_____________________________________________________________________________");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(width - 35, 20);
		tempLbl.setLocation(100, rbY-20);
		m_EOLPanel.add(tempLbl);

		// Create another label to hide behind the m_EOLPanel
		m_HowEasyLbl = new JLabel("Rate how easy or difficult this Chinese word will be to learn then click Continue.");
		m_HowEasyLbl.setFont(CmeApp.SysSmallFont);
		m_HowEasyLbl.setSize(width - 35, 20);
		m_HowEasyLbl.setLocation(182, 350);
		this.add(m_HowEasyLbl);

		m_LineUnderLbl = new JLabel("_____________________________________________________________________________");
		m_LineUnderLbl.setFont(CmeApp.SysSmallFont);
		m_LineUnderLbl.setSize(width - 35, 20);
		m_LineUnderLbl.setLocation(100, 350);
		this.add(m_LineUnderLbl);

		// Create the rating 1 radio button and label
		tempLbl = new JLabel("1");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate1RB = new JRadioButton();
		m_Rate1RB.setSize(rbWidth, rbHeight);
		m_Rate1RB.setLocation(rbX, rbY);
		m_Rate1RB.setBackground(Color.LIGHT_GRAY);
		m_Rate1RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 1;
					}
				});
		m_EOLPanel.add(m_Rate1RB);

		// Add the "Easy" label
		tempLbl = new JLabel("Easy");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(50, lblSize);
		tempLbl.setLocation(lblX-13, lblY+15);
		m_EOLPanel.add(tempLbl);

		rbX += xInc;
		lblX += xInc;
		
		// Create the rating 2 radio button and label
		tempLbl = new JLabel("2");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate2RB = new JRadioButton();
		m_Rate2RB.setSize(rbWidth, rbHeight);
		m_Rate2RB.setLocation(rbX, rbY);
		m_Rate2RB.setBackground(Color.LIGHT_GRAY);
		m_Rate2RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 2;
					}
				});
		m_EOLPanel.add(m_Rate2RB);
		
		rbX += xInc;
		lblX += xInc;
		
		// Create the rating 3 radio button and label
		tempLbl = new JLabel("3");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate3RB = new JRadioButton();
		m_Rate3RB.setSize(rbWidth, rbHeight);
		m_Rate3RB.setLocation(rbX, rbY);
		m_Rate3RB.setBackground(Color.LIGHT_GRAY);
		m_Rate3RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 3;
					}
				});
		m_EOLPanel.add(m_Rate3RB);
		
		rbX += xInc;
		lblX += xInc;

		// Create the rating 4 radio button and label
		tempLbl = new JLabel("4");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate4RB = new JRadioButton();
		m_Rate4RB.setSize(rbWidth, rbHeight);
		m_Rate4RB.setLocation(rbX, rbY);
		m_Rate4RB.setBackground(Color.LIGHT_GRAY);
		m_Rate4RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 4;
					}
				});
		m_EOLPanel.add(m_Rate4RB);
		
		rbX += xInc;
		lblX += xInc;

		// Create the rating 5 radio button and label
		tempLbl = new JLabel("5");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate5RB = new JRadioButton();
		m_Rate5RB.setSize(rbWidth, rbHeight);
		m_Rate5RB.setLocation(rbX, rbY);
		m_Rate5RB.setBackground(Color.LIGHT_GRAY);
		m_Rate5RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 5;
					}
				});
		m_EOLPanel.add(m_Rate5RB);
		
		// Add the "Medium" label
		tempLbl = new JLabel("Medium");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(75, lblSize);
		tempLbl.setLocation(lblX-23, lblY+15);
		m_EOLPanel.add(tempLbl);

		rbX += xInc;
		lblX += xInc;

		// Create the rating 6 radio button and label
		tempLbl = new JLabel("6");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate6RB = new JRadioButton();
		m_Rate6RB.setSize(rbWidth, rbHeight);
		m_Rate6RB.setLocation(rbX, rbY);
		m_Rate6RB.setBackground(Color.LIGHT_GRAY);
		m_Rate6RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 6;
					}
				});
		m_EOLPanel.add(m_Rate6RB);
		
		rbX += xInc;
		lblX += xInc;

		// Create the rating 7 radio button and label
		tempLbl = new JLabel("7");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate7RB = new JRadioButton();
		m_Rate7RB.setSize(rbWidth, rbHeight);
		m_Rate7RB.setLocation(rbX, rbY);
		m_Rate7RB.setBackground(Color.LIGHT_GRAY);
		m_Rate7RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 7;
					}
				});
		m_EOLPanel.add(m_Rate7RB);
		
		rbX += xInc;
		lblX += xInc;

		// Create the rating 8 radio button and label
		tempLbl = new JLabel("8");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate8RB = new JRadioButton();
		m_Rate8RB.setSize(rbWidth, rbHeight);
		m_Rate8RB.setLocation(rbX, rbY);
		m_Rate8RB.setBackground(Color.LIGHT_GRAY);
		m_Rate8RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 8;
					}
				});
		m_EOLPanel.add(m_Rate8RB);
		
		rbX += xInc;
		lblX += xInc;

		// Create the rating 9 radio button and label
		tempLbl = new JLabel("9");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(lblSize, lblSize);
		tempLbl.setLocation(lblX, lblY);
		m_EOLPanel.add(tempLbl);
		
		m_Rate9RB = new JRadioButton();
		m_Rate9RB.setSize(rbWidth, rbHeight);
		m_Rate9RB.setLocation(rbX, rbY);
		m_Rate9RB.setBackground(Color.LIGHT_GRAY);
		m_Rate9RB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 9;
					}
				});
		m_EOLPanel.add(m_Rate9RB);
		
		rbX += xInc;
		lblX += xInc;

		m_NullRB = new JRadioButton();
		m_NullRB.setSize(rbWidth, rbHeight);
		m_NullRB.setLocation(rbX, rbY);
		m_NullRB.setBackground(Color.LIGHT_GRAY);
		m_NullRB.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_iEaseRating = 0;
					}
				});
		m_NullRB.setVisible(false);
		m_NullRB.setSelected(true);
		m_EOLPanel.add(m_NullRB);
		
		// m_NullRB
		// Add the "Hard" label
		tempLbl = new JLabel("Hard");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(50, lblSize);
		tempLbl.setLocation(lblX-119, lblY+15);
		m_EOLPanel.add(tempLbl);

		// Create the button group and add all buttons
		m_BGroup = new ButtonGroup();
		m_BGroup.add(m_Rate1RB);
		m_BGroup.add(m_Rate2RB);
		m_BGroup.add(m_Rate3RB);
		m_BGroup.add(m_Rate4RB);
		m_BGroup.add(m_Rate5RB);
		m_BGroup.add(m_Rate6RB);
		m_BGroup.add(m_Rate7RB);
		m_BGroup.add(m_Rate8RB);
		m_BGroup.add(m_Rate9RB);
		m_BGroup.add(m_NullRB);
		// Create and add the Continue button
		m_EOLContinueButton = new JButton("Continue");
		m_EOLContinueButton.setSize(100, 20);
		m_EOLContinueButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_EOLContinueButton.setLocation(440, lblY+45);
		m_EOLContinueButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						String name = "Sample";
						if(m_NullRB.isSelected())
						{
							m_Parent.postStatusMessage(" - Continue button pressed but no rating selected. Repeating.", true);
							JOptionPane.showMessageDialog(m_Parent, 
									"Please select an ease of learning rating (1-9)", 
									"Rate Ease of Learning", JOptionPane.WARNING_MESSAGE);
							return;
						}
						m_Parent.postStatusMessage(" - Continue button pressed with rating of " 
								+ String.valueOf(m_iEaseRating) + " for symbol " +
								String.valueOf(m_EOLOrder[m_iImgIdx]), true);
						// Save this rating -- WE MAY NOT NEED THIS
						m_EaseRatings[m_iERIdx] = m_iEaseRating;
						m_iERIdx++;
						// Get the image and store the rating
						CmeImage img = (CmeImage)(m_vImgVec.elementAt(m_EOLOrder[m_iImgIdx]));
						img.setEOLRate(m_iEaseRating);

						m_EOLPanel.setVisible(false); // Hide the panel
						// Set timer interval
						m_Clock.setVisible(true); // Show the clock
						m_iImgIdx++; // Next image
						if(m_iImgIdx < m_EOLOrder.length)
						     name = ((CmeImage)m_vImgVec.elementAt(m_EOLOrder[m_iImgIdx])).getReferenceName();
						else
							name = "Sample";
						if(name.contains("Sample")) // Time to quit
						{
							m_HowEasyLbl.setVisible(false);
							m_LineUnderLbl.setVisible(false);
							m_Clock.setVisible(false);
							m_Parent.setNextState();
						}
						else
						{
							if(m_State == CmeState.RATE_EASE_OF_LEARNING)
							{
								m_Clock.setInitialTime(3); // Set for 3 seconds
								m_iSecondsRemaining = m_iInitialTime; // Reset for 3 seconds
							}
							setEOLTimer();
							paint(getGraphics()); // Repaint
						}
					}
				});
		m_EOLPanel.add(m_EOLContinueButton);
		
		m_EaseRatings = new int[36];
		m_iERIdx = 0;
		
		m_EOLPanel.setVisible(false);
		
		// Create the Estimate Number to Remember panel
		m_ENRPanel = new JPanel();
		m_ENRPanel.setSize(width - 35, 125);
		m_ENRPanel.setLocation(15, this.getHeight() - 135);
		m_ENRPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_ENRPanel.setBackground(Color.LIGHT_GRAY);
		m_ENRPanel.setLayout(null);
		this.add(m_ENRPanel);
		
		lblY = 10;
		
		// Add the labels for Number to Remember
		m_ENRNumToRemLbl1 = new JLabel("Click in the edit box to select the number you think you");
		m_ENRNumToRemLbl1.setFont(CmeApp.SysSmallFont);
		m_ENRNumToRemLbl1.setSize(width - 35, 20);
		m_ENRNumToRemLbl1.setLocation(300, lblY);
		m_ENRPanel.add(m_ENRNumToRemLbl1);
		
		m_ENRNumToRemLbl2 = new JLabel("will be able to remember (0-18) then click Continue.");
		m_ENRNumToRemLbl2.setFont(CmeApp.SysSmallFont);
		m_ENRNumToRemLbl2.setSize(width - 35, 20);
		m_ENRNumToRemLbl2.setLocation(310, lblY+15);
		m_ENRPanel.add(m_ENRNumToRemLbl2);
		
		// Add the labels for Number Answered Correctly
		m_ENRNumCorrectLbl1 = new JLabel("Click in the edit box to select the number you think you");
		m_ENRNumCorrectLbl1.setFont(CmeApp.SysSmallFont);
		m_ENRNumCorrectLbl1.setSize(width - 35, 20);
		m_ENRNumCorrectLbl1.setLocation(300, lblY);
		m_ENRPanel.add(m_ENRNumCorrectLbl1);
		m_ENRNumCorrectLbl1.setVisible(false);
		
		m_ENRNumCorrectLbl2 = new JLabel("answered correctly (0-18) then click Continue.");
		m_ENRNumCorrectLbl2.setFont(CmeApp.SysSmallFont);
		m_ENRNumCorrectLbl2.setSize(width - 35, 20);
		m_ENRNumCorrectLbl2.setLocation(325, lblY+15);
		m_ENRPanel.add(m_ENRNumCorrectLbl2);
		m_ENRNumCorrectLbl2.setVisible(false);
		
		// Add the combobox
		m_ENRTextField = new JTextField(2);
		m_ENRTextField.setSize(50, 20);
		m_ENRTextField.setLocation((width-50)/2+6, lblY+40);
		KeyListener editListener =
				new KeyListener()
				{  
					public void keyPressed(KeyEvent keyEvent) {}
					public void keyReleased(KeyEvent keyEvent) {}
					public void keyTyped(KeyEvent e) {
						m_bEstNumDone = true;
					}
					
				};
		m_ENRTextField.addKeyListener(editListener);
		m_ENRPanel.add(m_ENRTextField);
		m_ENRPre1 = -1;
		m_ENRPre2 = -1;
		m_ENRPost1 = -1;
		m_ENRPost2 = -1;
		m_ENC1 = -1;
		m_ENC2 = -1;

		// Add the continue button
		m_ENRContinueButton = new JButton("Continue");
		m_ENRContinueButton.setSize(100, 20);
		m_ENRContinueButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_ENRContinueButton.setLocation(460, lblY+70);
		m_ENRContinueButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						int value = 0;
						
						try {
							value = Integer.parseInt(m_ENRTextField.getText());
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(m_Parent, 
								"Please enter a number from 0 to 18 in the given text field.", 
								"Estimate a Number", JOptionPane.ERROR_MESSAGE);
							m_ENRTextField.setText("");
							return;
						}
						// Ignore button click if an estimate has not been made
						if(!m_bEstNumDone || value < 0 || value > 18) {
							JOptionPane.showMessageDialog(m_Parent, 
								"Please enter a number from 0 to 18 in the given text field.", 
								"Estimate a Number", JOptionPane.ERROR_MESSAGE);
							m_ENRTextField.setText("");
							return;
						}
						
						m_ENRTextField.setText("");
						
						m_ENRPanel.setVisible(false);
						// Get the estimate from the combobox
						switch(m_State)
						{
							case CmeState.ESTIMATE_NUMBER_TO_REMEMBER_PRE:
							{
								if(m_ENRPre1 == -1) // Haven't gotten this one yet
								{
									m_ENRPre1 = value;
									m_Parent.postStatusMessage(" - Test 1 estimated number to remember pretrial =  "
											+ String.valueOf(m_ENRPre1), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(1, 0, m_ENRPre1);
								}
								else
								{
									m_ENRPre2 = value;
									m_Parent.postStatusMessage(" - Test 2 estimated number to remember pretrial =  "
											+ String.valueOf(m_ENRPre2), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(2, 0, m_ENRPre2);
								}
							break;
							}
							case CmeState.ESTIMATE_NUMBER_TO_REMEMBER_POST:
							{
								if(m_ENRPost1 == -1) // Haven't gotten this one yet
								{
									m_ENRPost1 = value;
									m_Parent.postStatusMessage(" - Test 1 estimated number to remember posttrial =  "
											+ String.valueOf(m_ENRPost1), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(1, 1, m_ENRPost1);
								}
								else
								{
									m_ENRPost2 = value;
									m_Parent.postStatusMessage(" - Test 2 estimated number to remember posttrial =  "
											+ String.valueOf(m_ENRPost2), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(2, 1, m_ENRPost2);
								}	
							break;
							}
							case CmeState.ESTIMATE_NUMBER_CORRECT:
							{
								if(m_ENC1 == -1) // Haven't gotten this one yet
								{
									m_ENC1 = value;
									m_Parent.postStatusMessage(" - Test 1 estimated number correct posttrial =  "
										+ String.valueOf(m_ENRPost1), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(1, 2, m_ENC1);
								}
								else
								{
									m_ENC2 = value;
									m_Parent.postStatusMessage(" - Test 2 estimated number correct posttrial =  "
											+ String.valueOf(m_ENRPost2), true);
									// Save it for later printing also
									m_Parent.saveUserEstimate(2, 2, m_ENC2);
								}	
							break;
							}
						}
						paint(getGraphics());
						m_Parent.setNextState();
					}
				});
		m_ENRPanel.add(m_ENRContinueButton); 
		
		m_ENRPanel.setVisible(false);
		m_iTopY = 60;
		m_iLeftX = 21;

		m_iGroupDisplayed = 0;
		m_iTrialNumber = 0;

		m_State = 0;
		m_iTimerInterval = 1000; // set to 1 second intervals on timers
		
		// Build the arrays of coordinates for drawing images in the grid
		m_iSpacing = 20;
		m_iGridCellWidth = (m_iTextFrameWidth - (2 * m_iSpacing)) / 3;
		m_iGridCellHeight = (m_iTextFrameHeight - (2 * m_iSpacing)) / 3;
		m_iXCoords = new int[3];
		m_iXCoords[0] = m_iTextFrameLeftX + m_iSpacing;
		m_iXCoords[1] = m_iXCoords[0] + m_iGridCellWidth;
		m_iXCoords[2] = m_iXCoords[1] + m_iGridCellWidth;
		m_iYCoords = new int[3];
		m_iYCoords[0] = m_iTextFrameTopY + m_iSpacing;
		m_iYCoords[1] = m_iYCoords[0] + m_iGridCellHeight;
		m_iYCoords[2] = m_iYCoords[1] + m_iGridCellHeight;
		
		m_iStudied = new int[9];
		for(int i=0; i<9; i++)
			m_iStudied[i] = 0; // Not studied
		
		// Create the study panel
		m_StudyPan = new CmeStudy(this);
		m_StudyPan.setVisible(false);
		
		// Create the done studying grid button
		m_DoneStudyingGridBtn = new JButton("Done Studying Grid");
		m_DoneStudyingGridBtn.setSize(150, 20);
		m_DoneStudyingGridBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_DoneStudyingGridBtn.setLocation((width-150)/2, 680);
		m_DoneStudyingGridBtn.setEnabled(false);
		m_DoneStudyingGridBtn.setVisible(false);
		m_DoneStudyingGridBtn.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						int i = 0;
						for(i=0; i<9; i++) {
							if (m_iStudied[i] != 0) {
								break; // Not studied
							}
						}
						if (i >= 9 )
							if (JOptionPane.showConfirmDialog(null, 
								"You have not yet studied any of characters!\nAre you sure you wish to proceed?", 
								"Nothing Has Been Studied!", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
								return;
						
						if(m_StudyPan.isVisible())
						{
							m_StudyPan.toFront();
							m_StudyPan.requestFocusInWindow();
							return;
						}
						m_Clock.setTimeToZero();
						m_iSecondsRemaining = 0;
						stopClock();
					}
				});
		this.add(m_DoneStudyingGridBtn); 

		// Create the Testing panel
		m_TestPanel = new JPanel();
		m_TestPanel.setSize(width - 35, 125);
		m_TestPanel.setLocation(15, this.getHeight() - 135);
		m_TestPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_TestPanel.setBackground(Color.LIGHT_GRAY);
		m_TestPanel.setLayout(null);
		this.add(m_TestPanel);

		// Add the instructions above
		tempLbl = new JLabel("Enter your answer here:");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(360, 20);
		tempLbl.setLocation(340, 20);
		m_TestPanel.add(tempLbl);

		// Add the instructions below
		tempLbl = new JLabel("If what you are typing does not appear click in the text box then try again.");
		tempLbl.setFont(CmeApp.SysSmallFont);
		tempLbl.setSize(600, 20);
		tempLbl.setLocation(225, 50);
		m_TestPanel.add(tempLbl);

		// Add the text field
		m_TestAnswer = new JTextField();
		m_TestAnswer.setSize(150, 20);
		m_TestAnswer.setLocation(535, 20);
		m_TestAnswer.setEditable(true);
		m_TestAnswer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		m_TestPanel.add(m_TestAnswer);
		
		/* Setup the remaining Index array with values from 0-18 */
		if (m_iRemainingIdx == null) {
			m_iRemainingCount = 18;
			m_iRemainingIdx = new int[m_iRemainingCount];
			for(int i=0; i < m_iRemainingCount; i++) m_iRemainingIdx[i] = i;
		}
		
		// Add the continue button
		m_TestContinueButton = new JButton("Continue");
		m_TestContinueButton.setSize(100, 20);
		m_TestContinueButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		m_TestContinueButton.setLocation(460, lblY+70);
		m_TestContinueButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						/* 
						 * Randomized Test Phase
						 * Implemented below, every tested image is random from the 18 total images 
						 * studies in group one and two of each trail.
						 */
						
						String subjAns = m_TestAnswer.getText();
						m_TestAnswer.setText(""); // Clear this answer
						m_TestAnswer.requestFocus();
						
						String correctAns = "";
						String imageValue = "";
						
						int [] group;
						int grpIdx;
						
						if (m_iTestIndex < 9) {
							grpIdx = m_iTestIndex;
							group = m_iGroup1;
						} else {
							grpIdx = m_iTestIndex-9;
							group = m_iGroup2;
						}
						
						if(m_vImgVec == null) // Get it
							m_vImgVec = m_ImageFactory.getImagesVector();

						// Get the image and store the answer
						CmeImage img = (CmeImage)(m_vImgVec.elementAt(group[grpIdx]));
						if(img == null)
							System.out.println("img is null.");

						img.setUserAnswer(subjAns);
						img.setTestPresentationOrder(17 - m_iRemainingCount);
						
						correctAns = m_ImageFactory.getReferenceName(group[grpIdx]);
						imageValue = m_ImageFactory.getImageValue(group[grpIdx]);
						
						m_Parent.postStatusMessage(" - Test " + String.valueOf(m_iTrialNumber) + ": Item " +
								String.valueOf(17 - m_iRemainingCount) + " Subject Answer = " + subjAns + 
								", Correct Answer = " + correctAns + " @ " + imageValue, true);
						
						if (correctAns.toLowerCase().matches(subjAns.toLowerCase())) {
							int imageVal = 0;
							try {
							imageVal = Integer.parseInt(imageValue);
							} catch(Exception ex) { imageVal = 0; }
							
							int value = m_Parent.getUserPoints(m_iTrialNumber);
							m_Parent.saveUserPoints(m_iTrialNumber, value+imageVal);
						}
						
						/* Determine next image! */
						int idx = (int)(Math.floor(Math.random() * m_iRemainingCount));
						m_iTestIndex = m_iRemainingIdx[idx];
						m_iRemainingCount--;
						
						for(int i=idx; i<m_iRemainingCount; i++)
							m_iRemainingIdx[i] = m_iRemainingIdx[i+1];

						paint(getGraphics());

						if (m_iRemainingCount < 0) // We're done
						{
							m_TestPanel.setVisible(false);
							m_Parent.setNextState();
						}
						
						/** Old method for displaying test images -- this method is sequential as specified below */
						// Testing will be done in the order images appeared in the study phase
						//  but interspersed from the three groups of six, i.e. 
						//  Test Item		0  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15  16  17
						//	Group Index		0  1  0  1  0  1  0  1  0  1   0   1   0   1   0   1   0   1
						//  Item Index		0  0  1  1  2  2  3  3  4  4   5   5   6   6   7   7   8   8
						// Record answer
						/*
						String subjAns = m_TestAnswer.getText();
						m_TestAnswer.setText(""); // Clear this answer
						m_TestAnswer.requestFocus();
						String correctAns = "";
						String imageValue = "";
						
						if(m_vImgVec == null) // Get it
							m_vImgVec = m_ImageFactory.getImagesVector();

						if(m_iTestGroup == 0)
						{
							// Get the image and store the answer
							PCLE_Image img = (PCLE_Image)(m_vImgVec.elementAt(m_iGroup1[m_iTestIndex]));
							if(img == null)
								System.out.println("img is null.");

							img.setUserAnswer(subjAns);
							correctAns = m_ImageFactory.getReferenceName(m_iGroup1[m_iTestIndex]);
							imageValue = m_ImageFactory.getImageValue(m_iGroup1[m_iTestIndex]);
						}
						else if(m_iTestGroup == 1)
						{
							// Get the image and store the answer
							PCLE_Image img = (PCLE_Image)(m_vImgVec.elementAt(m_iGroup2[m_iTestIndex]));
							if(img == null)
								System.out.println("img is null.");
							
							img.setUserAnswer(subjAns);
							correctAns = m_ImageFactory.getReferenceName(m_iGroup2[m_iTestIndex]);
							imageValue = m_ImageFactory.getImageValue(m_iGroup2[m_iTestIndex]);
						}
							
						int itemIdx = (m_iTestIndex  * 2) + m_iTestGroup;
						
						if(m_iTrialNumber == 1)
						{
							m_Parent.postStatusMessage(" - Test 1: Item " +
									String.valueOf(itemIdx) +
									" Subject Answer = " + subjAns + ", Correct Answer = " +
									correctAns + " @ " + imageValue, true);	
						}
						else
						{
							m_Parent.postStatusMessage(" - Test 2: Item " +
									String.valueOf((m_iTestGroup * 9) + m_iTestIndex) +
									" Subject Answer = " + subjAns + ", Correct Answer = " +
									correctAns + " @ " + imageValue, true);
						}
						
						m_iTestGroup++;
						if(m_iTestGroup >= 2)
						{
							m_iTestGroup = 0;
							m_iTestIndex++;
						}
						paint(getGraphics());
						if(itemIdx >= 17) // We're done
						{
							m_TestPanel.setVisible(false);
							m_Parent.setNextState();
						}
						*/
					}
				});
		m_TestPanel.add(m_TestContinueButton); 
		
		m_TestPanel.setVisible(false);
		
		
		
	}

	//--------------------------------------------
	/** Get a reference to the parent JFrame */
	//--------------------------------------------
	public CmeApp getParentFrame()
	{
		return m_Parent;
	}
	
	//--------------------------------------------
	/** Override the paint() method */
	//--------------------------------------------
	public void paint(Graphics g)
	{
		super.paint(g);
		// Paint the area
		g.setColor(Color.white);
		g.fillRect(m_iTextFrameLeftX, m_iTextFrameTopY, m_iTextFrameWidth, m_iTextFrameHeight);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY,
				m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY + m_iTextFrameHeight);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY + m_iTextFrameHeight, 
				m_iTextFrameLeftX, m_iTextFrameTopY + m_iTextFrameHeight);
		g.setColor(Color.DARK_GRAY);
		g.drawLine(m_iTextFrameLeftX + m_iTextFrameWidth, m_iTextFrameTopY, 
				m_iTextFrameLeftX, m_iTextFrameTopY);
		g.drawLine(m_iTextFrameLeftX, m_iTextFrameTopY,
				m_iTextFrameLeftX, m_iTextFrameTopY + m_iTextFrameHeight);
		
		if(m_State == CmeState.RATE_EASE_OF_LEARNING)
		{
			// Make sure we don't try to draw before our next state is set
			if(m_iImgIdx < m_EOLOrder.length)
			{
				// Get the current image
				Image img = ((CmeImage)m_vImgVec.elementAt(m_EOLOrder[m_iImgIdx])).getImage();
				g.drawImage(img,(m_iTextFrameWidth - img.getWidth(null))/2, 
						(m_iTextFrameHeight - img.getHeight(null))/2, null);
			}
		}
		else if((m_State == CmeState.ESTIMATE_NUMBER_TO_REMEMBER_PRE) ||
				(m_State == CmeState.ESTIMATE_NUMBER_TO_REMEMBER_POST) ||
				(m_State == CmeState.ESTIMATE_NUMBER_CORRECT))
		{
			int curY = this.m_iTopY;
			// Draw all the strings
			g.setColor(Color.black);
			g.setFont(CmeApp.SysInstructionFont);
			for(int i=0; i<m_vTextStrings.size(); i++)
			{
				String str = (String)m_vTextStrings.elementAt(i);
				// See if it defines an image
				g.drawString((String)m_vTextStrings.elementAt(i), m_iLeftX, curY);
				curY+=15;
			}			
		}
		else if(m_State == CmeState.LEARNING)
		{
			// Draw the grid of images
			int[] theArray = null;
			switch(this.m_iGroupDisplayed)
			{
				case 1 : theArray = this.m_iGroup1; break;
				case 2 : theArray = this.m_iGroup2; break;
			}
			if(theArray == null) return; // Don't draw anything
			
			g.setFont(CmeApp.SysTitleFontB);
			for(int i=0; i<9; i++)
			{
				// Get the image to draw
				Image img = this.m_ImageFactory.getImage(theArray[i]);
				String value = this.m_ImageFactory.getImageValue(theArray[i]);
				String difficulty;
				if (theArray[i] < 12)
					difficulty = "E";
				else if (theArray[i] > 11 && theArray[i] < 24)
					difficulty = "M";
				else
					difficulty = "H";
				// Calculate coordinates
				int gridWidth = m_iXCoords[1] - m_iXCoords[0]; // Should be standard
				int gridHeight = m_iYCoords[1] - m_iYCoords[0];
				int xOffset = (gridWidth - img.getWidth(null)) / 2;
				int yOffset = (gridHeight - img.getHeight(null)) / 2;
				
				int xi = i%3;
				int yi = i/3;

				g.drawImage(img, m_iXCoords[xi]+xOffset - 50, m_iYCoords[yi]+yOffset, null); 
				g.drawString(difficulty + value, m_iXCoords[xi]+gridWidth/2 + 75, m_iYCoords[yi]+gridHeight/2 + 10);
			}
			// Draw the grid around the images
			g.setColor(Color.black);
			g.drawRect(m_iXCoords[0], m_iYCoords[0], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[1], m_iYCoords[0], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[2], m_iYCoords[0], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[0], m_iYCoords[1], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[1], m_iYCoords[1], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[2], m_iYCoords[1], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[0], m_iYCoords[2], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[1], m_iYCoords[2], m_iGridCellWidth, m_iGridCellHeight);
			g.drawRect(m_iXCoords[2], m_iYCoords[2], m_iGridCellWidth, m_iGridCellHeight);
			
			// Draw the appropriate color borders
			for(int i=0; i<9; i++)
			{
				int offsets = 5;
				if(this.m_iStudied[i] == 0)
					g.setColor(Color.green);
				else
					g.setColor(Color.red);
				
				switch(i)
				{
					case 0 : g.drawRect(m_iXCoords[0] + offsets, m_iYCoords[0] + offsets, 
									    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 1 : g.drawRect(m_iXCoords[1] + offsets, m_iYCoords[0] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 2 : g.drawRect(m_iXCoords[2] + offsets, m_iYCoords[0] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 3 : g.drawRect(m_iXCoords[0] + offsets, m_iYCoords[1] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 4 : g.drawRect(m_iXCoords[1] + offsets, m_iYCoords[1] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 5 : g.drawRect(m_iXCoords[2] + offsets, m_iYCoords[1] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 6 : g.drawRect(m_iXCoords[0] + offsets, m_iYCoords[2] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 7 : g.drawRect(m_iXCoords[1] + offsets, m_iYCoords[2] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
					case 8 : g.drawRect(m_iXCoords[2] + offsets, m_iYCoords[2] + offsets, 
						    m_iGridCellWidth - (2*offsets), m_iGridCellHeight - (2*offsets));
						break;
				}
			}
		}
		else if(m_State == CmeState.TESTING)
		{
			if (m_iRemainingCount < 0)
				return;
			
			Image img = null;
			// Draw the appropriate test image
			int idx = m_iTestIndex;
			int [] group;
			if (idx < 9) {
				group = m_iGroup1;
			} else {
				idx -= 9;
				group = m_iGroup2;
			}
			
			img = this.m_ImageFactory.getImage(group[idx]);
			
			if(img != null)
				g.drawImage(img,(m_iTextFrameWidth - img.getWidth(null))/2, 
						(m_iTextFrameHeight - img.getHeight(null))/2, null);
		}
	}
	
	//---------------------------------------------------
	/** Set the array of indices for the order of 
	 *   for the EOL phase presentation */
	//---------------------------------------------------
	public void setEOLPresentationOrder(int[] eolOrder)
	{
		m_EOLOrder = eolOrder;
	}
	
	//---------------------------------------------------
	/** Handle rating the ease of learning */
	//---------------------------------------------------
	public void rateEaseOfLearning()
	{
		// TESTING
		/*if(true) // Skip while testing
		{
			  m_Clock.setVisible(false); // Hide the clock
	    	  m_EOLPanel.setVisible(false); // Show the panel
	    	  m_Parent.setNextState();
	    	  return;
		}*/
		m_State = CmeState.RATE_EASE_OF_LEARNING;
		m_iImgIdx = 0;
		m_vImgVec = m_ImageFactory.getImagesVector();
		paint(getGraphics());
//		m_iInitialTime = m_iSecondsRemaining = 1;
		m_iInitialTime = m_iSecondsRemaining = 3;
		m_Clock.setInitialTime(m_iInitialTime);
		setEOLTimer();
	}
	
	//----------------------------------------------------------
	/** Handle estimate number to remember or number correct. */
	//----------------------------------------------------------
	public void estimateNumber(String fileName, int phase)
	{
		FileReader		instFile;
		BufferedReader	bufReader = null;
		String 			line;

		
		m_State = phase;
		// Get the instructions
		if(m_vTextStrings != null)
			m_vTextStrings.removeAllElements();
		else
			m_vTextStrings = new Vector();
		// Open the file
		try
		{
			instFile = new FileReader(fileName);
		}
		catch(FileNotFoundException e1) // If we failed to opened it
		
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to open " + fileName, 
					"Error Opening File", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Read the text strings and add them to the text area
		try
		{
			bufReader = new BufferedReader(instFile);
			while((line = bufReader.readLine()) != null)
			{
				m_vTextStrings.add(line);
			}
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(this, 
					"Error: Unable to read " + fileName, 
					"Error Reading File", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(m_State == CmeState.ESTIMATE_NUMBER_TO_REMEMBER_PRE)
		{
			m_HowEasyLbl.setVisible(false);
			m_LineUnderLbl.setVisible(false);
			m_ENRNumToRemLbl1.setVisible(true);
			m_ENRNumToRemLbl2.setVisible(true);
			m_ENRNumCorrectLbl1.setVisible(false);
			m_ENRNumCorrectLbl2.setVisible(false);
			this.m_ENRPanel.setVisible(true);
			m_bEstNumDone = false;
			this.paint(this.getGraphics());
		}
		else if(m_State == CmeState.ESTIMATE_NUMBER_TO_REMEMBER_POST)
		{
			m_HowEasyLbl.setVisible(false);
			m_LineUnderLbl.setVisible(false);
			m_ENRNumToRemLbl1.setVisible(true);
			m_ENRNumToRemLbl2.setVisible(true);
			m_ENRNumCorrectLbl1.setVisible(false);
			m_ENRNumCorrectLbl2.setVisible(false);
			this.m_ENRPanel.setVisible(true);
			m_bEstNumDone = false;
			this.paint(this.getGraphics());
		}
		else // m_State == PCLE_State.ESTIMATE_NUMBER_CORRECT
		{
			m_HowEasyLbl.setVisible(false);
			m_LineUnderLbl.setVisible(false);
			m_ENRNumToRemLbl1.setVisible(false);
			m_ENRNumToRemLbl2.setVisible(false);
			m_ENRNumCorrectLbl1.setVisible(true);
			m_ENRNumCorrectLbl2.setVisible(true);
			this.m_ENRPanel.setVisible(true);
			m_bEstNumDone = false;
			this.paint(this.getGraphics());
		}
	}
	
	//-------------------------------------------------------
	/** Do a learning trial */
	//-------------------------------------------------------
	public void doLearning(int[] group1, int[] group2)
	{
		// Set the arrays of indices for the images
		m_iGroup1 = group1;
		m_iGroup2 = group2;
		// Set the trial and group indices
		m_iGroupDisplayed = 1;
		m_iTrialNumber++;
		
		m_State = CmeState.LEARNING;
		this.m_Clock.setVisible(true);
		this.m_DoneStudyingGridBtn.setEnabled(true);
		this.m_DoneStudyingGridBtn.setVisible(true);
		m_iSecondsRemaining = 30;
		m_Clock.setInitialTime(30);
		// TESTING
//		m_iSecondsRemaining = 10;
//		m_Clock.setInitialTime(10);
		setLearningTimer();
		paint(getGraphics());
	}
	
	//-------------------------------------------------------
	/** Do testing */
	//-------------------------------------------------------
	public void doTesting(int trial, int[] group1, int[] group2)
	{
		// Testing will be done in the order images appeared in the study phase
		//  but interspersed from the three groups of six, i.e. 
		//  Test Item		0  1  2  3  4  5  6  7  8  9  10  11  12  13  14  15  16  17
		//	Group Index		0  1  0  1  0  1  0  1  0  1   0   1   0   1   0   1   0   1
		//  Item Index		0  0  1  1  2  2  3  3  4  4   5   5   6   6   7   7   8   8

		// Set the arrays of indices for the images
		m_iGroup1 = group1;
		m_iGroup2 = group2;
		m_iTrialNumber = trial;
	
		/* Setup the random indexes for images */
		m_iRemainingCount = 18;
		if (m_iRemainingIdx == null) {
			m_iRemainingIdx = new int[m_iRemainingCount];
		}
		for(int i=0; i < m_iRemainingCount; i++) m_iRemainingIdx[i] = i;
		
		/* Determine next image! */
		int idx = (int)(Math.floor(Math.random() * m_iRemainingCount));
		m_iTestIndex = m_iRemainingIdx[idx];
		m_iRemainingCount--;
		for(int i=idx; i<m_iRemainingCount; i++)
			m_iRemainingIdx[i] = m_iRemainingIdx[i+1];
		
		m_iTestGroup = 0;
		m_State = CmeState.TESTING;
		this.m_Clock.setVisible(false);
		this.m_TestPanel.setVisible(true);
		m_TestAnswer.requestFocus();
	}
	
	//-------------------------------------------------------
	/** Set a reference to the image factory */
	//-------------------------------------------------------
	public void setImageFactory(CmeImageFactory iFact)
	{
		m_ImageFactory = iFact;
	}
	
	//-------------------------------------------------------
	/** Set a timer for the Ease of Learning phase*/
	//-------------------------------------------------------
	private void setEOLTimer()
	{
		ActionListener taskPerformer = new ActionListener() 
		  {
		      public void actionPerformed(ActionEvent evt) 
		      {
		    	  m_iSecondsRemaining--;
		    	  m_Clock.decrementTime();
		    	  if(m_iSecondsRemaining == 0)
		    	  {
					  m_Clock.setVisible(false); // Hide the clock
			    	  m_EOLPanel.setVisible(true); // Show the panel
			  		  m_Parent.postStatusMessage(" - Stopping timer to estimate ease of learning for character "
							+ String.valueOf(m_EOLOrder[m_iImgIdx]) + ".", true);
			    	  // Clear all the radio buttons
			    	  m_NullRB.doClick();
			    	  m_Timer.stop();
			    	  paint(getGraphics());
		    	  }
		      }
		  };
		m_Timer = new Timer(m_iTimerInterval, taskPerformer);
		m_Timer.setRepeats(true); // Run till done
		m_Parent.postStatusMessage(" - Starting timer to estimate ease of learning for character "
				+ String.valueOf(m_EOLOrder[m_iImgIdx]) + ".", true);
		m_Timer.start();
	}

	//-------------------------------------------------------
	/** Set a timer for the Learning phase*/
	//-------------------------------------------------------
	private void setLearningTimer()
	{
		ActionListener taskPerformer = new ActionListener() 
		  {
		      public void actionPerformed(ActionEvent evt) 
		      {
		    	  if(m_bClockIsRunning)
		    	  {
		    		  m_iSecondsRemaining--;
		    		  m_Clock.decrementTime();
		    	  }
		    	  if(m_iSecondsRemaining == 0)
		    	  {
		    		  if(m_StudyPan.isVisible())
		    		  {
		    			  stopClock();
						  m_StudyPan.setVisible(false);
						  m_DoneStudyingGridBtn.setVisible(true);
						  m_DoneStudyingGridBtn.setEnabled(true);
		    		  }
		    		  
					  m_Clock.setVisible(false); // Hide the clock
			    	  m_Timer.stop();
			    	  paint(getGraphics());
					  m_iGroupDisplayed++;
					  // Reset study order
					  m_iStudyOrder = 0;
					  
					  if(m_iGroupDisplayed >= 3) // finished with all 2 groups
					  {
						  m_Parent.postStatusMessage(" - End study phase", true);
						  for(int i=0; i<9; i++) // Reset all for next time
							  m_iStudied[i] = 0; // Not studied
						  m_DoneStudyingGridBtn.setEnabled(false);
						  m_DoneStudyingGridBtn.setVisible(false);
						  m_Parent.setNextState();
					  }
					  else
					  {
						  m_Parent.postStatusMessage(" - 60 second study period elapsed.", true);
						  // Do next group
						  // TESTING
						  m_Clock.setVisible(true); // Show the clock
//						  m_iSecondsRemaining = 10;
//						  m_Clock.setInitialTime(10);
						  m_iSecondsRemaining = 30;
						  m_Clock.setInitialTime(30);
						  for(int i=0; i<9; i++)
							  m_iStudied[i] = 0; // Not studied
						  setLearningTimer();
						  paint(getGraphics());
					  }
		    	  }
		      }
		  };
		  
		m_bClockIsRunning = false;
		m_Timer = new Timer(m_iTimerInterval, taskPerformer);
		m_Timer.setRepeats(true); // Run till done
		m_Timer.start();
	}

	//-------------------------------------------------------
	/** Stop incrementing the clock */
	//-------------------------------------------------------
	public void stopClock()
	{
		m_bClockIsRunning = false;
		
		// Add a study time for this image
		m_iEndStudyTime = m_Clock.getTimeRemaining();
		if(m_StudyPan.isVisible())
		{
			m_StudyPan.getImage().addStudyTime(
					m_iStartStudyTime - m_iEndStudyTime, m_iStudyOrder);
		}
		m_DoneStudyingGridBtn.setVisible(true);
		m_DoneStudyingGridBtn.setEnabled(true);
	}
	
	//-------------------------------------------------------
	/** Get a reference to the clock */
	//-------------------------------------------------------
	public CmeClock getClock()
	{
		return m_Clock;
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
		if(m_State != CmeState.LEARNING) return;
		
		if(this.m_StudyPan.isVisible())
		{
			m_StudyPan.toFront();
			m_StudyPan.paint(m_StudyPan.getGraphics());
			return;
		}	
		
		// See who got clicked
		int x = e.getX();
		int y = e.getY();
		int selIdx = 0;
		
		int iRow = -1;
		int iCol = -1;
		

		if((y >= m_iYCoords[0]) && (y < m_iYCoords[1])) {
			iRow = 0;
		} else if((y >= m_iYCoords[1]) && (y < m_iYCoords[2])) {
			iRow = 1;
		} else if((y >= m_iYCoords[2]) && (y < (m_iYCoords[2] + m_iGridCellHeight))) {
			iRow = 2;
		}
		
		if((x >= m_iXCoords[0]) && (x < m_iXCoords[1])) {
			iCol = 0;
		} else if((x >= m_iXCoords[1]) && (x < m_iXCoords[2])) {
			iCol = 1;
		} else if((x >= m_iXCoords[2]) && (x < (m_iXCoords[2] + m_iGridCellWidth))) {
			iCol = 2;
		}

		if (iRow < 0 || iCol < 0)
			return;
		
		selIdx = 3*iRow + iCol;
		m_iStudied[selIdx] = 1;
		
		// Repaint
		paint(getGraphics());
		// Show the study panel
		Vector vec = m_ImageFactory.getImagesVector();
		CmeImage theImg;
		
		if(this.m_iGroupDisplayed == 1)
		{
			theImg = (CmeImage)(vec.elementAt(m_iGroup1[selIdx]));
			m_StudyPan.setImage(theImg);
			this.m_Parent.postStatusMessage(" - Studying " + theImg.getReferenceName() +
					". Clock = " + m_Clock.getTimeRemaining() + " seconds.", true);
		}
		else if(this.m_iGroupDisplayed == 2)
		{
			theImg = (CmeImage)(vec.elementAt(m_iGroup2[selIdx]));
			m_StudyPan.setImage((CmeImage)(vec.elementAt(m_iGroup2[selIdx])));
			this.m_Parent.postStatusMessage(" - Studying " + theImg.getReferenceName()+
					". Clock = " + m_Clock.getTimeRemaining() + " seconds.", true);
		}
		m_iStartStudyTime = m_Clock.getTimeRemaining();
		m_iStudyOrder++; // Set next study order index
		
		this.m_bClockIsRunning = true;
		this.m_StudyPan.setVisible(true);
		m_DoneStudyingGridBtn.setVisible(false);
		m_DoneStudyingGridBtn.setEnabled(false);
		// Restart the timer
	}
}
