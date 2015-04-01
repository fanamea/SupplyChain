package modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import agents.Business;
import artefacts.Material;
import artefacts.OrderReq;
import repast.simphony.essentials.RepastEssentials;
import modules.Inventory;
import InventoryPolicies.InvPolicies;
import InventoryPolicies.InventoryPolicy;

public class InventoryPlanModule{
	
	private Business biz;
	private InventoryOpsModule opsAgent;
	private HashMap<Material, Inventory> inventories;
	
	private TreeMap<Integer, Double> demandForecast;
	
	private double serviceLevel;
	
	
	public InventoryPlanModule(Business biz){
		this.biz = biz;
		this.opsAgent = biz.getInventoryOpsModule();
		this.serviceLevel = 0.98;
		
		this.inventories = opsAgent.getInventories();		
	}
	
	
	public void setInventoryPolicy(InvPolicies policy){
		for(Inventory inventory : inventories.values()){
			inventory.setInventoryPolicy(policy);
		}
	}
	
	/**
	 * Für Inventory Policies jeden Tick
	 */
	public void placeOrderReqs(){
		OrderOpsModule orderAgent = biz.getOrderOpsModule();		
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Material material : inventories.keySet()){
			ArrayList<OrderReq> orderReqs = new ArrayList<OrderReq>();
			double amount = inventories.get(material).getOrder();
			if(amount>0){
				orderReqs.add(new OrderReq(material, currentTick, amount));
			}
			orderAgent.handOrderReqs(material, orderReqs);
		}				
	}
	
	/**
	 * Für Inventory Policies in der Planungperiode
	 */
	public void recalcPolicyParams(){
		for(Inventory inventory : inventories.values()){
			inventory.getPolicy().recalcParams();
		}
	}
	
	/**
	 * Für ohne Inventory Policies, sondern Production Planning + MRP	 
	 */
	public void planMRP(ProductionPlanModule productionPlanModule){
		calcInInventoriesDueLists(productionPlanModule);
	}
	
	/**
	 * Für ohne Inventory Policies, sondern Production Planning + MRP
	 */
	public void planEndProduct(){
		calcOutInventoryDueList();
	}
	
	
	/**
	 * ProductionPlanAgent.lotPlan -> inventory.dueList
	 */
	public void calcInInventoriesDueLists(ProductionPlanModule productionPlanModule){
		
		//Über alle Inventories
		for(Material material : this.inventories.keySet()){
			
			if(material!=biz.getProduct()){
				TreeMap<Integer, Double> dueList = new TreeMap<Integer, Double>();
				Inventory inventory = inventories.get(material);
				//Über alle Eintrage in productionDueList
				TreeMap<Integer, Double> lotPlan = productionPlanModule.getLotPlan();
				for(Integer date : lotPlan.keySet()){
					double amount = productionPlanModule.getResourceDemand(date, material);
					dueList.put(date, amount);
				}
				inventory.setDueList(dueList);
				System.out.println("inInventoryDueList: " + dueList);
			}
		}
	}		
	
	public void setServiceLevels(double serviceLevel){
		for(Inventory inventory : inventories.values()){
			inventory.setServiceLevel(serviceLevel);
		}
	}
	
	public TreeMap<Integer, Double> getInventoryDueList(Material material){
		return inventories.get(material).getDueList();
	}
	
	public Inventory getInventory(Material material){
		return inventories.get(material);
	}

	public void calcOutInventoryDueList(){
		System.out.println("Biz: " + this.biz.getId() + ", Forecast: " + this.demandForecast);
		Inventory outInventory = inventories.get(biz.getProduct());
		TreeMap<Integer, Double> plannedStocks = getPlannedStocks(this.demandForecast);
		System.out.println("plannedStocks: " + plannedStocks);
		outInventory.setDueList(plannedStocks);
	}

		
	/*
	 * TODO: Substract forecasted inventory for first period
	 */
	public TreeMap<Integer, Double> getPlannedStocks(TreeMap<Integer, Double> forecast){
		
		TreeMap<Integer, Double> subMap = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> cumulatedStocks = new TreeMap<Integer, Double>();
		TreeMap<Integer, Double> plannedStocks = new TreeMap<Integer, Double>();
		
		for(Integer i : forecast.keySet()){
			subMap.put(i, forecast.get(i));
			cumulatedStocks.put(i, getCumulatedStock(subMap));
		}
		
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
	public double getCumulatedStock(TreeMap<Integer, Double> forecast){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(Integer i : forecast.keySet()){
			stats.addValue(forecast.get(i));
		}
		double sd = stats.getStandardDeviation();
		if(sd==0) sd=0.001;
		
		NormalDistribution normal = new NormalDistribution(stats.getSum(), sd*stats.getN());
		double totalStock = normal.inverseCumulativeProbability(serviceLevel);
		
		return totalStock;
	}
	
	public void handForecast(TreeMap<Integer, Double> forecast){
		this.demandForecast = forecast;
	}
	
	public String getPlanString(){
		String string = "";
		string += "      Inventories: \n";
		for(Material material :inventories.keySet()){
			string += "         Material: " + material.getId() + "\n" 
					+ "            " + inventories.get(material).getParameterString() + "\n";
		}
		return string;
	}
	
	public String getInformationString(){
		String string = "";
		string += "      InInventories: \n";
		for(Material material :inventories.keySet()){
			string += "         Material: " + material.getId() + "\n" 
					+ "            " + inventories.get(material).getInformationString() + "\n";
		}
		string += "      OutInventory: " + "\n"
				+ "            " + outInventory.getInformationString() + "\n";
		
		return string;
	}

}
