package modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lotSizingAlgorithms.LotSizingAlgorithm;
import lotSizingAlgorithms.SilverMeal;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import modules.Link;
import agents.Business;
import artefacts.Material;
import artefacts.ProdRequest;

public class ProductionPlanModule {
	
	private Business biz;
	private LotSizingAlgorithm lotSizingAlgo;
	private int productionTime;
	private double productionCapacity;
	private double setUpCost;
	private double serviceLevel;
	
	private HashMap<Material, Double> billOfMaterial;
	private CopyOnWriteArrayList<ProdRequest> prodRequestPipeLine;
	TreeMap<Integer, Double> lotPlan;
	private TreeMap<Integer, Double> forecast;
	
	
	public ProductionPlanModule(Business biz){
		this.biz = biz;
		this.lotSizingAlgo = new SilverMeal();
		this.prodRequestPipeLine = biz.getProductionOpsModule().getProdReqPipeLine();
		this.productionTime = 2;
		this.productionCapacity = biz.getProductionOpsModule().getCapacity();
		this.setUpCost = 1;
		this.billOfMaterial = new HashMap<Material, Double>();
		for(Link link : biz.getUpstrLinks()){
			System.out.println("bomAdd");
			billOfMaterial.put(link.getMaterial(), 1.0);   //TODO: BillOfMaterial bei Setup einlesen
		}
		this.forecast = new TreeMap<Integer, Double>();
		lotPlan = new TreeMap<Integer, Double>();
		System.out.println("Biz: " + biz.getId() + ", bom.size: " + billOfMaterial.size());
	}	
	
	public void planProduction(){
		TreeMap<Integer, Double> dueList = biz.getInventoryPlanModule().getInventoryDueList(biz.getProduct());
		TreeMap<Integer, Double> capacitatedDueList = capacitatePlannedStocks(dueList);
		System.out.println("Capacitated DueList: " + capacitatedDueList);
		double holdingCost = biz.getInventoryPlanModule().getInventory(biz.getProduct()).getHoldingCost();
		lotPlan = this.lotSizingAlgo.calcLotPlan(capacitatedDueList, setUpCost, holdingCost);
		System.out.println("lotPlan:" + lotPlan);
		fillProdRequestPipeLine(lotPlan);
	}
	
	private void fillProdRequestPipeLine(TreeMap<Integer, Double> lotPlan){
		for(Integer i : lotPlan.keySet()){
			this.prodRequestPipeLine.add(new ProdRequest(i-productionTime, lotPlan.get(i), this.billOfMaterial));
		}
	}
	
	private TreeMap<Integer, Double> capacitatePlannedStocks(TreeMap<Integer, Double> planned){
		TreeMap<Integer, Double> capacitated = new TreeMap<Integer, Double>();
		for(Integer i : planned.descendingKeySet()){
			if(planned.get(i)>productionCapacity){
				if((i-1)!=planned.firstKey()){
					planned.put(i-1, planned.get(i)-productionCapacity);
				}
				capacitated.put(i, productionCapacity);				
			}
			else{
				capacitated.put(i, planned.get(i));
			}
		}
		return capacitated;
	}
	
	public double getResourceDemand(int date, Material material){
		if(lotPlan.containsKey(date)){
			System.out.println("Material: " + material.getId());
			return lotPlan.get(date)*billOfMaterial.get(material);	
		}
				
		else
			return 0.0;
	}
	
	
	public void handForecast(TreeMap<Integer, Double> forecast){
		this.forecast = forecast;
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
	

}
