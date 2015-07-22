package modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lotSizingAlgorithms.CapacitatedSilverMeal;
import lotSizingAlgorithms.LotSizingAlgorithm;
import lotSizingAlgorithms.SilverMeal;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import repast.simphony.essentials.RepastEssentials;
import modules.Link;
import agents.Business;
import artefacts.Material;
import artefacts.ProdRequest;

public class ProductionPlanModule {
	
	private Business biz;
	
	//Parameters to define during setup!
	private LotSizingAlgorithm lotSizingAlgo;
	private int productionTime;
	private double productionCapacity;
	private double setUpCost;	
	private HashMap<Material, Double> billOfMaterial;
	
	private double sumProdRequests;
	private double adjustmentSave;
	
	private CopyOnWriteArrayList<ProdRequest> prodRequestPipeLine;
	TreeMap<Integer, Double> lotPlan;
	TreeMap<Integer, Double> lotPlanHistory;
	
	public ProductionPlanModule(Business biz){
		this.biz = biz;
		this.sumProdRequests = 0;
		this.adjustmentSave = 0;
		this.prodRequestPipeLine = biz.getProductionOpsModule().getProdReqPipeLine();
		this.billOfMaterial = new HashMap<Material, Double>();
		for(Link link : biz.getUpstrLinks()){
			////System.out.println("bomAdd");
			billOfMaterial.put(link.getMaterial(), 1.0);   //TODO: BillOfMaterial bei Setup einlesen
		}
		lotPlan = new TreeMap<Integer, Double>();
		lotPlanHistory = new TreeMap<Integer, Double>();
		////System.out.println("Biz: " + biz.getId() + ", bom.size: " + billOfMaterial.size());
	}
	
	public void planProduction(){
		TreeMap<Integer, Double> dueList = biz.getInventoryPlanModule().getInventoryDueList(biz.getProduct());
		//int pivot = dueList.lastKey()-biz.getPlanningPeriod()+1;
		//SortedMap<Integer, Double> tailmap = dueList.tailMap(pivot);
		//TreeMap<Integer, Double> periodList = new TreeMap<Integer, Double>(tailmap);
		TreeMap<Integer, Double> adjustedDueList = adjustDueList(dueList);
		biz.getInformationModule().putAdjustedDueList(adjustedDueList);
		TreeMap<Integer, Double> shiftedList = shiftList(adjustedDueList, -productionTime);
		lotPlan = this.lotSizingAlgo.calcLotPlan(shiftedList);
		
		fillProdRequestPipeLine(lotPlan);
		System.out.println("lotPlan: " + lotPlan);
		lotPlanHistory.putAll(lotPlan);
	}
	
	public TreeMap<Integer, Double> shiftList(TreeMap<Integer, Double> list, int shift){
		TreeMap<Integer, Double> shiftedList = new TreeMap<Integer, Double>();
		for(Integer i : list.keySet()){
			shiftedList.put(i+shift, list.get(i));
		}
		return shiftedList;
	}
	
	private void fillProdRequestPipeLine(TreeMap<Integer, Double> lotPlan){;
		for(Integer i : lotPlan.keySet()){
			if(lotPlan.get(i)>0){
				this.prodRequestPipeLine.add(new ProdRequest(i, lotPlan.get(i), this.billOfMaterial));
				sumProdRequests+=lotPlan.get(i);
			}			
		}
	}
	
