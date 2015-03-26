package artefacts;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

	@Override
	public int compare(Order arg0, Order arg1) {
		return arg0.getDate().compareTo(arg1.getDate());
	}
	
}
