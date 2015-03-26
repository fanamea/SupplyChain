package artefacts;

import java.util.Comparator;

public class ProdRequestComparator implements Comparator<ProdRequest> {

	@Override
	public int compare(ProdRequest arg0, ProdRequest arg1) {
		return arg0.getDate().compareTo(arg1.getDate());
	}
	
}