	public TreeMap<Integer, Double> adjustDueList(TreeMap<Integer, Double> dueList){
		TreeMap<Integer, Double> adjustedDueList = new TreeMap<Integer, Double>(dueList);
		//System.out.println("periodList1: " + adjustedDueList);
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(currentTick<=1){
			return adjustedDueList;
		}
		int pivotDate = adjustedDueList.firstKey();
		//System.out.println("pivotDate: " + pivotDate);
		ProductionOpsModule opsModule = biz.getProductionOpsModule();
		double currentInventory = biz.getInventoryOpsModule().getInventoryLevel(biz.getProduct());
		double backlog = biz.getDeliveryModule().getBacklog();
		double futureProduction = biz.getProductionOpsModule().getFutureProduction();
		double forecast = biz.getInformationModule().getSumFC(currentTick+1, pivotDate-1);			
		//double arrivingProduction = getPlannedArrivingProduction(currentTick+1, pivotDate-1);
		double adjustment = (backlog  + forecast) - (currentInventory + futureProduction);
		this.adjustmentSave = adjustment;
		System.out.println("currentTick: " + currentTick + "forecast " + forecast + ", backlog: " + backlog + ", currentInventory: " + currentInventory + ", futureProduction: " + futureProduction + ", ADJUSTMENT: " + adjustment);
		
		if(adjustment>=0){
			adjustedDueList.put(pivotDate, adjustedDueList.get(pivotDate)+adjustment);
		}
		
		else{
			for(Integer i : adjustedDueList.keySet()){
				if(adjustment!=0){
					double sub = Math.min(adjustedDueList.get(i), Math.abs(adjustment));
					adjustedDueList.put(i, adjustedDueList.get(i)-sub);
					adjustment+=sub;
				}
			}
		}
		//System.out.println("periodList2: " + adjustedDueList);
		return adjustedDueList;
	}
	
	public double getResourceDemand(int date, Material material){
		if(lotPlan.containsKey(date)){
			//System.out.println("lotPlan: " + lotPlan + ", boM: "+ billOfMaterial + ", Material: " + material.getId());
			return lotPlan.get(date)*billOfMaterial.get(material);	
		}
				
		else
			return 0.0;
	}
	
	public double getPlannedArrivingProduction(int start, int end){
		double sum = 0;
		for(int i=start-productionTime; i<=end-productionTime; i++){
			sum += this.lotPlanHistory.get(i);
		}
		return sum;
	}
	
	public double getPlannedProduction(int tick){
		return this.lotPlanHistory.get(tick);
	}
	
	public double getAdjustment(){
		return this.adjustmentSave;
	}
	
	public double getSumProdRequests(){
		return this.sumProdRequests;
	}
	
	public TreeMap<Integer, Double> getLotPlan(){
		return this.lotPlan;
	}
	
	public double getMaterialFactor(Material material){
		return this.billOfMaterial.get(material);
	}
	
	public HashMap<Material, Double> getBoM(){
		return this.billOfMaterial;
	}
	
	public void setSetUpCost(double setUpCost){
		this.setUpCost = setUpCost;
		if(this.lotSizingAlgo!=null){
			this.lotSizingAlgo.setFixCost(setUpCost);
		}
	}
	
	public void setHoldingCost(double holdingCost){
		if(this.lotSizingAlgo!=null){
			this.lotSizingAlgo.setHoldingCost(holdingCost);
		}
	}
	
	public void setProductionCapacity(double capacity){
		if(this.lotSizingAlgo!=null){
			this.lotSizingAlgo.setCapacity(capacity);
		}
		this.productionCapacity  = capacity;
	}
	
	public double getProductionCapacity(){
		return this.productionCapacity;
	}
	
	public double getSetUpCost(){
		return this.setUpCost;
	}
	
	public int getProductionTime(){
		return this.productionTime;
	}
	
	public void setProductionTime(int productionTime){
		this.productionTime = productionTime;
	}
	
	public void setLotSizingAlgorithm(LotSizingAlgorithm lotSizingAlgo){
		this.lotSizingAlgo = lotSizingAlgo;
		this.lotSizingAlgo.setFixCost(this.setUpCost);
		double holdingCost = biz.getInventoryPlanModule().getInventory(biz.getProduct()).getHoldingCost();
		this.lotSizingAlgo.setHoldingCost(holdingCost);
	}
	
	public void setBillOfMaterial(HashMap<Material,Double> boM){
		this.billOfMaterial = boM;
	}
	

}
