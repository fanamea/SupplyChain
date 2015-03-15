package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	public InventoryAgent inventoryAgent;
	public Material material;
	private HashMap<Integer, Double> dueList;
	
	private ArrayList<Double> inventoryLevel;
	private double serviceLevel;
	private double orderUpToLevel;
	private double reorderLevel;
	private double orderQuantity;
	private int period;
	private int lastOrderDate;
	
	private double fixOrderCost;
	private double holdingCost;

	private boolean infinite;				//Unendliches Lager (Ressource supplier)
	
	public Inventory(InventoryAgent inventoryAgent, Material material){
		this.inventoryAgent = inventoryAgent;
		this.material = material;
		this.dueList = new HashMap<Integer, Double>();
		this.inventoryLevel = new ArrayList<Double>();
		fixOrderCost = 100;
		holdingCost = 0.5;
		
		inventoryLevel.add(30.0);
		serviceLevel = 0.95;
		orderUpToLevel = -1;
		reorderLevel = -1;
		period = -1;
		orderQuantity = -1;
		
		infinite = false;
	}
	
	public void calcOrderQuantity(){
		double meanOrder = inventoryAgent.getForecastAgent().getAvgOrderFC();
		this.orderQuantity = inventoryAgent.getPlanningTechniques().getEOQ(meanOrder, fixOrderCost, holdingCost);
	}
	
	public void prepareTick(){
		int date = (int)RepastEssentials.GetTickCount();
		inventoryLevel.add(inventoryLevel.get(date-1));
		//System.out.println("Inventory: date: " + date + ", inventory: " + inventoryLevel);
	}
	
	public void setInventoryPolicy(String policy){
		//TODO: Auf grundlage des strings reorderLevel, reorderInterval setzen
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
		//(s,S) oder (s,Q)
		if(reorderLevel != -1){
			if(curInvLevel <= reorderLevel){
				//(s,S)
				if(orderUpToLevel != -1)
					return orderUpToLevel-curInvLevel;
				//(s,Q)
				else
					return orderQuantity;
			}			
		}
		//(P,S) oder (P,Q)
		else if(period != -1){
			if(currentTick-lastOrderDate == period){
				setLastOrderDate(currentTick);
				//(P,S)
				if(orderUpToLevel != -1)
					return orderUpToLevel-curInvLevel;
				//(P,Q)
				else
					return orderQuantity;
			}
		}
		return 0.0;
	}
	
	public void setLastOrderDate(int date){
		this.lastOrderDate = date;
	}
	
	public double getServiceLevel(){
		return this.serviceLevel;
	}
	
	public void setServiceLevel(double d){
		this.serviceLevel = d;
	}
	
	public double getOrderSize(){
		return orderUpToLevel - inventoryLevel.get((int)RepastEssentials.GetTickCount());
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
	
	public HashMap<Integer, Double> getDueList(){
		return this.dueList;
	}
	
	public void setDueList(HashMap<Integer, Double> list){
		this.dueList = list;
	}
	
	public void setDueListEntry(int index, double amount){
		this.dueList.put(index, amount);
	}
	
	public double getDueListEntry(int index){
		return this.dueList.get(index);
	}
	
	public void setReorderLevel(double level){
		this.reorderLevel = level;
	}
	
	public void setOrderQuantity(double q){
		this.orderQuantity = q;
	}
	
	public double getFixOrderCost(){
		return this.fixOrderCost;
	}
	
	public double getHoldingCost(){
		return this.holdingCost;
	}
	
	public double getReorderLevel(){
		return this.reorderLevel;
	}
	public void setOrderUpToLevel(double l){
		this.orderUpToLevel = l;
	}
	
	public void setPeriod(int p){
		this.period = p;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public String getInformationString(){
		String string = "";
		string += "AimLevel: " + aimLevel + ";   Inventory Level: " + inventoryLevel;
		return string;
	}
	

}
