package supplyChain;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.math3.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Test {
	
	public static void main(String[] args){
		SortedMap<Integer, Double> forecast = new TreeMap<Integer, Double>();
		for(int i = 1; i<10; i++){
			forecast.put(i, (double)i);
		}
		TreeMap<Integer, Double> forecast2 = new TreeMap<Integer, Double>();
		for(int i = 10; i<20; i++){
			forecast.put(i, (double)i);
		}
		forecast.putAll(forecast2);
		forecast = forecast.tailMap(10);
		forecast2 = new TreeMap<Integer, Double>(forecast);
		for(Integer i : forecast2.keySet()){
			System.out.println(i + ": " + forecast.get(i));
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
