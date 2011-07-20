package pkgCMEF;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

//====================================================================
/**
 * CmeInstructions
 * <P>
 * Purpose: This class implements necessary handling to get and change
 * particulars about HTML components after an HTML file is loaded.
 * 
 * @author Terry Meacham
 * @version 1.0 Date: July, 2011
 */
// ===================================================================
public class CmeHtmlView extends JEditorPane {

	/**
	 * Serial ID for ... serialization?
	 */
	private static final long serialVersionUID = 3154648948110081289L;
	
	/**
	 * Application to use for translations
	 */
	private CmeApp m_App;

	/** Vector store for the names of each editable component */
	private Vector<String> m_EditList;
	private Iterator<String> m_EditIter;

	private Vector<String> m_RadioList;
	private Iterator<String> m_RadioIter;
	
	private Vector<String> m_RadioValueList;
	private Iterator<String> m_RadioValueIter;
	
	/** Keep track to ensure at least one radio button is true for each set */
	private HashMap<String, Boolean> m_hRadResults;

	private int MAX_DEPTH = 30;

	public CmeHtmlView(CmeApp app) {
		m_App = app;

		HyperlinkListener hListener = new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hl) {
				if (hl.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					Object component = null;
					
					try {
						component = getComponentByName(hl.getDescription());
					} catch (Exception e) {
						m_App.dmsg(10, "Unknown Link URL: " + hl.getDescription());
						e.printStackTrace();
					}
					
					if (component == null) {
						m_App.dmsg(10, "Unknown Link URL: " + hl.getDescription());
						return;
					}					
				}
			}
		};
		
		this.setEditable(false);
		this.setDoubleBuffered(true);
		this.addHyperlinkListener(hListener);
		
		m_EditList = new Vector<String>();
		m_RadioList = new Vector<String>();
		m_RadioValueList = new Vector<String>();
		m_hRadResults = new HashMap<String, Boolean>();
	}
	
	/**
	 * Clear the sequence data from m_RadioIter, m_RadioValIter, etc.
	 */
	private void clearSequenceInfo() {
		m_EditList.clear();
		m_EditIter = null;

		m_RadioList.clear();
		m_RadioIter = null;
		
		m_RadioValueList.clear();
		m_RadioValueIter = null;
	}
	
	public boolean setContent(String content) {
		this.removeAll();
		clearSequenceInfo();

		//this.setContentType("text/plain");
		this.setContentType("text/html");
		((HTMLDocument)this.getDocument()).setBase(ClassLoader.getSystemResource("."));
		this.setText(content);
		
		generateComponentList();
		m_App.dmsg(10, "Rad Count: " + Integer.toString(m_RadioList.size()));
		
		return true;
	}
	
	public void generateComponentList() {
		clearSequenceInfo();
		m_App.dmsg(10, "Generate Component List!");

		if (!(this.getDocument() instanceof HTMLDocument))
			return;
		
		Element[] root = ((HTMLDocument) this.getDocument())
				.getRootElements();
		
		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseComponentList(root[htm], 0);
			}
		}
		
		m_App.dmsg(11, "Edit List:\n" + m_EditList.toString());
		m_App.dmsg(11, "Radio List:\n" + m_RadioList.toString());
		m_App.dmsg(10, "Componet List Generation Complete!");
	}

	private void recurseComponentList(Element node, int depth) {

		m_App.dmsg(11, "|" + node.getName().toLowerCase() + "|");
		if (node.getName() == "input") {
			AttributeSet attr = node.getAttributes();
			
			String type = (String) attr.getAttribute(HTML.Attribute.TYPE);
			String name = (String) attr.getAttribute(HTML.Attribute.NAME);
			String value = (String) attr.getAttribute(HTML.Attribute.VALUE);

			if (name == null) {
				System.out.println("All inputs must have a name!");
				System.exit(1);
			}

			m_App.dmsg(11, "|" + type.toLowerCase() + "|");
			if (type.toLowerCase().equals("radio")) {
				if (value == null) {
					System.out.println("Radio buttons must have an input Value!");
					System.exit(1);
				}

				m_RadioList.add(name);
				m_RadioValueList.add(value);
			} else { /*if (type.toLowerCase() == "text")*/
				m_EditList.add(name);
			}
		}

		m_App.dmsg(11, "Node Depth: " + Integer.toString(node.getElementCount()));
		for (int x = 0; x < node.getElementCount(); x++) {
			if (depth < MAX_DEPTH) {
				recurseComponentList(node.getElement(x), depth + 1);
			} else {
				System.out.println("Max depth reached!");
			}
		}

	}

	/**	
	 * Iterate through the containers and pull out the necessary information.
	 * 
	 * @param container
	 *            - container from the HTMLView
	 * @return true if the feedback given is valid, false else.
	 * @throws Exception
	 */
	public boolean generateFeedback(CmeState state) throws Exception {
		return recurseFeedback(state, this, 0);
	}
	
	private boolean recurseFeedback(CmeState state, Container container, int depth) throws Exception {
		boolean ret = true;
		Component[] components = null;
		
		if (depth == 0)
			m_hRadResults.clear();
		
		if (m_EditIter == null) {
			m_EditIter = m_EditList.iterator();
			m_App.dmsg(10, "Edit: " + String.valueOf(m_EditList.size()));
		}

		if (m_RadioIter == null) {
			m_RadioIter = m_RadioList.iterator();
			m_App.dmsg(10, "Radio: " + String.valueOf(m_RadioList.size()));
		}
		
		if (m_RadioValueIter == null) {
			m_RadioValueIter = m_RadioValueList.iterator();
			m_App.dmsg(10, "RadioId: " + String.valueOf(m_RadioValueList.size()));
		}

		components = container.getComponents();
		for (int x = 0; x < components.length; x++) {
			if (components[x] instanceof JTextField) {
				
				JTextField tf = (JTextField) components[x];

				if (m_EditIter.hasNext()) {
					if (!state.validateInput(tf.getText())) {
						ret = false;
						m_App.dmsg(10, "Failed to validate input: " + tf.getText());
					} else {
						m_App.addFeedback(m_EditIter.next(), tf.getText());
					}
				} else {
					throw new Exception("Invalid number of components! (tf)");
				}
			} else if (components[x] instanceof JCheckBox) {
				
				JCheckBox cb = (JCheckBox) components[x];

				if (m_EditIter.hasNext()) {
					m_App.addFeedback(m_EditIter.next(), Boolean.toString(cb.isSelected()));
					return false;
				} else {
					throw new Exception("Invalid number of components! (cb)");
				}
			
			} else if (components[x] instanceof JRadioButton) {
				
				JRadioButton rb = (JRadioButton) components[x];

				if (m_RadioIter == null)
					throw new Exception("Ack, Radio Object, but no radio Iterator!");

				if (m_RadioValueIter == null)
					throw new Exception("Ack, Radio Object, but no radio Value Iterator!");
				
				if (m_RadioIter.hasNext() && m_RadioValueIter.hasNext()) {
					String radioName = new String();
					String radioId = new String();
					
					radioName = m_RadioIter.next();
					radioId = m_RadioValueIter.next();
					
					if (rb.isSelected()) {
						m_hRadResults.put(radioName, true);
						m_App.addFeedback(radioName, radioId);
					} else if (!m_hRadResults.containsKey(radioName)) 
						m_hRadResults.put(radioName, false);

					m_App.dmsg(12, "Radio Button Names: " + radioName);
				} else {
					throw new Exception("Invalid number of components! (rb)");
				}

			} else if (components[x] instanceof Container) {
				ret &= recurseFeedback(state, (Container) components[x], depth+1);
			}
		}
	
		if (m_EditIter != null && !m_EditIter.hasNext()) {
			m_EditIter = null;
		}

		if (m_RadioIter != null && !m_RadioIter.hasNext()) {
			m_RadioIter = null;	
		}
		
		if (m_RadioValueIter != null && !m_RadioValueIter.hasNext()) {
			m_RadioValueIter = null;
		}

		/* Iterator through the current results for radial sets without a selection */
		if (depth == 0) {
			Collection<Boolean> col = m_hRadResults.values();
			Iterator<Boolean> iter = col.iterator();
			
			m_App.dmsg(10, "Rad Results:\n" + m_hRadResults.toString());
			
			while(iter.hasNext()) {
				if (iter.next() == false)
					return false;
			}
			
			m_App.dmsg(10, "Final return: " + String.valueOf(ret));
		}
		
		return ret;
	}

	/**	
	 * Return the component named 'name'.
	 * 
	 * @param name - name of the component to retrieve
	 */
	public Object getComponentByName(String name) {
		return null;
	}
}
