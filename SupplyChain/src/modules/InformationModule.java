package modules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import repast.simphony.essentials.RepastEssentials;
import agents.Business;
import artefacts.DemandData;
import artefacts.Material;

public class InformationModule {
	
	private Business biz;
	private DemandData orderData;
	private DemandData internDemandData;
	private DemandData externDemandData;
	private DemandData combinedDemandData;
	private double trustLevel;
	private boolean trustFeedback;
	private int lastCombined;
	private boolean informationSharing;
	
	private HashMap<Link, DescriptiveStatistics> leadTimeData;
	private DescriptiveStatistics allLeadTimeData;
	private TreeMap<Integer, Double> fcIntern;
	private TreeMap<Integer, Double> histFcIntern;
	private TreeMap<Integer, Double> fcExtern;
	private TreeMap<Integer, Double> histFcExtern;
	private TreeMap<Integer, Double> fcCombined;
	private TreeMap<Integer, Double> histFcCombined;
	
	private double holdingCostProduct;
	private double holdingCostResources;
	private double productionCost;
	private double orderCost;
	private double sumBacklog;
	private double arrivingProduction;
	private double startedProduction;
	private double arrivingShipments;
	private double planningTimeShipments;
	private double ordered;
	private HashMap<Integer, Double> orderPlan;
	private HashMap<Integer, Double> adjustedDueListHist;
	
	
	
	public InformationModule(Business biz){
		this.biz = biz;
		this.orderData = new DemandData();
		this.internDemandData = new DemandData();
		this.trustLevel = 0.0;
		this.trustFeedback = false;
		this.informationSharing = false;
		this.fcIntern = new TreeMap<Integer, Double>();
		this.histFcIntern = new TreeMap<Integer, Double>();
		this.fcExtern = new TreeMap<Integer, Double>();
		this.histFcExtern = new TreeMap<Integer,Double>();
		this.fcCombined = new TreeMap<Integer, Double>();
		this.histFcCombined = new TreeMap<Integer, Double>();
		if(informationSharing){
			this.combinedDemandData = new DemandData();
		}
		else{
			this.combinedDemandData = this.internDemandData;
		}		
		this.orderPlan = new HashMap<Integer, Double>();
		this.adjustedDueListHist = new HashMap<Integer, Double>();		
		this.allLeadTimeData = new DescriptiveStatistics();
		this.leadTimeData = new HashMap<Link, DescriptiveStatistics>();
		for(Link link : biz.getUpstrLinks()){
			leadTimeData.put(link, new DescriptiveStatistics());
		}
	}
	
	public void forecast(int start, int end){
		biz.getForecastModule().setDemandData(internDemandData);
		fcIntern = biz.getForecastModule().getForecast(start, end);
		this.histFcIntern.putAll(fcIntern);
		
		if(!informationSharing){
			this.fcCombined = fcIntern;
			this.histFcCombined.putAll(fcIntern);
		}
		else{
			biz.getForecastModule().setDemandData(externDemandData);
			this.fcExtern = biz.getForecastModule().getForecast(start, end);
			this.histFcExtern.putAll(fcExtern);
			this.fcCombined = combineDataSeries(fcIntern, fcExtern, trustLevel);
			this.histFcCombined.putAll(fcCombined);
		}				
	}
	
	public void combineDemandData(){
		if(informationSharing){
			int currentTick = (int)RepastEssentials.GetTickCount();
			double intern;
			double extern;
			double comb;
			Integer i = lastCombined;
			for(i=lastCombined; i<currentTick; i++){
				intern = this.internDemandData.getDemandData(i);
				extern = this.externDemandData.getDemandData(i);
				comb = (1-trustLevel)*intern+trustLevel*extern;
				combinedDemandData.handDemandData(i, comb);
			}
			lastCombined = (int)RepastEssentials.GetTickCount();
		}
	}
	
