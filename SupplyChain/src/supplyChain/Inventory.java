package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	private HashMap<Integer, Double> dueList;
	
	private ArrayList<Double> inventoryLevel;
	private int reorderLevel;
	private int reorderInterval;
	private int lastOrderDate;
	private int orderUpToLevel;
	private boolean infinite;				//Unendliches Lager (Ressource supplier)
	
	public Inventory(){
		this.dueList = new HashMap<Integer, Double>();
		this.inventoryLevel = new ArrayList<Double>();
		inventoryLevel.add(20.0);
		reorderLevel = 10;
		reorderInterval = -1;
		orderUpToLevel = 30;
		infinite = false;
	}
	
	
	public void prepareTick(){
		int date = (int)RepastEssentials.GetTickCount();
		inventoryLevel.add(inventoryLevel.get(date-1));
		//System.out.println("Inventory: date: " + date + ", level: " + inventoryLevel.get(date));
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
	
	public boolean checkReorder(){
		if(reorderLevel != -1){
			//System.out.println("reorderLevel");
			return inventoryLevel.get(inventoryLevel.size()-1)<=reorderLevel;
		}
		else if(reorderInterval != -1){
			//System.out.println("reorderInterval");
			return RepastEssentials.GetTickCount()-lastOrderDate == reorderInterval;
		}
		else return false;
	}
	
	public void setLastOrderDate(int date){
		this.lastOrderDate = date;
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
	
	public void setDueListEntry(int index, double amount){
		this.dueList.put(index, amount);
	}
	
	public double getDueListEntry(int index){
		return this.dueList.get(index);
	}

}
