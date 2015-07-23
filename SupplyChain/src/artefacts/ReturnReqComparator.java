package artefacts;

import java.util.Comparator;

public class ReturnReqComparator implements Comparator<ReturnReq> {

	@Override
	public int compare(ReturnReq arg0, ReturnReq arg1) {
		return arg0.getDate().compareTo(arg1.getDate());
	}
	
}
