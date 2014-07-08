package pkgCMEF;

import java.util.Vector;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class CmeComponent implements CmeResponse {

	/** Component List */
	private Vector<Component> m_Components;
	private Vector<String> m_Values;

	/** Component Name */
	private String m_Name;
	
	/** Component ID */
	private String m_Group;

	/** Component Type */
	private int m_Type = 0;
	
	/** Component Types */
	public static final int EDIT_FIELD 		= 0;
  public static final int TEXT_FIELD    = 1;
	public static final int CHECK_BOX 		= 2;
	public static final int RADIO_BUTTON 	= 3;
	public static final int HIDDEN			  = 4;
	
	public CmeComponent(String name) {
    if (name == null)
      System.err.println("No name specified for this component!");

		m_Components = new Vector<Component>();
		m_Values = new Vector<String>();
		m_Name = name.toString();
		m_Group = name.toString();
	}

	public void setName(String name) {
		m_Name = name.toString();
	}
	
	public void setGroup(String id) {
		m_Group = id.toString();
	}
	
	public String getName() {
		return m_Name.toString();
	}
	
	public String getGroup() {
		return m_Group.toString();
	}
	
	public int getType() {
		return m_Type;
	}

	public void addComponent(Component comp) {
		if (comp == null) {
			m_Type = CmeComponent.HIDDEN;
			return;
    } else if (comp instanceof JTextField) {
      m_Type = CmeComponent.EDIT_FIELD;
    } else if (comp instanceof JTextArea) {
      m_Type = CmeComponent.TEXT_FIELD;
		} else if (comp instanceof JCheckBox) {
			m_Type = CmeComponent.CHECK_BOX;
		} else if (comp instanceof JRadioButton) {
			m_Type = CmeComponent.RADIO_BUTTON;
		}
		m_Components.add(comp);
	}
	
	public void addComponent(Component comp, String value) {
		addComponent(comp);
		m_Values.add(value);
	}
	
	public String getValue() {
		switch (m_Type) {
		case CmeComponent.HIDDEN:
			return m_Values.get(0);
    case CmeComponent.EDIT_FIELD:
      return ((JTextField)m_Components.get(0)).getText();

    case CmeComponent.TEXT_FIELD:
      return ((JTextArea)m_Components.get(0)).getText();
			
		case CmeComponent.CHECK_BOX:
			if (((JCheckBox)m_Components.get(0)).isSelected())
				return m_Values.get(0);
			return "";
			
		case CmeComponent.RADIO_BUTTON:
			for (int x = 0; x < m_Components.size(); x++) {
				if (((JRadioButton)m_Components.get(x)).isSelected()) {
					return m_Values.get(x); 
				}
			}
		}
		
		return null;
	}
	
	public Component getComponent(int x) {
		return m_Components.get(0);
	}
	
	public int getComponentCount() {
		return m_Components.size();
	}

}
