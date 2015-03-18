package supplyChain;

import java.util.ArrayList;
import java.util.TreeMap;

import InventoryPolicies.ContinuousOUT;
import InventoryPolicies.ContinuousQ;
import InventoryPolicies.InvPolicies;
import InventoryPolicies.InventoryPolicy;
import InventoryPolicies.PeriodicOUT;
import InventoryPolicies.PeriodicQ;
import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	private Business biz;
	private InventoryOpsAgent opsAgent;
	private Material material;
	private InventoryPolicy policy;
	
	private ArrayList<Double> inventoryLevel;
	private TreeMap<Integer, Double> dueList;
		
	private double fixOrderCost;
	private double holdingCost;
	private double serviceLevel;

	private boolean infinite;				//Unendliches Lager (Ressource supplier)
	
	public Inventory(Business biz, Material material){
		this.biz = biz;
		this.opsAgent = biz.getInventoryOpsAgent();
		this.material = material;
		this.dueList = new TreeMap<Integer, Double>();
		this.inventoryLevel = new ArrayList<Double>();
		fixOrderCost = 100;
		holdingCost = 0.5;	
		serviceLevel = 0.95;		
		infinite = false;
		
		inventoryLevel.add(30.0);
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
	}
	
	public double getOrder(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double curInvLevel = inventoryLevel.get(currentTick);
		
		return this.policy.getOrder(currentTick, curInvLevel);
	}
	
	public double getServiceLevel(){
		return this.serviceLevel;
	}
	
	public void setServiceLevel(double d){
		this.serviceLevel = d;
	}
	
	public boolean getInfinite(){
		return this.infinite;
	}
	
	public void setInfinite(boolean b){
		this.infinite = b;
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
	
	public double getFixOrderCost(){
		return this.fixOrderCost;
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
	
	public String getInformationString(){
		String string = "";
		string += "Inventory Level: " + inventoryLevel;
		return string;
	}
	

}
