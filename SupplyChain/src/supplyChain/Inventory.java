package supplyChain;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Inventory {
	
	private ArrayList<Integer> inventoryLevel;
	private int reorderLevel;
	private int reorderInterval;
	private int lastOrderDate;
	private int orderUpToLevel;
	private boolean infinite;
	
	public Inventory(){
		this.inventoryLevel = new ArrayList<Integer>();
		inventoryLevel.add(20);
		reorderLevel = 10;
		reorderInterval = -1;
		orderUpToLevel = 30;
		infinite = false;
	}
	
	
	public void updateInventory(){
		int date = (int)RepastEssentials.GetTickCount();
		inventoryLevel.add(inventoryLevel.get(date-1));
		//System.out.println("Inventory: date: " + date + ", level: " + inventoryLevel.get(date));
	}
	
	public void setInventoryPolicy(String policy){
		//TODO: Auf grundlage des strings reorderLevel, reorderInterval setzen
	}
	
	public void lowerInventory(int size){
		int currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
		this.inventoryLevel.set((int)RepastEssentials.GetTickCount(), currentLevel - size);
	}
	
	public void incrInventory(int size){
		int currentLevel = this.inventoryLevel.get((int)RepastEssentials.GetTickCount());
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
	
	public int getOrderSize(){
		return orderUpToLevel - inventoryLevel.get((int)RepastEssentials.GetTickCount());
	}
	
	public boolean getInfinite(){
		return this.infinite;
	}
	
	public void setInfinite(boolean b){
		this.infinite = b;
	}
	
	public int getInventoryLevel(){
		return inventoryLevel.get(inventoryLevel.size()-1);
	}

}
