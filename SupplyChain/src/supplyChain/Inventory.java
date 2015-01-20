package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	private HashMap<Integer, Double> dueList;
	
	private ArrayList<Double> inventoryLevel;
	private double serviceLevel;
	private double reorderLevel;
	private int reorderInterval;
	private int lastOrderDate;
	private double orderUpToLevel;
	private boolean infinite;				//Unendliches Lager (Ressource supplier)
	
	public Inventory(){
		this.dueList = new HashMap<Integer, Double>();
		this.inventoryLevel = new ArrayList<Double>();
		this.serviceLevel = 0.95;
		inventoryLevel.add(20.0);
		reorderLevel = 10;
		reorderInterval = -1;
		orderUpToLevel = 30;
		infinite = false;
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
		if(reorderLevel != -1){
			//System.out.println("reorderLevel");
			if(curInvLevel <= reorderLevel)
				return orderUpToLevel-curInvLevel;
			
		}
		else if(reorderInterval != -1){
			//System.out.println("reorderInterval");
			if(currentTick-lastOrderDate == reorderInterval)
				return orderUpToLevel-curInvLevel;
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

}
