package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Business extends Node{
		
	private Inventory inventory;
	private Factory factory;
	
	public Business(int tier){
		super(tier);
		this.inventory = new Inventory();
		this.factory = new Factory(this);
		
	}
	
	public void receive(Shipment shipment){
		inventory.incrInventory(shipment.getSize());
	}
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 6)
	public void ship(){
		//System.out.println("ship");
		int date = (int)RepastEssentials.GetTickCount();
		for(Link link : downstrLinks){
			link.executeOrders();
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void placeOrder(){
		if(upstrLinks.size()==0) return;
		//System.out.println("placeOrder");
		if(inventory.checkReorder()){
			int date = (int)RepastEssentials.GetTickCount();
			upstrLinks.get(0).addOrder(new Order(date, inventory.getOrderSize()));
			inventory.setLastOrderDate(date);
			//System.out.println("Order added");
		}
	}	
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		this.inventory.setInfinite(true);
		upstrLinks.add(b);
	}
	
	public void efectShipment(Shipment shipment){
		if(upstrLinks.size()==0) return;
		this.inventory.lowerInventory(shipment.getSize());
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 10)
	public void updateInventory(){
		inventory.updateInventory();
	}
	
	public boolean isOrderShipable(Order order){
		if(order.getSize()<=inventory.getInventoryLevel())
			return true;
		else return false;
	}
	
	public int getShipableAmount(Order order){
		return Math.min(order.getSize(), inventory.getInventoryLevel());
	}
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "Inventory: " + this.inventory.getInventoryLevel() + "\n";
		return string;
	}

}
