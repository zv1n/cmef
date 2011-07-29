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
	
	/** Components used in the HtmlView */
	private Vector<CmeResponse> m_Components;

	public CmeHtmlView(CmeApp app) {
		m_App = app;

		HyperlinkListener hListener = new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hl) {
				if (hl.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					CmeComponent component = null;
					Component cmp = null;
					
					try {
						component = getComponentByName(hl.getDescription());
					} catch (Exception e) {
						m_App.dmsg(10, "Unknown Link URL(CBN): " + hl.getDescription());
						e.printStackTrace();
					}
					
					if (component == null) {
						m_App.dmsg(10, "Unknown Link URL: " + hl.getDescription());
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
		m_Components = new Vector<CmeResponse>();
		m_RadioValueList = new Vector<String>();
	}
	
	/**
	 * Clear the sequence data from m_RadioIter, m_RadioValIter, etc.
	 */
	private void clearSequenceInfo() {
		if (m_EditList != null)
			m_EditList.clear();
		m_EditIter = null;

		if (m_RadioList != null)
			m_RadioList.clear();
		m_RadioIter = null;

		if (m_RadioValueList != null)
			m_RadioValueList.clear();
		m_RadioValueIter = null;
	}
	
	public boolean setContent(String content) throws Exception {
		m_Components.clear();
		this.removeAll();
		clearSequenceInfo();

		//this.setContentType("text/plain");
		this.setContentType("text/html");
		((HTMLDocument)this.getDocument()).setBase(ClassLoader.getSystemResource("."));		
		this.setText(content);
		
		generateComponentList();
		return true;
	}
	
	/**
	 * Generate the components from the current instruction file.
	 * @throws Exception
	 */
	private void generateComponentList() throws Exception {
		clearSequenceInfo();
		m_App.dmsg(10, "Generate Component List!");

		if (!(this.getDocument() instanceof HTMLDocument))
			return;
		
		Element[] root = ((HTMLDocument) this.getDocument())
				.getRootElements();
		
		for (int htm = 0; htm < root.length; htm++) {
			if (root[htm].getName().equals("html")) {
				recurseAttributeList(root[htm], 0);
			}
		}
		
		m_EditIter = m_EditList.iterator();
		m_RadioIter = m_RadioList.iterator();
		m_RadioValueIter = m_RadioValueList.iterator();
		
		recurseComponentList(this, 0);
		System.out.println(m_Components.size());
		
		clearSequenceInfo();
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
				} else 
					throw new Exception("Invalid number of components! (tf)");
				
			} else if (components[x] instanceof JCheckBox) {
				
				JCheckBox cb = (JCheckBox) components[x];

				if (m_EditIter.hasNext()) {
					CmeComponent comp = new CmeComponent(m_EditIter.next());
					comp.addComponent(components[x], cb.getText());
					m_Components.add(comp);
				} else 
					throw new Exception("Invalid number of components! (cb)");
			
			} else if (components[x] instanceof JRadioButton) {
				if (m_RadioIter == null)
					throw new Exception("Ack, Radio Object, but no radio Iterator!");

				if (m_RadioValueIter == null)
					throw new Exception("Ack, Radio Object, but no radio Value Iterator!");
				
				if (m_RadioIter.hasNext() && m_RadioValueIter.hasNext()) {
					CmeComponent comp = new CmeComponent(m_RadioIter.next());
					comp.addComponent(components[x], m_RadioValueIter.next());
					m_Components.add(comp);
				} else 
					throw new Exception("Invalid number of components! (rb)");

			} else if (components[x] instanceof Container) {
				recurseComponentList((Container) components[x], depth+1);
			}
		}
	}

	/**	
	 * Return the component named 'name'.
	 * @param name - name of the component to retrieve
	 */
	public CmeComponent getComponentByName(String name) {
		if (m_Components == null)
			return null;
		for (int x = 0; x < m_Components.size(); x++) {
			if(m_Components.get(x).getName().equals(name))
				return (CmeComponent) m_Components.get(x);
		}
		return null;
	}

	/**	
	 * Return the component group 'group'.
	 * @param group - group of the component to retrieve
	 */
	public CmeComponent getComponentByGroup(String group) {
		if (m_Components == null)
			return null;
		for (int x = 0; x < m_Components.size(); x++) {
			if(m_Components.get(x).getGroup().equals(group))
				return (CmeComponent) m_Components.get(x);
		}
		return null;
	}

	/**	
	 * Return the component named 'name'.
	 * @param name - name of the component to retrieve
	 */
	public Iterator<CmeResponse> getResponseIterator() {
		if (m_Components == null)
			return null;
		return m_Components.iterator();
	}
}
