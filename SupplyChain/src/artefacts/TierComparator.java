package artefacts;

import java.util.Comparator;

import agents.Business;

public class TierComparator implements Comparator<Business> {

	@Override
	public int compare(Business arg0, Business arg1) {
		return new Integer(arg0.getTier()).compareTo(new Integer(arg1.getTier()));
	}
	
}
