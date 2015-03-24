package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public abstract class Business extends Node{
	
	protected DeliveryModule deliveryModule;
	protected OrderOpsModule orderOpsModule;
	protected InventoryOpsModule inventoryOpsModule;	
	protected InventoryPlanModule inventoryPlanModule;
	protected ForecastModule forecastModule;
	
	public Business(int tier){
		super(tier);
	}
	
	public void initNode(){
		this.deliveryModule = new DeliveryModule(this);
		this.orderOpsModule= new OrderOpsModule(this);
		this.inventoryOpsModule= new InventoryOpsModule(this);
		this.inventoryPlanModule = new InventoryPlanModule(this);
		this.forecastModule = new ForecastModule(this);
		
	}
	
	public abstract void plan();
	
	@ScheduledMethod(start=1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryOpsModule.prepareTick();
	}	
	
	@ScheduledMethod(start=1, interval = 1, priority = 8)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : this.upstrLinks){
			shipments = link.getArrivingShipments();
			orderOpsModule.processInShipments(shipments);
		}
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 6)
	public void placeOrders(){
		orderOpsModule.placeOrders();
		}
	
	@ScheduledMethod(start=1, interval = 1, priority = 4)
	public void fetchOrders(){
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
		deliveryModule.processOrders(newOrders);
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 2)
	public void dispatchShipments(){
		this.deliveryModule.dispatchShipments();
	}	
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		this.inventoryOpsModule.setInfinite(false);
		upstrLinks.add(b);
	}
	
	public DeliveryModule getDeliveryModule(){
		return this.deliveryModule;
	}
	
	public InventoryOpsModule getinventoryOpsModule(){
		return this.inventoryOpsModule;
	}
	
	public InventoryPlanModule getInventoryPlanModule(){
		return this.inventoryPlanModule;
	}
	
	public OrderOpsModule getOrderOpsModule(){
		return this.orderOpsModule;
	}
	
	public ForecastModule getForecastModule(){
		return this.forecastModule;
	}
	
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "\n";
		string += "   InventoryAgent: \n" 
				+ inventoryOpsModule.getInformationString();
		return string;
	}


}
