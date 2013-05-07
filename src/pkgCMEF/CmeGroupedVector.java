package pkgCMEF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class CmeGroupedVector<T> extends Vector<CmePair> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2890690044723023023L;
	private String m_activeGroup = null;
	private HashMap<String, Integer> m_cGroups;
	
	public CmeGroupedVector() {
		m_cGroups = new HashMap<String,Integer>();
	}
	
	public void setActiveGroup(String group) {
		m_activeGroup = group;
	}
	
	public void clearActiveGroup() {
		m_activeGroup = null;
	}
	
	public boolean add(CmePair pair) {
		String group = pair.getPairGroup();
		m_cGroups.put(group, m_cGroups.get(group)+1);
		return super.add(pair);
	}
	
	public CmePair elementAt(int index) {
		Iterator<CmePair> iter = this.iterator();
		
		if (index < 0)
			return null;
		
		if (m_activeGroup == null) {
			if (this.size() <= index)
				return null;
			return super.elementAt(index);
		}

		if (m_cGroups.get(m_activeGroup) <= index)
			return null;

		while (iter.hasNext()) {
			CmePair pair = iter.next();
			if (m_activeGroup == pair.getPairGroup())
				if (index == 0)
					return pair;
				index--;
		}

		return null;
	}
}
