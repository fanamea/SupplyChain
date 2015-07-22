package modules;

import java.util.ArrayList;
import java.util.TreeMap;

import agents.Business;
import modules.InventoryOpsModule;
import artefacts.Material;
import InventoryPolicies.ContinuousOUT;
import InventoryPolicies.ContinuousQ;
import InventoryPolicies.InventoryPolicy;
import InventoryPolicies.PeriodicOUT;
import InventoryPolicies.PeriodicOUT_Quantify;
import InventoryPolicies.PeriodicQ;
import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	private Business biz;
	private InventoryOpsModule opsModule;
	private Material material;
	private InventoryPolicy policy;
	
	private ArrayList<Double> inventoryLevel;
	private TreeMap<Integer, Double> dueList;
	private TreeMap<Integer, Double> orderList;
	
	private TreeMap<Integer, Double> histDueList;
	
	//Parameters to define durint setup!
	private double holdingCost;
	private double serviceLevel;
	
	
	
	public Inventory(Business biz, InventoryOpsModule opsModule, Material material){
		this.biz = biz;
		this.opsModule = opsModule;
		this.material = material;
		this.dueList = new TreeMap<Integer, Double>();
		this.orderList = new TreeMap<Integer, Double>();
		this.inventoryLevel = new ArrayList<Double>();
		this.histDueList = new TreeMap<Integer, Double>();
		
		inventoryLevel.add(0.0);
	}
	
	public void prepareTick(){
		int date = (int)RepastEssentials.GetTickCount();
		inventoryLevel.add(inventoryLevel.get(date-1));
		////System.out.println("Inventory: date: " + date + ", inventory: " + inventoryLevel);
	}
	
	
	public void setInventoryPolicy(InventoryPolicy policy){
		this.policy = policy;
	
	}
	
	public void lowerInventory(double size){
		double currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
		this.inventoryLevel.set((int)RepastEssentials.GetTickCount(), currentLevel - size);
	}
	
	public void incrInventory(double size){
		double currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
		this.inventoryLevel.set((int)RepastEssentials.GetTickCount(), currentLevel + size);
		////System.out.println("BIZ: " + biz.getId() + ", MATERIAL: " + material + ", INVENTORY INCREASED: " + size);
	}
	
	
	/**
	 * Unterscheidet: Wenn Policy -> frag Policy, sonst orderList
	 * @return
	 */
	public double getOrder(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		
		if(this.policy!=null){
			double curInvPos = opsModule.getInventoryPosition(this.material);
			return this.policy.getOrder(currentTick, curInvPos);
		}
		else{
			return this.orderList.get(currentTick);
		}	
		
	}
	
	public double getServiceLevel(){
		return this.serviceLevel;
	}
	
	public void setHoldingCost(double holdingCost){
		this.holdingCost = holdingCost;
	}
	
	public void setServiceLevel(double d){
		this.serviceLevel = d;
	}
	
	public double getInventoryLevel(){
		return inventoryLevel.get(inventoryLevel.size()-1);
	}
	
	public TreeMap<Integer, Double> getDueList(){
		return this.dueList;
	}
	
	public void putDueList(TreeMap<Integer, Double> list){
		this.dueList = list;
		for(Integer i : list.keySet()){
			if(this.histDueList.containsKey(i)){
				histDueList.put(i, histDueList.get(i)-list.get(i));
			}
			else{
				histDueList.put(i, list.get(i));
			}
		}
	}
	
	public void setDueListEntry(int index, double amount){
		this.dueList.put(index, amount);
	}
	
	public double getDueListEntry(int index){
		return this.histDueList.get(index);
	}
	
	public double getHoldingCost(){
		return this.holdingCost;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public InventoryPolicy getPolicy(){
		return this.policy;
	}
	
	public TreeMap<Integer, Double> getOrderList(){
		return this.orderList;
	}
	
	public String getParameterString(){		
		return this.policy.getParameterString();
	}
	
	public String getInformationString(){
		String string = "";
		string += "Inventory Level: " + inventoryLevel;
		return string;
	}
	

}
