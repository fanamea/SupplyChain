package simulation;

import inventoryPlannningAlgorithm.BookbinderTan;
import inventoryPlannningAlgorithm.DefaultInventoryPlanning;
import inventoryPlannningAlgorithm.InventoryPlanningAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
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
import org.apache.commons.math3.stat.regression.SimpleRegression;

import agents.Business;
import agents.Retailer;
import artefacts.DemandData;
import artefacts.TierComparator;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import repast.simphony.random.RandomHelper;
import setups.ReturnsAndBWE_Chen;
import setups.Setup;
import setups.TestSetup;

public class Test {
	
	public static void main(String[] args){
		Setup setup = new TestSetup();
		ArrayList<Business> list = new ArrayList<Business>();
		list.add(new Retailer(setup, 3));
		list.add(new Retailer(setup, 2));
		list.add(new Retailer(setup, 1));
		
		System.out.println(list);
		
		Collections.sort(list, new TierComparator());
		
		for(Business biz : list){
			System.out.println(biz.getTier());
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
			////System.out.println(i + ": " + cumulatedStocks.get(i));
		}
		////System.out.println("----------");
		
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
