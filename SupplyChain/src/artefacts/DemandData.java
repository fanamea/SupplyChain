package artefacts;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import repast.simphony.essentials.RepastEssentials;

public class DemandData implements Iterable<Integer>{
	
	private TreeMap<Integer, Double> dataMap;
	private DataSet demandDataSet;
	private DescriptiveStatistics demandStats;
	
	public DemandData(){
		
		this.dataMap = new TreeMap<Integer, Double>();
		this.demandDataSet = new DataSet();
		this.demandStats = new DescriptiveStatistics();
		
	}
	
	public double getStandardDeviation(){
		return demandStats.getStandardDeviation();
	}
	
	public double getStandardDeviation(int period){
		return getTailStats(period).getStandardDeviation();
	}
	
	public double getMean(){
		return demandStats.getMean();
	}
	
	public double getMean(int period){
		return getTailStats(period).getMean();
	}
	
	public double getVariance(){
		return demandStats.getVariance();
	}
	
	public double getVariance(int period){
		return getTailStats(period).getVariance();
	}
	
	public void handDemandData(int tick, double demand){
		////System.out.println("handDemandData: " + tick + ", demand: " + demand);
		DataPoint dp = new Observation(demand);
		dp.setIndependentValue("Tick", tick);
		this.dataMap.put(tick, demand);
		this.demandDataSet.add(dp);
		this.demandStats.addValue(demand);
	}
	
	
	public double getDemandData(int tick){
		return this.dataMap.get(tick);
	}
	
	public TreeMap<Integer, Double> getDataMap(){
		return this.dataMap;
	}
	
	public DataSet getDemandDataSet(){
		return this.demandDataSet;
	}
	
	public DataSet getDemandDataSet(int period){
		DataSet ds = new DataSet();
		int currentTick = (int)RepastEssentials.GetTickCount();
		
		for(int i = currentTick - period; i<currentTick; i++){
			DataPoint dp = new Observation(this.dataMap.get(i));
			dp.setIndependentValue("Tick", i);
			ds.add(dp);
		}
		return ds;
	}
	
	public DescriptiveStatistics getDemandStats(){
		return this.demandStats;
	}

	@Override
	public Iterator iterator() {
		return this.dataMap.entrySet().iterator();
	}
	
	public DescriptiveStatistics getTailStats(int period){
		DescriptiveStatistics temp = new DescriptiveStatistics();
		int pivot = (int)RepastEssentials.GetTickCount() - period + 1;
		for(Double d : dataMap.tailMap(pivot).values()){
			temp.addValue(d);
		}
		return temp;
	}

}
