package inventoryPlannningAlgorithm;

import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class DefaultInventoryPlanning extends InventoryPlanningAlgorithm{
	
	public TreeMap<Integer, Double> calcInventoryPlan(TreeMap<Integer, Double> forecast, double serviceLevel) {
		TreeMap<Integer, Double> subMap = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> cumulatedStocks = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> plannedStocks = new TreeMap<Integer, Double>();
		DescriptiveStatistics allStats = new DescriptiveStatistics();
		DescriptiveStatistics subStats = new DescriptiveStatistics();
		double sd;
		double c;
		
		
		for(Double d : forecast.values()){
			allStats.addValue(d);
		}
		
		sd = allStats.getStandardDeviation();
		c = sd/allStats.getMean();
		
		//System.out.println("Forecast: " + forecast);
		//System.out.println("SD: " + sd);
		
		for(Integer i : forecast.keySet()){
			subStats.addValue(forecast.get(i));
			cumulatedStocks.put(i, getCumulatedStock(subStats, serviceLevel, sd));
		}
		
		boolean first = true;
		for(Integer i : cumulatedStocks.keySet()){
			double ante = 0;
			if(!first){
				ante = cumulatedStocks.get(i-1);
			}
			plannedStocks.put(i, cumulatedStocks.get(i)-ante);
			//plannedStocks.put(i, forecast.get(i)+2);
			first = false;
		}
		
		return plannedStocks;
	}
	
	
	//Kein Rückgriff auf history, nur forecast (sd)
	public double getCumulatedStock(DescriptiveStatistics subStats, double serviceLevel, double sd){		
		
		if(sd==0.0){
			return subStats.getSum();
		}
		else{
			NormalDistribution normal = new NormalDistribution(subStats.getSum(), sd*subStats.getN());
			double totalStock = normal.inverseCumulativeProbability(serviceLevel);
			return totalStock;
		}
	}
	

}
