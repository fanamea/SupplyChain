package lotSizingAlgorithms;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class CapacitatedSilverMeal extends LotSizingAlgorithm{
	
	private SilverMeal silverMeal;

	public CapacitatedSilverMeal(double fixCost, double holdingCost, double capacity) {
		super(fixCost, holdingCost);
		this.capacity = capacity;
		this.silverMeal = new SilverMeal(fixCost, holdingCost);
	}
	
	@Override
	public TreeMap<Integer, Double> calcLotPlan(TreeMap<Integer, Double> demand) {
		TreeMap<Integer, Double> capacitated = capacitatePlannedStocks(demand);
		return this.silverMeal.calcLotPlan(capacitated);
	}

	private TreeMap<Integer, Double> capacitatePlannedStocks(TreeMap<Integer, Double> planned){
		TreeMap<Integer, Double> capacitated = new TreeMap<Integer, Double>();
		for(Integer i : planned.descendingKeySet()){
			if(planned.get(i)>capacity){
				if((i-1)!=planned.firstKey()){
					planned.put(i-1, planned.get(i)-capacity);
				}
				capacitated.put(i, capacity);				
			}
			else{
				capacitated.put(i, planned.get(i));
			}
		}
		return capacitated;
	}



	
	
	
	
}
