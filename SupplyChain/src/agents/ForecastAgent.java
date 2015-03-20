package agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.data.time.TimeSeries;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;
import net.sourceforge.openforecast.models.AbstractForecastingModel;
import net.sourceforge.openforecast.models.AbstractTimeBasedModel;
import net.sourceforge.openforecast.models.MovingAverageModel;
import repast.simphony.essentials.RepastEssentials;
import supplyChain.Link;

public class ForecastAgent{
	
	private Business biz;
	private ArrayList<Link> linkList;
	private TreeMap<Integer, Double> orderForecast; 
	private double avgOrderFC;
	
	private AbstractForecastingModel fcModel;
	private DataSet demandData;
	private DescriptiveStatistics demandStats;
	
	private int movAvgTimeSpanPast;
	private int movAvgTimeSpanFuture;
	
	public ForecastAgent(){
		
	}
	
	public ForecastAgent(Business biz){
		this.biz = biz;
		this.linkList = biz.getDownstrLinks();
		this.movAvgTimeSpanPast = 9;
		this.fcModel = new MovingAverageModel();
	}
	
	public TreeMap<Integer, Double> getForecast(int start, int end){
		TreeMap<Integer, Double> forecast = new TreeMap<Integer, Double>();
		fcModel.init(this.demandData);
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
		DataPoint dp = new Observation(demand);
		dp.setIndependentValue("Tick", tick);
		this.demandData.add(dp);
		this.demandStats.addValue(demand);
	}
}
