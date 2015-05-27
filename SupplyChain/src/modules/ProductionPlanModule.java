package modules;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	private CopyOnWriteArrayList<ProdRequest> prodRequestPipeLine;
	TreeMap<Integer, Double> lotPlan;	
	
	public ProductionPlanModule(Business biz){
		this.biz = biz;
		double holdingCost = biz.getInventoryPlanModule().getInventory(biz.getProduct()).getHoldingCost();
		this.lotSizingAlgo.setFixCost(setUpCost);
		this.lotSizingAlgo.setHoldingCost(holdingCost);
		this.prodRequestPipeLine = biz.getProductionOpsModule().getProdReqPipeLine();
		this.billOfMaterial = new HashMap<Material, Double>();
		for(Link link : biz.getUpstrLinks()){
			//System.out.println("bomAdd");
			billOfMaterial.put(link.getMaterial(), 1.0);   //TODO: BillOfMaterial bei Setup einlesen
		}
		lotPlan = new TreeMap<Integer, Double>();
		//System.out.println("Biz: " + biz.getId() + ", bom.size: " + billOfMaterial.size());
	}	
	
	public void planProduction(){
		TreeMap<Integer, Double> dueList = biz.getInventoryPlanModule().getInventoryDueList(biz.getProduct());
		lotPlan = this.lotSizingAlgo.calcLotPlan(dueList);
		//System.out.println("lotPlan:" + lotPlan);
		TreeMap<Integer, Double> adjustedLotPlan = adjustLotPlan(lotPlan);
		//System.out.println("ADJUSTED LOTPLAN: " + adjustedLotPlan);
		fillProdRequestPipeLine(adjustedLotPlan);
	}
	
	private void fillProdRequestPipeLine(TreeMap<Integer, Double> lotPlan){
		for(Integer i : lotPlan.keySet()){
			this.prodRequestPipeLine.add(new ProdRequest(i-productionTime, lotPlan.get(i), this.billOfMaterial));
		}
	}
	
	public TreeMap<Integer, Double> adjustLotPlan(TreeMap<Integer, Double> lotplan){
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(currentTick==-1){
			return lotplan;
		}
		int pivotDate = lotplan.firstKey();
		ProductionOpsModule opsModule = biz.getProductionOpsModule();
		double currentInventory = biz.getInventoryOpsModule().getInventoryLevel(biz.getProduct());
		double forecast = biz.getForecastModule().getFCSum(currentTick, pivotDate-1);
		double scheduled = opsModule.getProcessingProduction() + opsModule.getScheduledProduction();
		double adjustment = currentInventory+scheduled-forecast;
		System.out.println("currentTick: " + currentTick + ", currentInventory: " + currentInventory + "forecast: " + forecast + ", scheduled" + scheduled + ", ADJUSTMENT:" + adjustment);
		
		for(Integer i : lotplan.keySet()){
			if(adjustment!=0){
				double sub = Math.min(lotplan.get(i), adjustment);
				lotplan.put(i, lotplan.get(i)-sub);
				adjustment-=sub;
			}
		}
		return lotplan;
	}
	
	public double getResourceDemand(int date, Material material){
		if(lotPlan.containsKey(date)){
			//System.out.println("Material: " + material.getId());
			return lotPlan.get(date)*billOfMaterial.get(material);	
		}
				
		else
			return 0.0;
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
	}
	
	public void setProductionCapacity(double capacity){
		this.productionCapacity  = capacity;
	}
	
	public double getProductionCapacity(){
		return this.productionCapacity;
	}
	
	public int getProductionTime(){
		return this.productionTime;
	}
	
	public void setProductionTime(int productionTime){
		this.productionTime = productionTime;
	}
	
	public void setLotSizingAlgorithm(LotSizingAlgorithm lotSizingAlgo){
		this.lotSizingAlgo = lotSizingAlgo;
	}
	
	public void setBillOfMaterial(HashMap<Material,Double> boM){
		this.billOfMaterial = boM;
	}
	

}
