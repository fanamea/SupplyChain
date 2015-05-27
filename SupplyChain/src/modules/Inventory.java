package modules;

import java.util.ArrayList;
import java.util.TreeMap;

import agents.Business;
import modules.InventoryOpsModule;
import artefacts.Material;
import InventoryPolicies.ContinuousOUT;
import InventoryPolicies.ContinuousQ;
import InventoryPolicies.InvPolicies;
import InventoryPolicies.InventoryPolicy;
import InventoryPolicies.PeriodicOUT;
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
		
		inventoryLevel.add(0.0);
	}
	
	public void prepareTick(){
		int date = (int)RepastEssentials.GetTickCount();
		inventoryLevel.add(inventoryLevel.get(date-1));
		//System.out.println("Inventory: date: " + date + ", inventory: " + inventoryLevel);
	}
	
	
	public void setInventoryPolicy(InvPolicies policy){
		switch(policy){
		case ContOUT:
			this.policy = new ContinuousOUT(biz, this);
			break;
			
		case ContQ:
			this.policy = new ContinuousQ(biz, this);
			break;
		
		case PeriodicOUT:
			this.policy = new PeriodicOUT(biz, this);
			break;
		
		case PeriodicQ:
			this.policy = new PeriodicQ(biz, this);
			break;					
		}		
	}
	
	public void lowerInventory(double size){
		double currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
		this.inventoryLevel.set((int)RepastEssentials.GetTickCount(), currentLevel - size);
	}
	
	public void incrInventory(double size){
		double currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
		this.inventoryLevel.set((int)RepastEssentials.GetTickCount(), currentLevel + size);
		//System.out.println("BIZ: " + biz.getId() + ", MATERIAL: " + material + ", INVENTORY INCREASED: " + size);
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
	
	public void setDueList(TreeMap<Integer, Double> list){
		this.dueList = list;
	}
	
	public void setDueListEntry(int index, double amount){
		this.dueList.put(index, amount);
	}
	
	public double getDueListEntry(int index){
		return this.dueList.get(index);
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
