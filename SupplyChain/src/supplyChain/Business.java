package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Business extends Node{
	
	private DeliveryAgent deliveryAgent;
	private ForecastAgent forecastAgent;
	private InventoryAgent inventoryAgent;
	private ProductionAgent productionAgent;
	private OrderAgent orderAgent;
	
		
	public Business(int tier){
		super(tier);
		this.inventoryAgent = new InventoryAgent(this.upstrLinks);
		this.productionAgent = new ProductionAgent(this);
		
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryAgent.prepareTick();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 9)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : upstrLinks){
			shipments = link.getArrivingShipments();
			inventoryAgent.processInShipments(link, shipments);
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void placeOrder(){
		if(upstrLinks.size()==0) return;
		//System.out.println("placeOrder");
		orderAgent.placeOrders();
			//System.out.println("Order added");
		}	
	@ScheduledMethod(start = 1, interval = 1, priority = 6)
	public void fetchOrders(){
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 5)
	public void dispatchShipments(){
		this.deliveryAgent.dispatchShipments();
	}
	
		
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		this.inventoryAgent.setInfiniteInInventories(false);
		upstrLinks.add(b);
	}
	
	public DeliveryAgent getDeliveryAgent(){
		return this.deliveryAgent;
	}
	
	public ForecastAgent getForecastAgent(){
		return this.forecastAgent;
	}
	
	public InventoryAgent getInventoryAgent(){
		return this.inventoryAgent;
	}
	
	public ProductionAgent ProductionAgent(){
		return this.productionAgent;
	}
	
	public OrderAgent getOrderAgent(){
		return this.orderAgent;
	}
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "Inventory: " + this.inventoryAgent.getInventoryLevel() + "\n";
		return string;
	}

}
