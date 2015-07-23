package inventoryPlannningAlgorithm;

import java.util.TreeMap;

public abstract class InventoryPlanningAlgorithm {
	
	public abstract TreeMap<Integer, Double> calcInventoryPlan(TreeMap<Integer, Double> forecast, double serviceLevel);

}
