package lotSizingAlgorithms;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public class SilverMeal extends LotSizingAlgorithm{

	public SilverMeal(double fixCost, double holdingCost) {
		super(fixCost, holdingCost);
	}

	@Override
	public TreeMap<Integer, Double> calcLotPlan(TreeMap<Integer, Double> demand) {
		
		TreeMap<Integer, Double> lotPlan = new TreeMap<Integer, Double>();
		
		Integer i;
		int lotDate;
		double minPeriodCost;
		double newPeriodCost;
		TreeMap<Integer, Double> subMap = new TreeMap<Integer, Double>();
		Iterator<Integer> iterator = demand.keySet().iterator();
		
		//Initialisieren für die erste Periode
		i = iterator.next();
		lotDate = i;
		subMap.put(i, demand.get(i));
		minPeriodCost = getMeanCostPerPeriod(subMap, fixCost, holdingCost);
		lotPlan.put(i, demand.get(i));
		
		//Für jede weitere Periode
		while(iterator.hasNext()){
			i = iterator.next();
			subMap.put(i, demand.get(i));
			newPeriodCost = getMeanCostPerPeriod(subMap, fixCost, holdingCost);
			
			//Zum Lot hinzufügen
			if(newPeriodCost<minPeriodCost){
				minPeriodCost = newPeriodCost;
				lotPlan.put(lotDate, lotPlan.get(lotDate) + demand.get(i));
				lotPlan.put(i, 0.0);
			}
			//neues Lot aufmachen
			else{
				lotDate = i;
				lotPlan.put(lotDate, demand.get(i));
				subMap = new TreeMap<Integer, Double>();
				subMap.put(i, demand.get(i));
				minPeriodCost = getMeanCostPerPeriod(subMap, fixCost, holdingCost);				
			}	
		}
		return lotPlan;
	}
	
	/**
	 * Abbruchkriterium für Silver Meal
	 * @param period
	 * @param fixCost
	 * @param holdingCost
	 * @return
	 */
	private double getMeanCostPerPeriod(TreeMap<Integer, Double> period, double fixCost, double holdingCost){
		int count = 0;
		double holdingCostTotal = 0;
		for(Integer i : period.keySet()){
			holdingCostTotal += period.get(i)*holdingCost*count;
			count++;
		}
		return (fixCost + holdingCostTotal) / period.size();
	}
	
	
	
}
