package simulation;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.TreeMap;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.AbstractForecastingModel;
import net.sourceforge.openforecast.models.MovingAverageModel;

import org.apache.commons.math3.*;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import repast.simphony.random.RandomHelper;

public class Test {
	
	public static void main(String[] args){
		AbstractDistribution distr = new Uniform(3,3,3);
		for(int i=0; i<10; i++){
			System.out.println(distr.nextInt());
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
			//System.out.println(i + ": " + cumulatedStocks.get(i));
		}
		//System.out.println("----------");
		
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
	
	

}
