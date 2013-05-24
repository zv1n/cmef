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
	
	public int groupSize() {
		if (m_activeGroup == null)
			return super.size();

		Integer gcount = m_cGroups.get(m_activeGroup);
		if (gcount == null)
			return 0;

		return gcount;
	}
	
	public boolean add(CmePair pair) {
		String group = pair.getPairGroup();

		Integer val = m_cGroups.get(group);
		if (val == null)
			val = 0;
		val++;

		m_cGroups.put(group, val);

		return super.add(pair);
	}
	
	public CmePair elementAt(int index) {

		Iterator<CmePair> iter = this.iterator();
		
		if (index < 0) {
			return null;
		}
		
		if (m_activeGroup == null) {
			if (this.size() <= index) {
				return null;
			}
			return super.elementAt(index);
		}
		
		Integer gcount = m_cGroups.get(m_activeGroup);
		if (gcount == null || gcount <= index) {
			return null;
		}

		while (iter.hasNext()) {
			CmePair pair = iter.next();
			if (m_activeGroup.equals(pair.getPairGroup())) {
				if (index == 0) {
					return pair;
				}
				index--;
			}
		}
		
		return null;
	}
}
