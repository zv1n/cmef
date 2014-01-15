package pkgCMEF;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.FormView;
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
	
	private boolean m_textOnly = false;
	
	/** Event response for enter in text field or default submit button click */
	private ActionListener m_SubmitListener = null;
	
	private ActionListener m_CurrentLinkListener = null;
	
	/** Marker for the first component for a set of components */
	private Component m_FirstComponent = null;
	
	/** Vector store for the names of each editable component */
	private Vector<String> m_EditList;
	private Iterator<String> m_EditIter;
	private Vector<String> m_CheckValueList;
	private Iterator<String> m_CheckValueIter;
	private Vector<String> m_RadioList;
	private Iterator<String> m_RadioIter;
	private Vector<String> m_RadioValueList;
	private Iterator<String> m_RadioValueIter;
	/** Components used in the HtmlView */
	private Vector<CmeResponse> m_Components;

	public CmeHtmlView(CmeApp app, HyperlinkListener lResponse, boolean textOnly) {
		m_App = app;
		m_textOnly = textOnly;
		final HyperlinkListener linkResponse = lResponse;

		HyperlinkListener hListener = new HyperlinkListener() {
			HyperlinkListener response = linkResponse;
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hl) {
				if (hl.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					CmeComponent component = null;
					Component cmp = null;

					try {
						component = getComponentByName(hl.getDescription());
					} catch (Exception e) {
						// m_App.dmsg(10, "Unknown Link URL(CBN): " + hl.getDescription());
						e.printStackTrace();
					}

					if (component == null) {
						if (response != null)
							response.hyperlinkUpdate(hl);
						// m_App.dmsg(10, "Unknown Link URL: " + hl.getDescription());
						return;
					}

					cmp = component.getComponent(0);

					if (cmp instanceof JRadioButton) {
						JRadioButton rb = (JRadioButton) cmp;
						rb.setSelected(!rb.isSelected());
					} else if (cmp instanceof JCheckBox) {
						JCheckBox cb = (JCheckBox) cmp;
						cb.setSelected(!cb.isSelected());
					}
				}
			}
		};

		this.setEditable(false);
		this.setDoubleBuffered(true);
		this.addHyperlinkListener(hListener);

		m_EditList = new Vector<String>();
		m_RadioList = new Vector<String>();
		m_CheckValueList = new Vector<String>();
		m_Components = new Vector<CmeResponse>();
		m_RadioValueList = new Vector<String>();
	}

	/**
	 * Clear the sequence data from m_RadioIter, m_RadioValIter, etc.
	 */
	private void clearSequenceInfo() {
		if (m_EditList != null) {
			m_EditList.clear();
		}
		m_EditIter = null;

		if (m_RadioList != null) {
			m_RadioList.clear();
		}
		m_RadioIter = null;

		if (m_RadioValueList != null) {
			m_RadioValueList.clear();
		}
		m_RadioValueIter = null;
		
		if (m_CheckValueList != null) {
			m_CheckValueList.clear();
		}
		m_CheckValueIter = null;
	}

	public boolean setContent(String content, ActionListener response) throws Exception {
		if (m_textOnly)
			this.setContentType("text/plain");
		else
			this.setContentType("text/html");

		Document basedoc = getEditorKit().createDefaultDocument();
		
		if (basedoc instanceof HTMLDocument) {
			HTMLDocument doc = (HTMLDocument) basedoc;
		
				/* Make sure we have a clean document to use */
			this.setDocument(doc);
			
			//System.out.println("Base Dir: " + ClassLoader.getSystemClassLoader().getSystemResource("./"));
			
			String path = System.getProperty("user.dir");
			if (path == null) {
				System.out.println("Failed to open system path!");
				path = ClassLoader.getSystemClassLoader().getSystemResource(".").getPath();
				if (path == null) {
					System.out.println("Failed to open alternate system path!");
					System.exit(1);
				}
			} 
                
			URL url = new URL("file:///" + path + "/");
			doc.setBase(url);
			doc.setAsynchronousLoadPriority(-1);
			content = imageSrcFixup(path + "\\", content);
			
		}
		
		this.setText(content);

		if (basedoc instanceof HTMLDocument) {
			m_CurrentLinkListener = response;
			generateComponentList(response);
		}
		
		this.setVisible(true);
		return true;
	}
	
	public void refreshView() throws Exception {
		this.setText(this.getText());
		generateComponentList(m_CurrentLinkListener);
		this.setVisible(true);
	}
	
	public void clearContent() {
		this.setVisible(false);
		final Object thisParent = (Object) this;
		m_Components.clear();
		this.removeAll();
		clearSequenceInfo();
	}
	
	private void handleSubmitEvent(JTextField tf) {
		ActionListener[] al = tf.getActionListeners();
		
		for (int x=0; x<al.length; x++) {
			if (al[x] instanceof FormView) {
				tf.removeActionListener(al[x]);
			}
		}
		
		if (m_SubmitListener != null) {
			tf.addActionListener(m_SubmitListener);
		}
	}

	/**
	 * Generate the components from the current instruction file.
	 * @throws Exception
	 */
	private void generateComponentList(ActionListener response) throws Exception {
		clearSequenceInfo();
		m_App.dmsg(10, "Generate Component List!");

		if (!(this.getDocument() instanceof HTMLDocument)) {
			return;
		}
		
		m_Components.clear();
		m_FirstComponent = null;
		
		HTMLDocument doc = (HTMLDocument) this.getDocument();
		Element[] root = doc.getRootElements();	

		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseAttributeList(root[htm], 0);
			}
		}

		if (m_EditList.size() == 0 && m_RadioList.size() == 0) {
			clearSequenceInfo();
			return;
		}
		
		/* Unfortunately, the components of an HTML file are added
		 * in an asynchronous way.  Therefore, we have to continuously
		 * rebuild the list until its of the proper length.
		 */
		while (this.getComponentCount() < (m_EditList.size() + m_RadioList.size()));

		m_EditIter = m_EditList.iterator();
		m_RadioIter = m_RadioList.iterator();
		m_RadioValueIter = m_RadioValueList.iterator();
		m_CheckValueIter = m_CheckValueList.iterator();
		m_SubmitListener = response;

		recurseComponentList(this, 0);
		System.out.print("Count: ");
		System.out.println(m_Components.size());
		
		requestFirstFocus();
		
		clearSequenceInfo();
	}
	
	/** 
	 * Request to bring the first element into focus.
	 */
	public void requestFirstFocus() {
		if (m_FirstComponent != null)
			m_FirstComponent.requestFocus();
	}

	/**
	 * Recursive function to trace thru the attributes from the HTML file.
	 * 
	 * @param node - element associated with the recursive elements.
	 * @param depth - current depth in the element tree
	 */
	private void recurseAttributeList(Element node, int depth) {

		if (node.getName() == "input") {
			AttributeSet attr = node.getAttributes();

			String type = (String) attr.getAttribute(HTML.Attribute.TYPE);
			String name = (String) attr.getAttribute(HTML.Attribute.NAME);
			String value = (String) attr.getAttribute(HTML.Attribute.VALUE);

			if (name == null) {
				System.out.println("All inputs must have a name!");
				System.exit(1);
			}


			if (type.toLowerCase().equals("radio")) {
				if (value == null) {
					System.out.println("Radio buttons must have an input Value!");
					System.exit(1);
				}

				m_RadioList.add(name);
				m_RadioValueList.add(value);
			} else if (type.toLowerCase().equals("checkbox")) {
				if (value == null) {
					System.out.println("Check buttons must have an input Value!");
					System.exit(1);
				}

				m_EditList.add(name);
				m_CheckValueList.add(value);
			} else if (type.toLowerCase().equals("hidden")) {
				CmeComponent comp = new CmeComponent(name);
				comp.addComponent(null, value);
				m_Components.add(comp);
				m_App.dmsg(CmeApp.DEBUG_HTML_VIEW, "Hidden:" + name);
			} else { /*if (type.toLowerCase() == "text")*/
				m_EditList.add(name);
			}
		}

		for (int x = 0; x < node.getElementCount(); x++) {
			recurseAttributeList(node.getElement(x), depth + 1);
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
	private void recurseComponentList(Container container, int depth) throws Exception {
		Component[] components = null;
		components = container.getComponents();

		for (int x = 0; x < components.length; x++) {

			if (components[x] instanceof JTextField) {
				if (m_EditIter.hasNext()) {
					CmeComponent comp = new CmeComponent(m_EditIter.next());
					comp.addComponent(components[x]);
					m_Components.add(comp);
					handleSubmitEvent((JTextField)components[x]);
					
					if (m_FirstComponent == null)
						m_FirstComponent = components[x];
				} else {
					throw new Exception("Invalid number of components! (tf)");
				}

			} else if (components[x] instanceof JCheckBox) {

				JCheckBox cb = (JCheckBox) components[x];

				if (m_EditIter.hasNext() && m_CheckValueIter.hasNext()) {
					CmeComponent comp = new CmeComponent(m_EditIter.next());
					comp.addComponent(components[x], m_CheckValueIter.next());
					m_Components.add(comp);
					
					if (m_FirstComponent == null)
						m_FirstComponent = components[x];
				} else {
					throw new Exception("Invalid number of components! (cb)");
				}

			} else if (components[x] instanceof JRadioButton) {
				if (m_RadioIter == null) {
					throw new Exception("Ack, Radio Object, but no radio Iterator!");
				}

				if (m_RadioValueIter == null) {
					throw new Exception("Ack, Radio Object, but no radio Value Iterator!");
				}

				if (m_RadioIter.hasNext() && m_RadioValueIter.hasNext()) {
					String name = m_RadioIter.next();
					CmeComponent comp = getComponentByGroup(name);
					if (comp == null) {
						comp = new CmeComponent(name);
						m_Components.add(comp);
					}
					comp.addComponent(components[x], m_RadioValueIter.next());
					
					if (m_FirstComponent == null)
						m_FirstComponent = components[x];
				} else {
					throw new Exception("Invalid number of components! (rb)");
				}

			} else if (components[x] instanceof Container) {
				recurseComponentList((Container) components[x], depth + 1);
			}
		}
	}

	/**	
	 * Return the component named 'name'.
	 * @param name - name of the component to retrieve
	 */
	public CmeComponent getComponentByName(String name) {
		if (m_Components == null) {
			return null;
		}
		for (int x = 0; x < m_Components.size(); x++) {
			if (m_Components.get(x).getName().equals(name)) {
				return (CmeComponent) m_Components.get(x);
			}
		}
		return null;
	}

	/**	
	 * Return the component group 'group'.
	 * @param group - group of the component to retrieve
	 */
	public CmeComponent getComponentByGroup(String group) {
		if (m_Components == null) {
			return null;
		}
		for (int x = 0; x < m_Components.size(); x++) {
			if (m_Components.get(x).getGroup().equals(group)) {
				return (CmeComponent) m_Components.get(x);
			}
		}
		return null;
	}

	/**	
	 * Return the component named 'name'.
	 * @param name - name of the component to retrieve
	 */
	public Iterator<CmeResponse> getResponseIterator() {
		if (m_Components == null) {
			return null;
		}
		return m_Components.iterator();
	}

    private String imageSrcFixup(String path, String content) throws Exception {
        Vector<Integer> insert = new Vector<Integer>();
        
        String lc = new String(content);
        int start = 0;
        
        while ((start = lc.indexOf("src=\"", start)) != -1) {
            start += 5;
        }
        
        for(int x=insert.size()-1; x>=0; x--) {
            String newContent = content.substring(0, insert.get(x)) + path + content.substring(x+1);
            content = newContent;
        }
 
        return content;
    }
}
