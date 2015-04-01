package modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.data.time.TimeSeries;

import agents.Business;
import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.AbstractForecastingModel;
import net.sourceforge.openforecast.models.AbstractTimeBasedModel;
import net.sourceforge.openforecast.models.MovingAverageModel;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class ForecastModule{
	
	private Business biz;
	private ArrayList<Link> linkList;
	
	private AbstractForecastingModel fcModel;
	private TreeMap<Integer, Double> demandData;
	private DescriptiveStatistics demandStats;
	
	public ForecastModule(Business biz){
		this.biz = biz;
		this.linkList = biz.getDownstrLinks();
		this.fcModel = new MovingAverageModel(30);
		this.demandData = new TreeMap<Integer, Double>();
		this.demandStats = new DescriptiveStatistics();
	}
	
	public TreeMap<Integer, Double> getForecast(int start, int end){
		TreeMap<Integer, Double> forecast = new TreeMap<Integer, Double>();
		
		fcModel.init(mapToDataSet(demandData));
		DataSet fcSet = new DataSet();
		for(int i=start; i<=end; i++){
			DataPoint dp = new Observation(0.0);
			dp.setIndependentValue("Tick", i);
			fcSet.add(dp);
		}
		fcModel.forecast(fcSet);
		Iterator it = fcSet.iterator();
		while(it.hasNext()){
			DataPoint dp = (DataPoint)it.next();
			forecast.put((int)dp.getIndependentValue("Tick"), dp.getDependentValue());
		}
		return forecast;
	}
	
	public double getSDDemand(){
		return demandStats.getStandardDeviation();
	}
	
	public double getMeanDemand(){
		return demandStats.getMean();
	}
	
	public void handDemandData(int tick, double demand){		
		if(this.demandData.containsKey(tick)){
			demandData.put(tick, demandData.get(tick)+demand);
		}
		else{
			demandData.put(tick, demand);
		}
		this.demandStats.addValue(demand);
	}
	
	private DataSet mapToDataSet(TreeMap<Integer, Double> map){
		DataSet dataSet = new DataSet();
		for(Integer i : map.keySet()){
			DataPoint dp = new Observation(map.get(i));
			dp.setIndependentValue("Tick", i);
			
			dataSet.add(dp);			
		}
		return dataSet;
	}
}
