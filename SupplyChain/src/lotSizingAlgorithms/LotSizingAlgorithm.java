package lotSizingAlgorithms;

import java.util.TreeMap;

public abstract class LotSizingAlgorithm {
	
	public abstract TreeMap<Integer, Double> calcLotPlan(TreeMap<Integer, Double> demand, double fixCost, double holdingCost);

}