	public void recalcTrustLevel(){
		if(informationSharing && trustFeedback){
			int currentTick = (int)RepastEssentials.GetTickCount();
			int pivot = currentTick-biz.getPlanningPeriod();
			
			TreeMap<Integer, Double> subMapIntern = new TreeMap<Integer, Double>(this.histFcIntern.subMap(pivot, true, currentTick, false));
			double errorIntern = getErrorFc(this.internDemandData, subMapIntern);
			TreeMap<Integer, Double> subMapExtern = new TreeMap<Integer, Double>(this.histFcExtern.subMap(pivot, true, currentTick, false));
			double errorExtern = getErrorFc(this.internDemandData, subMapExtern);
			
			double adjust = 0;
			if(errorIntern>errorExtern){
				adjust = 0.05;			
			}
			else{
				adjust = -0.05;
			}
			this.trustLevel += adjust;
			
			this.trustLevel = Math.min(trustLevel, 1);
			this.trustLevel = Math.max(trustLevel, 0);
		}
	}
	
	private double getErrorFc(DemandData demandData, TreeMap<Integer, Double> fc){
		double sum = 0;
		for(Integer i : fc.keySet()){
			//System.out.println("I: " + i);
			sum += Math.pow(demandData.getDemandData(i) - fc.get(i), 2);
		}
		return Math.sqrt(sum/fc.size()-1);
	}
	
	public TreeMap<Integer, Double> combineDataSeries(TreeMap<Integer, Double> ds1, TreeMap<Integer, Double> ds2, double factor){
		TreeMap<Integer, Double> fcCombined = new TreeMap<Integer, Double>();
		double data1;
		double data2;
		double combined;
		for(Integer i : ds1.keySet()){
			data1 = ds1.get(i);
			data2 = ds2.get(i);
			combined = (1-factor)*data1 + factor*data2;
			fcCombined.put(i, combined);
		}
		return fcCombined;
	}
	
	public double getOrdered(){
		return this.ordered;
	}
	
	public void setOrdered(double d){
		this.ordered = d;
	}
	
	public void putLeadTimeData(Link link, double d){
		this.leadTimeData.get(link).addValue(d);
		this.allLeadTimeData.addValue(d);		
	}
	
	public double getMeanLeadTime(Link link){
		return leadTimeData.get(link).getMean();
	}
	
	public double getSDLeadTime(Link link){
		return leadTimeData.get(link).getStandardDeviation();
	}
	
	public double getSDLeadTimeAll(){
		return this.allLeadTimeData.getStandardDeviation();
	}
	
	public double getSDLeadTime(Material material){
		Link link = biz.getOrderPlanModule().getLink(material);
		return leadTimeData.get(link).getStandardDeviation();
	}
	
	public double getMeanLeadTime(Material material){
		Link link = biz.getOrderPlanModule().getLink(material);
		return getMeanLeadTime(link);		
	}
	
	public void putAdjustedDueList(TreeMap<Integer, Double> list){
		this.adjustedDueListHist.putAll(list);
	}
	
	public double getAdjustedDueListEntry(int tick){
		return this.adjustedDueListHist.get(tick);
	}
	
	public double getForecast(int tick){
		return this.histFcCombined.get(tick);
	}
	
	public void putOrderData(int tick, double order){
		this.orderData.handDemandData(tick, order);
	}
	
	public void setInformationTrust(double usageLevel){
		this.trustLevel = usageLevel;
	}
	
	public void setTrustFeedback(){
		this.trustFeedback = true;
	}
	
	public double getTrustLevel(){
		return this.trustLevel;
	}
	
	public void setTrustLevel(double trustLevel){
		this.trustLevel = trustLevel;
	}
	
	public void setPlanningLeadTimeShipments(double d){
		this.planningTimeShipments = d;
	}
	
	public double getMeanLeadTimeAll(){
		return this.allLeadTimeData.getMean();
	}
	
	public double getPlanningTimeShipments(){
		return this.planningTimeShipments;
	}
	
	public double getBacklogProductionEnd(){
		return biz.getProductionOpsModule().getBacklogEnd();
	}
	
	public double getBacklogProductionStart(){
		return biz.getProductionOpsModule().getBacklogStart();
	}
	
	public void setExtDemandData(DemandData demandData){
		this.externDemandData = demandData;
	}
	
	public void setIntDemandData(DemandData demandData){
		this.internDemandData = demandData;
	}
	
	public void addExtDemandData(int tick, double demand){
		this.externDemandData.handDemandData(tick, demand);
	}
	
	public void addIntDemandData(int tick, double demand){
		this.internDemandData.handDemandData(tick, demand);
	}
	
