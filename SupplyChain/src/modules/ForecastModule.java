package modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jfree.data.time.TimeSeries;

import agents.Business;
import artefacts.DemandData;
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
	
	private AbstractForecastingModel fcModel;
	private DemandData demandData;
	
	private int reviewPeriod;
	
	public ForecastModule(Business biz){
		this.biz = biz;
		this.reviewPeriod = 20;
		this.fcModel = new MovingAverageModel(this.reviewPeriod);
	}
	
	public TreeMap<Integer, Double> getForecast(int start, int end){
		TreeMap<Integer, Double> forecast = new TreeMap<Integer, Double>();
		fcModel.init(this.demandData.getDemandDataSet(reviewPeriod));
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
		//System.out.println("DemandData: " + this.demandData.getDataMap());
		//System.out.println("Forecast: " + forecast);
		return forecast;
	}
	
	public double getFCSum(int start, int end){
		TreeMap<Integer, Double> forecast = getForecast(start, end);
		double sum = 0;
		for(Double d : forecast.values()){
			sum+=d;
		}
		return sum;
	}
	
	public double getSDDemand(){
		return demandData.getStandardDeviation();
	}
	
	public double getMeanDemand(){
		return demandData.getMean();
	}
	
	public void setDemandData(DemandData demandData){
		this.demandData = demandData;
	}
}
