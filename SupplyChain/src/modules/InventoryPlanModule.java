package modules;

import inventoryPlannningAlgorithm.DefaultInventoryPlanning;
import inventoryPlannningAlgorithm.InventoryPlanningAlgorithm;

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
import InventoryPolicies.InventoryPolicy;

public class InventoryPlanModule{
	
	private Business biz;
	private InventoryOpsModule opsAgent;
	private HashMap<Material, Inventory> inventories;
	private InventoryPlanningAlgorithm inventoryPlanningAlgorithm;
	
	private TreeMap<Integer, Double> demandForecast;
	
	boolean returnsAllowed;
	
	public InventoryPlanModule(Business biz){
		this.biz = biz;
		this.opsAgent = biz.getInventoryOpsModule();
		this.inventoryPlanningAlgorithm = new DefaultInventoryPlanning();
		
		this.inventories = opsAgent.getInventories();
	}
	
	
	public void setInventoryPolicy(InventoryPolicy policy){
		for(Inventory inventory : inventories.values()){
			policy.setInventory(inventory);
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
			
			if(!returnsAllowed){
				amount = Math.max(0.0, amount);
			}			
			
			if(amount<0.0){				
				double inventoryLevel = inventories.get(material).getInventoryLevel();
				//For negative orders which are bigger than current InventoryLevel
				if((amount+inventoryLevel)<=0){
					amount = inventoryLevel;
				}				
			}			
			
			if(amount!=0.0){
				OrderReq newOrderReq = new OrderReq(material, currentTick, amount);
				if(newOrderReq.getSize()<0.0){
					System.out.println(newOrderReq.getSize());
				}
				orderReqs.add(newOrderReq);
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
				inventory.putDueList(dueList);
				////System.out.println("inInventoryDueList: " + dueList);
			}
		}
	}
	
	public void setHoldingCosts(double holdingCost){
		for(Inventory inventory : inventories.values()){
			inventory.setHoldingCost(holdingCost);
		}
	}
	
	public void setServiceLevels(double serviceLevel){
		for(Inventory inventory : inventories.values()){
			inventory.setServiceLevel(serviceLevel);
		}
	}
	
	public void setInventoryPlanningAlgorithm(InventoryPlanningAlgorithm algo){
		this.inventoryPlanningAlgorithm = algo;
	}
	
	public void setReturnsAllowed(boolean b){
		this.returnsAllowed = b;
	}
	
	public TreeMap<Integer, Double> getInventoryDueList(Material material){
		return inventories.get(material).getDueList();
	}
	
	public Inventory getInventory(Material material){
		return inventories.get(material);
	}

	public void calcOutInventoryDueList(){
		////System.out.println("Biz: " + this.biz.getId() + ", Forecast: " + this.demandForecast);
		Inventory outInventory = inventories.get(biz.getProduct());
		TreeMap<Integer, Double> inventoryPlan = inventoryPlanningAlgorithm.calcInventoryPlan(this.demandForecast, outInventory.getServiceLevel());
		////System.out.println("plannedStocks: " + plannedStocks);
		outInventory.putDueList(inventoryPlan);
	}
	
	public void handForecast(TreeMap<Integer, Double> forecast){
		this.demandForecast = forecast;
		//System.out.println(forecast);
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
		
		return string;
	}

}
