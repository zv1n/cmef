package pkgCMEF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class CmeGroupedVector<T> extends Vector<CmePair> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2890690044723023023L;
	private String[] m_activeGroups = null;
	private HashMap<String, Integer> m_cGroups;
	
	public CmeGroupedVector() {
		m_cGroups = new HashMap<String,Integer>();
	}
	
	public void setActiveGroups(String[] groups) {
		m_activeGroups = groups;
	}

	public String[] getActiveGroups() {
		return m_activeGroups;
	}

	public void setActiveGroup(String group) {
		m_activeGroups = new String[] { group };
	}
	
	public void clearActiveGroup() {
		m_activeGroups = null;
	}
	
	public int groupSize() {
		if (m_activeGroups == null || m_cGroups == null)
			return super.size();

		return getTotalCount();
	}

	private int getTotalCount() {
		Integer tcount = 0;

		for (String grp : m_activeGroups) {
			tcount += m_cGroups.get(grp);
		}

		return tcount;
	}

	private void incrementGroupCount(String group) {
		Integer val = m_cGroups.get(group);

		if (val == null)
			val = 1;
		else
			val++;

		m_cGroups.put(group, val);
	}
	
	private boolean isActiveGroup(String group) {
		for (String grp : m_activeGroups) {
			if (grp.equals(group))
				return true;
		}

		return false;
	}

	public boolean add(CmePair pair) {
		String group = pair.getPairGroup();
		incrementGroupCount(group);
		return super.add(pair);
	}

	public CmePair elementAt(int index) {

		Iterator<CmePair> iter = this.iterator();
		
		if (index < 0) {
			return null;
		}
		
		if (m_activeGroups == null) {
			if (this.size() <= index) {
				return null;
			}
			return super.elementAt(index);
		}
		
		Integer gcount = getTotalCount();
		if (gcount == null || gcount <= index) {
			return null;
		}

		while (iter.hasNext()) {
			CmePair pair = iter.next();
			if (isActiveGroup(pair.getPairGroup())) {
				if (index == 0) {
					return pair;
				}
				index--;
			}
		}
		
		return null;
	}
}