	public double getSDDemand(){
		return this.combinedDemandData.getStandardDeviation();
	}
	
	public double getSDDemand(int period){
		return this.combinedDemandData.getStandardDeviation(period);
	}
	
	public double getVarianceOrders(){
		return this.orderData.getVariance();
	}
	
	public double getMeanDemand(){
		return this.combinedDemandData.getMean();
	}
	
	public double getMeanDemand(int period){
		return this.combinedDemandData.getMean(period);
	}
	
	public void setCustomerDemandData(){
		setExtDemandData(searchCustomerDemandData());
		//System.out.println("Tier: " + biz.getTier());
		//System.out.println("InternDemandData: " + internDemandData);
		//System.out.println("ExternDemandData: " + externDemandData);
		
	}
	
	public double getVarianceCustomerOrders(){
		return this.externDemandData.getVariance();
	}
	
	public void putOrder(int tick, double d){
		if(orderPlan.get(tick)==null){
			orderPlan.put(tick, d);
		}
		else{
			orderPlan.put(tick, orderPlan.get(tick)+d);
		}
	}
	
	public double getPlannedOrder(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(this.orderPlan.get(currentTick)==null){
			return 0.0;
		}
		else{
			return this.orderPlan.get((int)RepastEssentials.GetTickCount());
		}
	}
	
	public void setInformationSharing(boolean b){
		this.informationSharing = b;
	}
	
	public double getPlannedProduction(){
		return biz.getProductionPlanModule().getPlannedProduction((int)RepastEssentials.GetTickCount());
	}
	
	public double getOrderAmountIn(){
		return internDemandData.getDemandData((int)RepastEssentials.GetTickCount());
	}
	
	public double getOrderAmountOut(){
		return orderData.getDemandData((int)RepastEssentials.GetTickCount());
	}
	
	public DemandData getInternDemandData(){
		return this.internDemandData;
	}
	
	public DemandData getExternDemandData(){
		return this.externDemandData;
	}
	
	public void setFcIntern(TreeMap<Integer, Double> fc){
		this.fcIntern = fc;
	}
	
	public void setFcExtern(TreeMap<Integer, Double> fc){
		this.fcExtern = fc;
	}
	
	public TreeMap<Integer, Double> getLastForecast(){
		return this.fcCombined;
	}
	
	public void addHoldingCostProduct(double cost){
		this.holdingCostProduct += cost;
	}
	
	public void addHoldingCostResources(double cost){
		this.holdingCostResources += cost;
	}
	
	public void addProductionCost(double cost){
		this.productionCost += cost;
	}
	
	public void addOrderCost(double cost){
		this.orderCost += cost;
	}
	
	public void addBacklog(double backlog){
		this.sumBacklog += backlog;
	}
	
	public void setArrivingProduction(double d){
		this.arrivingProduction = d;
	}
	
	public void setArrivingShipments(double d){
		this.arrivingShipments = d;
	}
	
	public void setStartedProduction(double d){
		this.startedProduction = d;
	}
	
	public double getStartedProduction(){
		return this.startedProduction;
	}
	
	public double getArrivingProduction(){
		return this.arrivingProduction;
	}
	
	public double getArrivingShipments(){
		return this.arrivingShipments;
	}
	
	public double getHoldingCostProduct(){
		return this.holdingCostProduct;
	}
	
	public double getHoldingCostResources(){
		return this.holdingCostResources;
	}
	
	public double getProductionCost(){
		return this.productionCost;
	}
	
	public double getOrderCost(){
		return this.orderCost;
	}
	
	public double getSumBacklog(){
		return this.sumBacklog;
	}
	
	public double getMeanBacklog(){
		return this.sumBacklog/RepastEssentials.GetTickCount();
	}
	
	public double getSumFC(int start, int end){
		double sum = 0;
		for(Double d : this.histFcCombined.subMap(start, true, end, true).values()){
			sum+=d;
		}
		return sum;
	}
	
	public DemandData searchCustomerDemandData(){
		if(this.biz.getTier()==2){
			return this.internDemandData;
		}
		else{
			return this.biz.getDownstrLinks().get(0).getDownstrNode().searchCustomerDemandData();
		}
	}

}
