package inventoryPlannningAlgorithm;

import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class BookbinderTan extends InventoryPlanningAlgorithm{
	
	public TreeMap<Integer, Double> calcInventoryPlan(TreeMap<Integer, Double> forecast, double serviceLevel) {
		TreeMap<Integer, Double> cumulatedStocks = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> plannedStocks = new TreeMap<Integer, Double>();
		DescriptiveStatistics allStats = new DescriptiveStatistics();
		DescriptiveStatistics subStats = new DescriptiveStatistics();
		double c;
		double z;
		
		for(Double d : forecast.values()){
			allStats.addValue(d);
		}
		NormalDistribution normal = new NormalDistribution(0,1);
		z = normal.inverseCumulativeProbability(serviceLevel);
		System.out.println("z: " + z);
		
		c = allStats.getStandardDeviation()/allStats.getMean();
		System.out.println("c: " + c);
		//System.out.println("Forecast: " + forecast);
		//System.out.println("SD: " + sd);
		
		for(Integer i : forecast.keySet()){
			subStats.addValue(forecast.get(i));
			double cumulatedStock = subStats.getSum() + z*c*Math.sqrt(subStats.getSumsq());
			cumulatedStocks.put(i, cumulatedStock);
		}
		System.out.println(cumulatedStocks);
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
