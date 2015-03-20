package supplyChain;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.MovingAverageModel;

import org.apache.commons.math3.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Test {
	
	public static void main(String[] args){
		DataSet dataSet = new DataSet();
		for(int i = 0; i<100; i=i+2){
			Observation ob1 = new Observation(5.0);
			ob1.setIndependentValue("Tick", i);
			dataSet.add(ob1);
		}	
		
		ForecastingModel model = new MovingAverageModel(10);
		model.init(dataSet);
		
		DataSet fcDataSet = new DataSet();
		DataPoint fc1 = new Observation(0.0);
		fc1.setIndependentValue("Tick", 102);
		DataPoint fc2 = new Observation(0.);
		fc2.setIndependentValue("Tick", 104);
		fcDataSet.add(fc1);
		fcDataSet.add(fc2);
		
		model.forecast(fcDataSet);
		Iterator it = fcDataSet.iterator();
		while(it.hasNext()){
			DataPoint dp = (DataPoint)it.next();
			System.out.println(dp.getDependentValue());
		}
		
	}
	
	
	/*
	 * TODO: Substract forecasted inventory for first period
	 */
	public static TreeMap<Integer, Double> getPlannedStocks(TreeMap<Integer, Double> forecast){
		
		TreeMap<Integer, Double> subMap = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> cumulatedStocks = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> plannedStocks = new TreeMap<Integer, Double>();
		
		for(Integer i : forecast.keySet()){
			subMap.put(i, forecast.get(i));
			cumulatedStocks.put(i, getCumulatedStock(subMap));
			System.out.println(i + ": " + cumulatedStocks.get(i));
		}
		System.out.println("----------");
		
		boolean first = true;
		for(Integer i : cumulatedStocks.keySet()){
			double ante = 0;
			if(!first){
				ante = cumulatedStocks.get(i-1);
			}
			plannedStocks.put(i, cumulatedStocks.get(i)-ante);
			first = false;
		}
		
		return plannedStocks;
	}
	
	//Kein Rückgriff auf history, nur forecast (sd)
	public static double getCumulatedStock(TreeMap<Integer, Double> forecast){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(Integer i : forecast.keySet()){
			stats.addValue(forecast.get(i));
		}
		double sd = stats.getStandardDeviation();
		if(sd==0) sd=0.001;
		
		NormalDistribution normal = new NormalDistribution(stats.getSum(), sd*stats.getN());
		double totalStock = normal.inverseCumulativeProbability(0.8);
		
		return totalStock;
	}
	
	public static void exampleSetUp(Setup setup){
		
		for(int i = 0; i < 7; i++){
			setup.addNode(i);
		}
		
		setup.addLink(0, 3, 3);
		setup.addLink(1, 3, 3);
		setup.addLink(2, 4, 3);
		setup.addLink(3, 5, 2);
		setup.addLink(4, 5, 2);
		setup.addLink(5, 6, 1);
		
		setup.print();
	}

}
